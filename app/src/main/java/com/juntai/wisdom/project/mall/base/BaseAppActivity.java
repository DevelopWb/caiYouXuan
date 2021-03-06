package com.juntai.wisdom.project.mall.base;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.example.app_basemodule.bean.order.CreatOrderBean;
import com.example.app_basemodule.bean.order.OrderDetailBean;
import com.example.app_basemodule.net.AppHttpPath;
import com.example.app_basemodule.utils.UserInfoManager;
import com.example.chat.base.uploadFile.UploadUtil;
import com.example.chat.base.uploadFile.listener.OnUploadListener;
import com.example.live_moudle.util.ObjectBoxUtil;
import com.juntai.disabled.basecomponent.base.BaseResult;
import com.juntai.disabled.basecomponent.bean.ContactBean;
import com.juntai.disabled.basecomponent.bean.LiveListBean;
import com.juntai.disabled.basecomponent.bean.UploadFileBean;
import com.juntai.disabled.basecomponent.bean.address.AddressListBean;
import com.juntai.disabled.basecomponent.bean.objectboxbean.MessageBodyBean;
import com.juntai.disabled.basecomponent.mvp.BasePresenter;
import com.juntai.disabled.basecomponent.utils.ActivityManagerTool;
import com.juntai.disabled.basecomponent.utils.HawkProperty;
import com.juntai.disabled.basecomponent.utils.MD5;
import com.juntai.disabled.basecomponent.utils.NotificationTool;
import com.juntai.disabled.basecomponent.utils.ToastUtils;
import com.juntai.disabled.basecomponent.utils.eventbus.EventBusObject;
import com.juntai.disabled.basecomponent.utils.eventbus.EventManager;
import com.juntai.disabled.bdmap.BaseRequestLocationActivity;
import com.juntai.disabled.bdmap.utils.NagivationUtils;
import com.juntai.wisdom.project.mall.R;
import com.juntai.wisdom.project.mall.entrance.LoginActivity;
import com.juntai.wisdom.project.mall.home.commodityfragment.commodity_detail.CommodityDetailActivity;
import com.juntai.wisdom.project.mall.home.shop.ShopActivity;
import com.juntai.wisdom.project.mall.mine.address.AddOrEditAddressActivity;
import com.juntai.wisdom.project.mall.mine.address.AddressListActivity;
import com.juntai.wisdom.project.mall.news.ChatActivity;
import com.juntai.wisdom.project.mall.order.allOrder.AllOrderActivity;
import com.juntai.wisdom.project.mall.order.confirmOrder.ConfirmOrderActivity;
import com.juntai.wisdom.project.mall.order.evaluate.EvaluateActivity;
import com.juntai.wisdom.project.mall.order.orderDetail.OrderDetailActivity;
import com.juntai.wisdom.project.mall.order.orderPay.OrderPayActivity;
import com.juntai.wisdom.project.mall.order.refund.RefundActivity;
import com.juntai.wisdom.project.mall.order.refund.RefundRequestActivity;
import com.juntai.wisdom.project.mall.search.SearchActivity;
import com.juntai.wisdom.project.mall.share.ShareActivity;
import com.juntai.wisdom.project.mall.utils.StringTools;
import com.juntai.wisdom.project.mall.webSocket.MyWsManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

/**
 * @aouther tobato
 * @description ??????
 * @date 2020/4/27 8:48  app?????????
 */
public abstract class BaseAppActivity<P extends BasePresenter> extends BaseRequestLocationActivity<P> {
    public static String WX_APPID = "wx5fd6d26f7806a119";
    public UploadUtil mUploadUtil;

