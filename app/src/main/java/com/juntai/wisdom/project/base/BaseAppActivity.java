package com.juntai.wisdom.project.base;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.juntai.disabled.basecomponent.bean.address.AddressListBean;
import com.juntai.disabled.basecomponent.bean.objectboxbean.MessageBodyBean;
import com.juntai.disabled.basecomponent.utils.ActivityManagerTool;
import com.juntai.wisdom.project.AppHttpPathMall;
import com.juntai.disabled.basecomponent.mvp.BasePresenter;
import com.juntai.disabled.basecomponent.utils.MD5;
import com.juntai.disabled.basecomponent.utils.NotificationTool;
import com.juntai.wisdom.project.beans.order.CreatOrderBean;
import com.juntai.wisdom.project.beans.order.OrderDetailBean;
import com.juntai.wisdom.project.beans.order.OrderListBean;
import com.juntai.wisdom.project.entrance.LoginActivity;
import com.juntai.wisdom.project.home.commodityfragment.commodity_detail.CommodityDetailActivity;
import com.juntai.wisdom.project.order.confirmOrder.ConfirmOrderActivity;
import com.juntai.wisdom.project.order.allOrder.AllOrderActivity;
import com.juntai.wisdom.project.order.orderDetail.OrderDetailActivity;
import com.juntai.wisdom.project.order.orderPay.OrderPayActivity;
import com.juntai.wisdom.project.home.shop.ShopActivity;
import com.juntai.wisdom.project.beans.UserInfoManagerMall;
import com.juntai.disabled.bdmap.utils.NagivationUtils;
import com.juntai.wisdom.project.R;
import com.juntai.wisdom.project.base.selectPics.BaseSelectPicsActivity;
import com.juntai.wisdom.project.mine.address.AddOrEditAddressActivity;
import com.juntai.wisdom.project.mine.address.AddressListActivity;
import com.juntai.wisdom.project.utils.StringTools;


import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

/**
 * @aouther tobato
 * @description 描述
 * @date 2020/4/27 8:48  app的基类
 */
public abstract class BaseAppActivity<P extends BasePresenter> extends BaseSelectPicsActivity<P> {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationTool.SHOW_NOTIFICATION = true;
        getToolbar().setBackgroundResource(R.drawable.sp_filled_gray_lighter);
    }

    /**
     * 重新登录
     */
    public void reLogin(String  regPhone) {
        UserInfoManagerMall.clearUserData();//清理数据
        ActivityManagerTool.getInstance().finishApp();
        startActivity(new Intent(this, LoginActivity.class).putExtra(BASE_STRING,regPhone
        ));
    }

    /**
     * 第三方分享
     *
     * @return
     */
    public boolean initThirdShareLogic(Intent intent, Context context, Class cls) {
        if (intent != null) {
            String shareTitle = intent.getStringExtra("title");
            String shareUrl = intent.getStringExtra("shareUrl");
            String sharePic = intent.getStringExtra("picPath");
            String shareContent = intent.getStringExtra("content");
            String shareFromApp = intent.getStringExtra("shareFromApp");
            if (!TextUtils.isEmpty(shareUrl) && !TextUtils.isEmpty(shareTitle)) {
                Intent toIntent = new Intent();
                toIntent.putExtra("title", shareTitle);
                toIntent.putExtra("shareUrl", shareUrl);
                toIntent.putExtra("picPath", sharePic);
                toIntent.putExtra("content", shareContent);
                toIntent.putExtra("shareFromApp", shareFromApp);
                toIntent.setClass(context, cls);
                startActivity(toIntent);
                return true;
            }
        }
        return false;
    }


    /**
     * 获取文件名称
     *
     * @param messageBodyBean
     * @return
     */
    public String getSavedFileName(MessageBodyBean messageBodyBean) {
        String content = messageBodyBean.getContent();
        return getSavedFileName(content);
    }

    /**
     * 获取文件名称
     *
     * @return
     */
    public String getSavedFileName(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        if (content.contains("/")) {
            content = content.substring(content.lastIndexOf("/") + 1, content.length());
        }
        return content;
    }

    /**
     * 获取文件名称  后缀
     *
     * @param messageBodyBean
     * @return
     */
    public String getSavedFileNameWithoutSuffix(MessageBodyBean messageBodyBean) {
        String content = messageBodyBean.getContent();
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        if (content.contains("/")) {
            content = content.substring(content.lastIndexOf("/") + 1, content.lastIndexOf("."));
        }
        return content;
    }




    @Override
    protected boolean canCancelLoadingDialog() {
        return true;
    }

    @Override
    protected String getUpdateHttpUrl() {
        return AppHttpPathMall.APP_UPDATE;
    }

    /**
     * 导航
     *
     * @param endLatlng 目的地
     * @param endName   目的地名称
     */
    public void navigationLogic(LatLng endLatlng, String endName) {
        AlertDialog.Builder build = new AlertDialog.Builder(mContext);
        final String item_list[] = {"腾讯地图", "高德地图", "百度地图"};
        build.setItems(item_list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (item_list[which]) {
                    case "腾讯地图":
                        NagivationUtils.getInstant().openTencent(mContext, endLatlng.latitude, endLatlng.longitude,
                                endName);
                        break;
                    case "高德地图":
                        NagivationUtils.getInstant().openGaoDeMap(mContext, endLatlng.latitude, endLatlng.longitude,
                                endName);
                        break;
                    case "百度地图":
                        NagivationUtils.getInstant().openBaiduMap(mContext, endLatlng.latitude, endLatlng.longitude,
                                endName);
                        break;
                    default:
                        break;
                }
            }
        });
        build.setTitle("请选择导航方式");
        AlertDialog alertDialog = build.create();
        alertDialog.show();
    }


    /**
     * 发送更新头像的广播
     */
    public void broadcasetRefreshHead() {
        Intent intent = new Intent();
        intent.setAction("action.refreshHead");
        sendBroadcast(intent);
    }

    /**
     * 获取builder
     *
     * @return
     */
    public FormBody.Builder getBaseBuilder() {
        FormBody.Builder builder = new FormBody.Builder()
                .add("account", UserInfoManagerMall.getPhoneNumber())
                .add("token", UserInfoManagerMall.getUserToken())
                .add("typeEnd", UserInfoManagerMall.DEVICE_TYPE)
                .add("userId", String.valueOf(UserInfoManagerMall.getUserId()));
        return builder;
    }
    /**
     * 获取builder
     *
     * @return
     */
    public FormBody.Builder getBaseBuilderWithoutParama() {
        FormBody.Builder builder = new FormBody.Builder();
        return builder;
    }
