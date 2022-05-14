package com.juntai.wisdom.project.utils;

import com.juntai.disabled.basecomponent.bean.objectboxbean.CommodityPropertyBean;
import com.juntai.disabled.basecomponent.bean.objectboxbean.CommodityPropertyBean_;
import com.juntai.wisdom.project.beans.CommodityDetailBean;
import com.juntai.disabled.basecomponent.utils.ObjectBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;
import io.objectbox.query.QueryCondition;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2022/5/7 9:26
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/5/7 9:26
 */
public class ObjectBoxMallUtil {


    /*====================================================    mall   ==============================================================*/

    /**
     * 添加商品属性到数据库
     */
    public static void addCommodityProperty(CommodityDetailBean.DataBean dataBean, List<CommodityPropertyBean> commodityPropertyBeans) {
        ObjectBox.get().boxFor(CommodityPropertyBean.class).removeAll();
        if (commodityPropertyBeans.size() > 0) {
            for (CommodityPropertyBean commodityPropertyBean : commodityPropertyBeans) {
                commodityPropertyBean.setShopId(dataBean.getShopId());
                commodityPropertyBean.setCommodityId(dataBean.getId());
                ObjectBox.get().boxFor(CommodityPropertyBean.class).put(commodityPropertyBean);
            }
        }

    }

    /**
     * 获取商品的图片
     */
    public static CommodityPropertyBean getCommodityProperty(CommodityDetailBean.DataBean dataBean, HashMap<String, String> propertyMap) {
        StringBuffer sb = new StringBuffer();
        String  sku = null;
        for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
            String content = entry.getValue();
            sb.append(content + ";");
        }
        sku = sb.toString().substring(0,sb.toString().length()-1);
        Box<CommodityPropertyBean> box = ObjectBox.get().boxFor(CommodityPropertyBean.class);
        QueryCondition<CommodityPropertyBean> condition = CommodityPropertyBean_.shopId.equal(dataBean.getShopId())
                .and(CommodityPropertyBean_.sku.equal(sku))
                .and(CommodityPropertyBean_.commodityId.equal(dataBean.getId()));
        List<CommodityPropertyBean> list = box.query(condition).build().find();
        if (list != null&&list.size()>0) {
            return list.get(0);
        }
        return null;
    }


}