    private OnFileUploadStatus onFileUploadStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        update(false);
        NotificationTool.SHOW_NOTIFICATION = true;
        getToolbar().setBackgroundResource(R.drawable.sp_filled_gray_lighter);
        initUploadUtil();
    }

    public void setOnFileUploadStatus(OnFileUploadStatus onFileUploadStatus) {
        this.onFileUploadStatus = onFileUploadStatus;
    }
    /**
     * ????????????????????????
     *
     * @param content
     */
    public void copy(String content) {
// ????????????????????????
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        ToastUtils.toast(mContext,"?????????");
    }
    protected void initUploadUtil() {
        //?????????????????????
        mUploadUtil = new UploadUtil();
        mUploadUtil.setOnUploadListener(new OnUploadListener() {
            @Override
            public void onAllSuccess() {
                ToastUtils.toast(mContext, "onAllSuccess");
            }

            @Override
            public void onAllFailed() {

            }

            @Override
            public void onThreadProgressChange(UploadFileBean uploadFileBean, int position, int percent) {
                Log.d("onThreadProgressChange", "onThreadProgressChange" + uploadFileBean.getFilePath() + "---------" + percent);

                if (onFileUploadStatus != null) {
                    onFileUploadStatus.onUploadProgressChange(uploadFileBean, percent);
                }
            }

            @Override
            public void onThreadFinish(UploadFileBean uploadFileBean, int position) {
                if (onFileUploadStatus != null) {
                    onFileUploadStatus.onUploadFinish(uploadFileBean);
                }
            }

            @Override
            public void onThreadInterrupted(int position) {

            }
        });
    }


    public interface OnFileUploadStatus {
        void onUploadProgressChange(UploadFileBean uploadFileBean, int percent);

        void onUploadFinish(UploadFileBean uploadFileBean);

    }

    /**
     * ????????????
     */
    public void reLogin(String regPhone) {
        UserInfoManager.clearUserData();//????????????
        //ws????????????
        MyWsManager.getInstance().disconnect();
        HawkProperty.clearRedPoint(mContext.getApplicationContext());
        ActivityManagerTool.getInstance().finishApp();
        startActivity(new Intent(this, LoginActivity.class).putExtra(BASE_STRING, regPhone
        ));
    }

    /**
     * ???????????????
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
     * ??????????????????
     *
     * @param messageBodyBean
     * @return
     */
    public String getSavedFileName(MessageBodyBean messageBodyBean) {
        String content = messageBodyBean.getContent();
        return getSavedFileName(content);
    }

    /**
     * ??????????????????
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
     * ??????????????????  ??????
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
        return AppHttpPath.APP_UPDATE;
    }

    /**
     * ??????
     *
     * @param endLatlng ?????????
     * @param endName   ???????????????
     */
    public void navigationLogic(LatLng endLatlng, String endName) {
        AlertDialog.Builder build = new AlertDialog.Builder(mContext);
        final String item_list[] = {"????????????", "????????????", "????????????"};
        build.setItems(item_list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (item_list[which]) {
                    case "????????????":
                        NagivationUtils.getInstant().openTencent(mContext, endLatlng.latitude, endLatlng.longitude,
                                endName);
                        break;
                    case "????????????":
                        NagivationUtils.getInstant().openGaoDeMap(mContext, endLatlng.latitude, endLatlng.longitude,
                                endName);
                        break;
                    case "????????????":
                        NagivationUtils.getInstant().openBaiduMap(mContext, endLatlng.latitude, endLatlng.longitude,
                                endName);
                        break;
                    default:
                        break;
                }
            }
        });
        build.setTitle("?????????????????????");
        AlertDialog alertDialog = build.create();
        alertDialog.show();
    }


    /**
     * ???????????????????????????
     */
    public void broadcasetRefreshHead() {
        Intent intent = new Intent();
        intent.setAction("action.refreshHead");
        sendBroadcast(intent);
    }

    /**
     * ??????builder
     *
     * @return
     */
    public FormBody.Builder getBaseBuilder() {
        FormBody.Builder builder = new FormBody.Builder()
                .add("account", UserInfoManager.getAccount())
                .add("token", UserInfoManager.getUserToken())
                .add("typeEnd", UserInfoManager.DEVICE_TYPE)
                .add("userId", String.valueOf(UserInfoManager.getUserId()));
        return builder;
    }

    /**
     * ??????builder
     *
     * @return
     */
    public FormBody.Builder getBaseBuilderWithoutParama() {
        FormBody.Builder builder = new FormBody.Builder();
        return builder;
    }