//    /**
//     * 获取builder
//     *
//     * @return
//     */
//    public FormBody.Builder getBaseBuilderWithoutUserId() {
//        FormBody.Builder builder = new FormBody.Builder();
//        builder.add("account", ChatUserInfoManager.getUserAccount());
//        builder.add("token", ChatUserInfoManager.getUserToken());
//        return builder;
//    }


    //    /**
    //     * 是否是内部账号
    //     *
    //     * @return
    //     */
    //    public boolean isInnerAccount() {
    //        return ChatUserInfoManager.isTest();

    //    }

    @Override
    public void onLocationReceived(BDLocation bdLocation) {

    }

    /**
     * 将list中的数据转成字符串  并以逗号隔开
     *
     * @return
     */
    public String getListToString(List<String> arrays) {
        if (arrays == null || arrays.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(arrays.size());
        for (String selectedServicePeople : arrays) {
            sb.append(selectedServicePeople + ",");
        }
        String people = sb.toString();
        if (StringTools.isStringValueOk(people)) {
            people = people.substring(0, people.length() - 1);
        }
        return people;
    }

    /**
     * 复制电话号码
     */
    public void copyTelephoneNum(String text) {
        //获取剪贴版
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //创建ClipData对象
        //第一个参数只是一个标记，随便传入。
        //第二个参数是要复制到剪贴版的内容
        ClipData clip = ClipData.newPlainText("simple text", text);
        //传入clipdata对象.
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public boolean requestLocation() {
        return false;
    }


    /**
     * 加密密码
     *
     * @param pwd
     * @return
     */
    protected String encryptPwd(String account, String pwd) {
        return MD5.md5(String.format("%s#%s", account, pwd));
    }

    @Override
    protected void selectedPicsAndEmpressed(List<String> icons) {

    }


    @Override
    protected String getDownloadTitleRightName() {
        return null;
    }

    @Override
    protected String getDownLoadPath() {
        return null;
    }


    public String parseFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    public void onBackPressed() {
        hideKeyboard(mBaseRootCol);
        super.onBackPressed();
    }


    /**
     * 跳转到商品详情
     * @param commodityId
     */
    public   void  startToCommodityDetail(int commodityId){
        startActivityForResult(new Intent(mContext, CommodityDetailActivity.class)
                .putExtra(BASE_ID, commodityId),BASE_REQUEST_RESULT);
    }
    /**
     * 跳转到店铺首页
     * @param shopId
     */
    public   void  startToShop(int shopId){
        startActivityForResult(new Intent(mContext, ShopActivity.class).putExtra(BASE_ID,shopId),BASE_REQUEST_RESULT);

    }
    /**
     * 跳转到支付界面
     * enterType 0 代表直接购买的时候
     *  1 代表购物车结算的时候
     *  2. 在待支付订单进入
     */
    public   void  startToOrderPayActivity(OrderListBean orderListBean, int enterType){
        startActivity(new Intent(mContext, OrderPayActivity.class)
                .putExtra(BASE_STRING,enterType)
                .putExtra(BASE_PARCELABLE,orderListBean));

    }
    /**
     * 跳转到确认订单
     */
    public   void  startToConfirmOrder( CreatOrderBean.DataBean dataBean){
        startActivity(new Intent(mContext, ConfirmOrderActivity.class).putExtra(BASE_PARCELABLE,dataBean));

    }
    /**
     * 跳转到地址列表
     * type  1是选择地址  0是地址管理
     */
    public   void  startToAddressListActivity(int type){
        startActivityForResult(new Intent(mContext, AddressListActivity.class).putExtra(BASE_ID,type),BASE_REQUEST_RESULT);


    }

    /**
     * 添加(编辑地址)
     */
    public void startToAddAddress(AddressListBean.DataBean dataBean) {
        startActivityForResult(new Intent(mContext, AddOrEditAddressActivity.class).putExtra(BASE_PARCELABLE, dataBean), BASE_REQUEST_RESULT);
    }
    /**
     * 所有订单
     * enterType  0代表支付成功之后  1代表个人中心进入
     */
    public void startToAllOrderActivity(int enterType,int tabPosition) {
        startActivity(new Intent(mContext, AllOrderActivity.class)
                .putExtra(BASE_ID2,tabPosition)
                .putExtra(BASE_ID,enterType));
    }

    /**
     *
     * @param orderId
     * @param orderStatus
     */
    public void startToOrderDetailActivity(int orderId,int orderStatus) {
        startActivity(new Intent(mContext, OrderDetailActivity.class)
                .putExtra(BASE_ID,orderId)
                .putExtra(BASE_ID2,orderStatus));
    }

}