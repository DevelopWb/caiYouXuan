package com.juntai.wisdom.project.home.shop;

import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juntai.disabled.basecomponent.base.BaseResult;
import com.juntai.disabled.basecomponent.utils.ImageLoadUtil;
import com.juntai.wisdom.project.R;
import com.juntai.wisdom.project.base.BaseAppActivity;
import com.juntai.wisdom.project.home.HomePageContract;
import com.juntai.wisdom.project.beans.shop.ShopDetailBean;
import com.juntai.wisdom.project.utils.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @aouther tobato
 * @description 描述  店铺首页
 * @date 2022/5/8 8:50
 */
public class ShopActivity extends BaseAppActivity<ShopPresent> implements HomePageContract.IHomePageView, View.OnClickListener {

    private ImageView mShopBackIv;
    private ImageView mShopCollectIv;
    private ImageView mShopShareIv;
    private ConstraintLayout mTopCl;
    private ImageView mShopOwnerHeadIv;
    /**
     * 店铺名称
     */
    private TextView mShopNameTv;
    /**
     * 开店时间
     */
    private TextView mShopCreatTimeTv, mScoreTv;
    /**
     * 描述
     */
    private TextView mShopDesTv;
    private Banner mShopBanner;
    public int shopId;
    private int collectId = 0;

    private ShopDetailBean.DataBean shopBean;

    @Override
    protected ShopPresent createPresenter() {
        return new ShopPresent();
    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_shop;
    }

    @Override
    public void initView() {
        shopId = getIntent().getIntExtra(BASE_ID, 0);
        initToolbarAndStatusBar(false);
        mShopBackIv = (ImageView) findViewById(R.id.shop_back_iv);
        mShopBackIv.setOnClickListener(this);
        mShopCollectIv = (ImageView) findViewById(R.id.shop_collect_iv);
        mShopCollectIv.setOnClickListener(this);
        mShopShareIv = (ImageView) findViewById(R.id.shop_share_iv);
        mShopShareIv.setOnClickListener(this);
        mTopCl = (ConstraintLayout) findViewById(R.id.top_cl);
        mShopOwnerHeadIv = (ImageView) findViewById(R.id.shop_owner_head_iv);
        mShopNameTv = (TextView) findViewById(R.id.shop_name_tv);
        mShopCreatTimeTv = (TextView) findViewById(R.id.shop_creat_time_tv);
        mScoreTv = (TextView) findViewById(R.id.shop_score_tv);
        mShopDesTv = (TextView) findViewById(R.id.shop_des_tv);
        mShopBanner = (Banner) findViewById(R.id.shop_banner);
    }

    private void initBanner(ShopDetailBean.DataBean shopBean) {
        this.shopBean = shopBean;
        collectId = shopBean.getIsCollect();
        List<String> picVideos = new ArrayList<>();

        mShopBanner.isAutoPlay(false);
        mShopBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                // TODO: 2022/5/4 查看图片大图

            }
        });
        GlideImageLoader imageLoader = new GlideImageLoader().setOnFullScreenCallBack(new GlideImageLoader.OnFullScreenListener() {
            @Override
            public void onFullScreen() {

            }
        });
        if (!TextUtils.isEmpty(shopBean.getCameraUrl())) {
            picVideos.add(shopBean.getCameraUrl());
        }
        String bannerPic = shopBean.getShopImg();
        if (!TextUtils.isEmpty(bannerPic)) {
            if (bannerPic.contains(",")) {
                String[] pics = bannerPic.split(",");
                for (String pic : pics) {
                    picVideos.add(pic);
                }
            } else {
                picVideos.add(bannerPic);
            }
        }
        mShopBanner.setImages(picVideos).setImageLoader(imageLoader).start();
    }

    /**
     * 配置基本数据
     *
     * @param shopBean
     */
    public void initOwnerBaseInfo(ShopDetailBean.DataBean shopBean) {

        ImageLoadUtil.loadSquareImage(mContext, shopBean.getHeadPortrait(), mShopOwnerHeadIv);
        mShopNameTv.setText(shopBean.getName());
        mShopCreatTimeTv.setText("开店时间:" + shopBean.getCreateTime());
        mScoreTv.setText("店铺得分:" + shopBean.getShopFraction());
        mShopDesTv.setText(shopBean.getIntroduction());
        mShopCollectIv.setImageResource(shopBean.getIsCollect() > 0 ? R.mipmap.collected_icon : R.mipmap.un_collect_icon);
        initBanner(shopBean);

    }

    @Override
    public void initData() {

    }


    @Override
    public void onSuccess(String tag, Object o) {
switch (tag) {
    case HomePageContract.UN_COLLECT_COMMODITY_SHOP:
        collectId =0;
        mShopCollectIv.setImageResource(R.mipmap.un_collect_icon);

        break;
    case HomePageContract.COLLECT_COMMODITY_SHOP:
        BaseResult baseResult = (BaseResult) o;
        collectId = Integer.parseInt(baseResult.getMessage());
        mShopCollectIv.setImageResource( R.mipmap.collected_icon);

        break;
    default:
        break;
}
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.shop_back_iv:
                finish();
                break;
            case R.id.shop_collect_iv:
                if (collectId> 0) {
                    mPresenter.collectShop(getBaseBuilder()
                            .add("isCollect", "1")
                            .add("id", String.valueOf(collectId))
                            .add("shopId", String.valueOf(shopId)).build(), HomePageContract.UN_COLLECT_COMMODITY_SHOP
                    );

                }else {
                    //收藏
                    mPresenter.collectShop(getBaseBuilder()
                            .add("isCollect", "0")
                            .add("shopId", String.valueOf(shopId)).build(), HomePageContract.COLLECT_COMMODITY_SHOP
                    );
                }
                break;
            case R.id.shop_share_iv:
                // TODO: 2022/5/8 店铺分享
                break;
        }
    }
}