//    /**
//     * ??????builder
//     *
//     * @return
//     */
//    public FormBody.Builder getBaseBuilderWithoutUserId() {
//        FormBody.Builder builder = new FormBody.Builder();
//        builder.add("account", UserInfoManagerChat.getUserAccount());
//        builder.add("token", UserInfoManagerChat.getUserToken());
//        return builder;
//    }


    //    /**
    //     * ?????????????????????
    //     *
    //     * @return
    //     */
    //    public boolean isInnerAccount() {
    //        return UserInfoManagerChat.isTest();

    //    }

    @Override
    public void onLocationReceived(BDLocation bdLocation) {

    }

    /**
     * ???list???????????????????????????  ??????????????????
     *
     * @return
     */
    public String listToString(List<String> arrays) {
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
     * ??????????????????
     */
    public void copyTelephoneNum(String text) {
        //???????????????
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //??????ClipData??????
        //???????????????????????????????????????????????????
        //????????????????????????????????????????????????
        ClipData clip = ClipData.newPlainText("simple text", text);
        //??????clipdata??????.
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public boolean requestLocation() {
        return false;
    }


    /**
     * ????????????
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
     * ?????????????????????
     *
     * @param commodityId
     */
    public void startToCommodityDetail(int commodityId) {
        startActivityForResult(new Intent(mContext, CommodityDetailActivity.class)
                .putExtra(BASE_ID, commodityId), BASE_REQUEST_RESULT);
    }

    /**
     * ?????????????????????
     *
     * @param shopId
     */
    public void startToShop(int shopId) {
        startActivityForResult(new Intent(mContext, ShopActivity.class).putExtra(BASE_ID, shopId), BASE_REQUEST_RESULT);

    }

    /**
     * ?????????????????????
     * enterType 0 ???????????????????????????
     * 1 ??????????????????????????????
     * 2. ????????????????????????
     */
    public void startToOrderPayActivity(BaseResult orderListBean, int enterType) {
        startActivity(new Intent(mContext, OrderPayActivity.class)
                .putExtra(BASE_STRING, enterType)
                .putExtra(BASE_PARCELABLE, orderListBean));

    }

    /**
     * ?????????????????????
     */
    public void startToConfirmOrder(CreatOrderBean.DataBean dataBean) {
        startActivity(new Intent(mContext, ConfirmOrderActivity.class).putExtra(BASE_PARCELABLE, dataBean));

    }

    /**
     * ?????????????????????
     * type  1???????????????  0???????????????
     */
    public void startToAddressListActivity(int type) {
        startActivityForResult(new Intent(mContext, AddressListActivity.class).putExtra(BASE_ID, type), BASE_REQUEST_RESULT);


    }

    /**
     * ??????(????????????)
     */
    public void startToAddAddress(AddressListBean.DataBean dataBean) {
        startActivityForResult(new Intent(mContext, AddOrEditAddressActivity.class).putExtra(BASE_PARCELABLE, dataBean), BASE_REQUEST_RESULT);
    }

    /**
     * ????????????
     * enterType  0????????????????????????  1????????????????????????
     */
    public void startToAllOrderActivity(int enterType, int tabPosition) {
        startActivity(new Intent(mContext, AllOrderActivity.class)
                .putExtra(BASE_ID2, tabPosition)
                .putExtra(BASE_ID, enterType));
    }

    /**
     * @param orderId
     * @param orderStatus
     */
    public void startToOrderDetailActivity(int orderId, int orderStatus) {
        startActivity(new Intent(mContext, OrderDetailActivity.class)
                .putExtra(BASE_ID, orderId)
                .putExtra(BASE_ID2, orderStatus));
    }

    /**
     * ?????? ??????????????????
     */
    public void startToOrderRefundRequestActivity(OrderDetailBean orderDetailBean) {
        startActivity(new Intent(mContext, RefundRequestActivity.class)
                .putExtra(BASE_PARCELABLE, orderDetailBean)
        );

    }

    @Override
    public void onEvent(EventBusObject eventBusObject) {
        super.onEvent(eventBusObject);
        switch (eventBusObject.getEventKey()) {
            case EventBusObject.HANDLER_MESSAGE:
                MessageBodyBean messageBody = (MessageBodyBean) eventBusObject.getEventObj();
                if (mContext instanceof ChatActivity) {
                    EventManager.getEventBus().post(new EventBusObject(EventBusObject.MESSAGE_BODY, messageBody));
                } else {
                    ObjectBoxUtil.addMessage(messageBody);
                    EventManager.getEventBus().post(new EventBusObject(EventBusObject.REFRESH_NEWS_LIST, messageBody));

                }
                break;
            case EventBusObject.EVALUATE:
                if (this instanceof AllOrderActivity) {
                    OrderDetailBean.CommodityListBean commodityBean = (OrderDetailBean.CommodityListBean) eventBusObject.getEventObj();
                    startToEvaluateActivity(commodityBean);
                }
                break;
            case EventBusObject.CREAT_ORDER:
                CreatOrderBean.DataBean dataBean  = (CreatOrderBean.DataBean) eventBusObject.getEventObj();
                startToConfirmOrder(dataBean);
                break;
            case EventBusObject.LIVE_SHARE:
                LiveListBean.DataBean.ListBean bean = (LiveListBean.DataBean.ListBean) eventBusObject.getEventObj();
                ShareActivity.startShareActivity(mContext, 2,bean);

                break;
            default:
                break;
        }
    }

    /**
     * ?????? ??????
     */
    public void startToEvaluateActivity(OrderDetailBean.CommodityListBean commodityBean) {
        OrderDetailBean orderDetailBean = new OrderDetailBean();
        List<OrderDetailBean.CommodityListBean> listBeans = new ArrayList<>();
        listBeans.add(commodityBean);
        orderDetailBean.setShopName(commodityBean.getShopName());
        orderDetailBean.setCommodityList(listBeans);
        startActivity(new Intent(mContext, EvaluateActivity.class)
                .putExtra(BASE_PARCELABLE, orderDetailBean)
        );

    }

    /**
     * ?????? ??????
     * receivedStatus ??????????????????  1????????? 2 ??????
     */
    public void startToRefundActivity(OrderDetailBean orderDetailBean, int receivedStatus) {
        startActivity(new Intent(mContext, RefundActivity.class)
                .putExtra(BASE_ID, receivedStatus)
                .putExtra(BASE_PARCELABLE, orderDetailBean));
    }

    /**
     * ??????????????????
     */
    public void startToChatActivity(ContactBean contactBean) {
        startActivity(new Intent(mContext, ChatActivity.class)
                .putExtra(BASE_PARCELABLE, contactBean));
    }

    /**
     * ??????????????????
     */
    public void startToSearchActivity(int type) {
        SearchActivity.startSearchActivity(mContext,type);
    }

}
