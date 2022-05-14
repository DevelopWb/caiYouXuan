package com.juntai.wisdom.project.order.orderPay;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.juntai.wisdom.project.AppHttpPathMall;
import com.juntai.wisdom.project.R;
import com.juntai.wisdom.project.base.BaseRecyclerviewActivity;
import com.juntai.wisdom.project.beans.PicTextBean;
import com.juntai.wisdom.project.beans.UserInfoManagerMall;
import com.juntai.wisdom.project.beans.order.OrderDetailBean;
import com.juntai.wisdom.project.beans.order.OrderListBean;
import com.juntai.wisdom.project.home.HomePageContract;
import com.juntai.wisdom.project.order.OrderPresent;
import com.juntai.wisdom.project.utils.CalendarUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @aouther tobato
 * @description 描述 订单支付
 * @date 2022/5/11 14:11
 */
public class OrderPayActivity extends BaseRecyclerviewActivity<OrderPresent> implements HomePageContract.IHomePageView, View.OnClickListener {

    /**
     * 支付剩余时间
     */
    private TextView mOrderLeftTimeTv;
    /**
     * 200
     */
    private TextView mOrderTotalPriceTv;
    /**
     * 确认支付
     */
    private TextView mPayTv;
    private OrderListBean orderListBean;
    //     0 代表直接购买的时候
//     *  1 代表购物车结算的时候
//     *  2. 在待支付订单进入
    private int enterType;
    //0 代表支付宝  1代表 微信 2代表 公户
    private int payType = 1;


    private long minute = 15;//这是分钟

    private long second = 0;//这是分钟后面的秒数。这里是以30分钟为例的，所以，minute是30，second是0


    private Timer timer;

    private TimerTask timerTask;

//这是接收回来处理的消息

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            if (minute == 0) {
                if (second == 0) {
                    mOrderLeftTimeTv.setText("结束");
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    if (timerTask != null) {
                        timerTask = null;
                    }
                    finish();
                } else {
                    second--;
                    if (second >= 10) {
                        mOrderLeftTimeTv.setText("0" + minute + ":" + second);
                    } else {
                        mOrderLeftTimeTv.setText("0" + minute + ":0" + second);
                    }
                }
            } else {
                if (second == 0) {
                    second = 59;
                    minute--;
                    if (minute >= 10) {
                        mOrderLeftTimeTv.setText(minute + ":" + second);

                    } else {
                        mOrderLeftTimeTv.setText("0" + minute + ":" + second);

                    }

                } else {
                    second--;

                    if (second >= 10) {
                        if (minute >= 10) {
                            mOrderLeftTimeTv.setText(minute + ":" + second);

                        } else {
                            mOrderLeftTimeTv.setText("0" + minute + ":" + second);

                        }

                    } else {
                        if (minute >= 10) {
                            mOrderLeftTimeTv.setText(minute + ":0" + second);

                        } else {
                            mOrderLeftTimeTv.setText("0" + minute + ":0" + second);

                        }

                    }

                }

            }

        }

    };


    @Override
    protected OrderPresent createPresenter() {
        return new OrderPresent();
    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_order_pay;
    }

    @Override
    public void initView() {
        super.initView();
        mOrderLeftTimeTv = (TextView) findViewById(R.id.order_left_time_tv);
        mOrderTotalPriceTv = (TextView) findViewById(R.id.order_total_price_tv);

        mPayTv = (TextView) findViewById(R.id.pay_tv);
        mPayTv.setOnClickListener(this);
        orderListBean = getIntent().getParcelableExtra(BASE_PARCELABLE);
        enterType = getIntent().getIntExtra(BASE_STRING, 0);
        // : 2022/5/13 剩余时间 倒计时
        if (2 != enterType) {
            //15分钟倒计时
            minute = 15;
        } else {
            //计算出还剩多少分钟再倒计时
            List<OrderDetailBean> detailBeans = orderListBean.getData();
            if (detailBeans != null && !detailBeans.isEmpty()) {
                OrderDetailBean detailBean = detailBeans.get(0);
                String endTime = detailBean.getExpireTime();
                if (!TextUtils.isEmpty(endTime)) {
                    long currentStamp = CalendarUtil.getStringTimestamp(CalendarUtil.getCurrentTime());
                    long entTimeLong = CalendarUtil.getStringTimestamp(endTime);
                    long time = entTimeLong - currentStamp;
                    if (time < 3600) {
                        minute = time / 60;
                        if (time % 60 != 0) {
                            second = time % 60;
                        }
                    }
                }
            }
        }
        mOrderTotalPriceTv.setText(String.format("¥ %s", orderListBean.getTotalPrice()));
        baseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<PicTextBean> arrays = adapter.getData();
                payType = position;
                for (int i = 0; i < arrays.size(); i++) {
                    PicTextBean picTextBean = arrays.get(i);
                    if (i == position) {
                        picTextBean.setSelect(true);
                    } else {
                        picTextBean.setSelect(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        timerTask = new TimerTask() {
            @Override

            public void run() {
                Message msg = new Message();

                msg.what = 0;

                handler.sendMessage(msg);

            }

        };

        timer = new Timer();

        timer.schedule(timerTask, 0, 1000);

    }


    @Override
    protected LinearLayoutManager getBaseAdapterManager() {
        return null;
    }

    @Override
    protected void getRvAdapterData() {
        List<PicTextBean> arrays = new ArrayList<>();
        arrays.add(new PicTextBean(R.mipmap.zhifubao_icon, PicTextBean.PAY_TYPE_ZHIFUBAO, false));
        arrays.add(new PicTextBean(R.mipmap.weixin_icon, PicTextBean.PAY_TYPE_WEIXIN, true));
        arrays.add(new PicTextBean(R.mipmap.bank_icon, PicTextBean.PAY_TYPE_PUB_ACCOUNT + UserInfoManagerMall.getSchoolName(), false));
        baseQuickAdapter.setNewData(arrays);
    }

    @Override
    protected boolean enableRefresh() {
        return false;
    }

    @Override
    protected boolean enableLoadMore() {
        return false;
    }

    @Override
    protected BaseQuickAdapter getBaseQuickAdapter() {
        return new OrderPayTypeAdapter(R.layout.mall_pay_type_item);
    }

    @Override
    public void onSuccess(String tag, Object o) {
        switch (tag) {
            case AppHttpPathMall.ORDER_PAY_PUB_ACCOUNT:
                startActivity(new Intent(mContext, PaySuccessActivity.class));
                finish();
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
            case R.id.pay_tv:

                switch (payType) {
                    case 0:
                        // TODO: 2022/5/11 支付宝支付
                        break;
                    case 1:
                        // TODO: 2022/5/11 微信支付
                        break;
                    case 2:
                        // : 2022/5/11 公户支付
                        List<Integer> ids = new ArrayList<>();
                        List<OrderDetailBean> datas = orderListBean.getData();
                        for (OrderDetailBean orderDetailBean : datas) {
                            ids.add(orderDetailBean.getId());
                        }
                        mPresenter.payByPubAccount(ids, AppHttpPathMall.ORDER_PAY_PUB_ACCOUNT);
                        break;
                    default:
                        break;
                }

                break;
        }
    }


    @Override
    public void onBackPressed() {

        showAlertDialog("您的订单还未支付", "继续支付", "放弃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
    }


    @Override

    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();

            timer = null;

        }

        if (timerTask != null) {
            timerTask = null;

        }

        minute = -1;

        second = -1;

        super.onDestroy();

    }
}