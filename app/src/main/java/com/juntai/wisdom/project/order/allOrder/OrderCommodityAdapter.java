package com.juntai.wisdom.project.order.allOrder;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.juntai.disabled.basecomponent.utils.ImageLoadUtil;
import com.juntai.wisdom.project.R;
import com.juntai.wisdom.project.beans.order.CreatOrderBean;
import com.juntai.wisdom.project.beans.order.OrderDetailBean;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2022/5/11 10:38
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/5/11 10:38
 */
public class OrderCommodityAdapter extends BaseQuickAdapter<OrderDetailBean.CommodityListBean, BaseViewHolder> {
    public OrderCommodityAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderDetailBean.CommodityListBean item) {
        ImageLoadUtil.loadSquareImage(mContext, item.getCoverImg(), helper.getView(R.id.commodity_pic_iv));
        helper.setText(R.id.commodity_name_tv, item.getCommodityName());
        helper.setText(R.id.commodity_property_tv, item.getCartInfo());
        helper.setText(R.id.all_price_tv, String.format("￥:%s",item.getPrices()));
        helper.setText(R.id.amount_tv, String.format("x%s",item.getCommodityNum()));

    }
}