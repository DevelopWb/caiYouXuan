package com.juntai.wisdom.project.home.shop;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.juntai.disabled.basecomponent.utils.eventbus.EventBusObject;
import com.juntai.wisdom.project.AppHttpPathMall;
import com.juntai.wisdom.project.R;
import com.juntai.wisdom.project.base.BaseRecyclerviewFragment;
import com.juntai.wisdom.project.home.HomePageContract;
import com.juntai.wisdom.project.home.commodityfragment.CommodityListAdapter;
import com.juntai.wisdom.project.beans.CommodityBean;
import com.juntai.wisdom.project.beans.CommodityDesListBean;
import com.juntai.wisdom.project.beans.shop.ShopCommodityListBean;


import java.util.List;

/**
 * @Author: tobato
 * @Description: 作用描述  店铺里面的商品列表
 * @CreateDate: 2022/4/29 17:37
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/4/29 17:37
 */
public class ShopCommodityListFragment extends BaseRecyclerviewFragment<ShopPresent> implements HomePageContract.IHomePageView {

    private int labelId;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private LinearLayoutManager linearLayoutManager;

    public static ShopCommodityListFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("label", type);
        ShopCommodityListFragment fragment = new ShopCommodityListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        labelId = getArguments().getInt("label");


    }


    @Override
    protected BaseQuickAdapter getBaseQuickAdapter() {
        return new CommodityListAdapter(R.layout.shop_commodity_list);
    }

    @Override

    protected boolean enableLoadMore() {
        return true;
    }

    @Override
    protected ShopPresent createPresenter() {
        return new ShopPresent();
    }

    @Override
    protected void initView() {
        super.initView();

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(staggeredGridLayoutManager);

        baseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CommodityBean bean = (CommodityBean) adapter.getItem(position);
                getBaseAppActivity().startToCommodityDetail(bean.getId());


            }
        });

    }

    public void onEvent(EventBusObject eventBusObject) {
        switch (eventBusObject.getEventKey()) {
            case EventBusObject.CHANGE_COMMODITY_ARRAY_TYPE:
                boolean isLinearLayoutManager = (boolean) eventBusObject.getEventObj();
                if (isLinearLayoutManager) {
                    mRecyclerview.setLayoutManager(linearLayoutManager);
                    ((CommodityListAdapter)baseQuickAdapter).setLinearLayoutManager(true);
                } else {
                    mRecyclerview.setLayoutManager(staggeredGridLayoutManager);
                    ((CommodityListAdapter)baseQuickAdapter).setLinearLayoutManager(false);
                }


                break;
            default:
                break;
        }
    }


    @Override
    protected LinearLayoutManager getBaseAdapterManager() {
        return null;
    }

    @Override
    protected void getRvAdapterData() {
        // TODO: 2022/5/8 获取店铺内所有的商品信息

        mPresenter.getShopCommodityList(getBaseAppActivity().getBaseBuilderWithoutParama()
                .add("shopId", String.valueOf(((ShopActivity) getActivity()).shopId))
                .add("categoryId", String.valueOf(labelId)).build(), AppHttpPathMall.SHOP_COMMODITY_LIST);

    }

    @Override
    protected boolean enableRefresh() {
        return true;
    }


    @Override
    public void onSuccess(String tag, Object o) {
        super.onSuccess(tag, o);

        switch (tag) {
            case AppHttpPathMall.COMMODIFY_RECOMMEND:
                CommodityDesListBean desListBean = (CommodityDesListBean) o;
                if (desListBean != null) {
                    CommodityDesListBean.DataBean dataBean = desListBean.getData();
                    if (dataBean != null) {
                        List<CommodityBean> data = dataBean.getList();
                        setData(data, dataBean.getTotalCount());

                    }
                }
                break;
            case AppHttpPathMall.SHOP_COMMODITY_LIST:

                ShopCommodityListBean shopCommodityListBean = (ShopCommodityListBean) o;
                if (shopCommodityListBean != null) {
                    List<CommodityBean> arrrays = shopCommodityListBean.getData();
                    if (arrrays != null) {
                        baseQuickAdapter.setNewData(arrrays);
                    }
                }

                break;
            default:
                break;
        }
    }
}
