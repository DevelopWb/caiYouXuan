package com.example.app_basemodule.net;


import com.example.app_basemodule.bean.AroundShopBean;
import com.example.app_basemodule.bean.CartListBean;
import com.example.app_basemodule.bean.CitysBean;
import com.example.app_basemodule.bean.CollectDataBean;
import com.example.app_basemodule.bean.CommodityDesListBean;
import com.example.app_basemodule.bean.CommodityDetailBean;
import com.example.app_basemodule.bean.CommodityEvaluationBean;
import com.example.app_basemodule.bean.IdNameBean;
import com.example.app_basemodule.bean.LiveResultBean;
import com.example.app_basemodule.bean.NewsListBean;
import com.example.app_basemodule.bean.PlayUrlBean;
import com.example.app_basemodule.bean.ShopListDataBean;
import com.example.app_basemodule.bean.UserBeanMall;
import com.example.app_basemodule.bean.order.ConfirmOrderBean;
import com.example.app_basemodule.bean.order.CreatOrderBean;
import com.example.app_basemodule.bean.order.OrderDetailDataBean;
import com.example.app_basemodule.bean.order.OrderListBean;
import com.example.app_basemodule.bean.order.OrderPayWxBean;
import com.example.app_basemodule.bean.order.OrderPayZfbBean;
import com.example.app_basemodule.bean.order.OrderStatusAmountBean;
import com.example.app_basemodule.bean.order.RefundReasonBean;
import com.juntai.disabled.basecomponent.base.BaseResult;
import com.juntai.disabled.basecomponent.bean.LiveListBean;
import com.juntai.disabled.basecomponent.bean.LiveTypeListBean;
import com.juntai.disabled.basecomponent.bean.OpenLiveBean;
import com.juntai.disabled.basecomponent.bean.UploadFileBean;
import com.juntai.disabled.basecomponent.bean.address.AddressListBean;
import com.juntai.disabled.basecomponent.bean.objectboxbean.MessageListBean;
import com.juntai.disabled.basecomponent.bean.shop.ShopCommodityListBean;
import com.juntai.disabled.basecomponent.bean.shop.ShopDetailBean;
import com.juntai.disabled.basecomponent.bean.weather.CityBean;
import com.juntai.disabled.basecomponent.bean.weather.ResponseForcastWeather;
import com.juntai.disabled.basecomponent.bean.weather.ResponseRealTimeWeather;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * responseBody????????????????????????(??????)?????????????????????????????????????????????????????????
 */
public interface AppServer {

    /**
     * ??????id   ???????????????
     *
     * @param sessionid
     * @return
     */
    @GET(AppHttpPath.BASE_CAMERA_URL + "/vss/video_keepalive/{sessionid}")
    Observable<OpenLiveBean> keepAlive(@Path("sessionid") String sessionid);

    /**
     * ???????????????
     *
     * @return
     */
    @POST(AppHttpPath.STREAM_OPE_ADDR)
    Observable<PlayUrlBean> openStream(@Body RequestBody requestBody);

    @POST(AppHttpPath.GET_SHOPES_AROUND)
    Observable<AroundShopBean> getAroundShopes(@Body RequestBody requestBody);


    /*====================================================    ????????????   ==============================================================*/
    @POST(AppHttpPath.ADDR_LIST)
    Observable<AddressListBean> getAddrList(@Body RequestBody requestBody);

    @POST(AppHttpPath.SET_DEFAULT_ADDR)
    Observable<BaseResult> setDefaultAdddr(@Body RequestBody requestBody);

    @POST(AppHttpPath.ADD_OR_EDIT_ADDR)
    Observable<BaseResult> addOrEditAddrList(@Body RequestBody requestBody);

    @POST(AppHttpPath.DELETE_ADDR)
    Observable<BaseResult> deleteAddr(@Query("account") String account, @Query("token") String token, @Query("typeEnd") String typeEnd, @Query("id") List<Integer> ids);




    /*====================================================    ??????   ==============================================================*/


    //????????????
    @POST(AppHttpPath.REALTIME_WEATHER)
    Observable<ResponseRealTimeWeather> getWeatherRealtime(@Query("longitude") String longitude, @Query("latitude") String latitude);

    //????????????
    @POST(AppHttpPath.FORCAST_WEATHER)
    Observable<ResponseForcastWeather> getForcast(@Query("longitude") String longitude, @Query("latitude") String latitude);

    @POST(AppHttpPath.PROVINCE)
    Observable<CityBean> getProvince();

    @POST(AppHttpPath.CITY)
    Observable<CityBean> getCity(@Query("cityNum") int cityNum);

    @POST(AppHttpPath.AREA)
    Observable<CityBean> getArea(@Query("cityNum") int cityNum);

    @POST(AppHttpPath.STREET)
    Observable<CityBean> getStreet(@Query("cityName") int townNum);




    /*====================================================    ????????????   ==============================================================*/


    @POST(AppHttpPath.COMMODIFY_LABELS)
    Observable<IdNameBean> getCommodityLaBels();


    @POST(AppHttpPath.COMMODIFY_RECOMMEND)
    Observable<CommodityDesListBean> getCommodityRecommendList(@Body RequestBody requestBody);

    @POST(AppHttpPath.COMMODIFY_DETAIL)
    Observable<CommodityDetailBean> getCommodityDetail(@Body RequestBody requestBody);

    @POST(AppHttpPath.COMMODIFY_EVALUATION)
    Observable<CommodityEvaluationBean> getCommodityEvaluation(@Body RequestBody requestBody);



    /*====================================================    ????????????   ==============================================================*/


    @POST(AppHttpPath.SHOP_DETAIL)
    Observable<ShopDetailBean> getShopDetail(@Body RequestBody requestBody);

    @POST(AppHttpPath.SHOP_COMMODITY_LIST)
    Observable<ShopCommodityListBean> getShopCommodityList(@Body RequestBody requestBody);


    @POST(AppHttpPath.SHOP_COLLECT)
    Observable<BaseResult> collectShop(@Body RequestBody requestBody);


    @POST(AppHttpPath.COMMODITY_COLLECT)
    Observable<BaseResult> collectCommodity(@Body RequestBody requestBody);


    @POST(AppHttpPath.SHOP_COLLECT_LIST)
    Observable<CollectDataBean> getShopCollectList(@Body RequestBody requestBody);

    @POST(AppHttpPath.COMMODITY_COLLECT_LIST)
    Observable<CollectDataBean> getCommodityCollectList(@Body RequestBody requestBody);



    /*====================================================    ?????????   ==============================================================*/


    @POST(AppHttpPath.EDIT_CART)
    Observable<BaseResult> editCart(@Body RequestBody requestBody);

    @POST(AppHttpPath.CART_LIST)
    Observable<CartListBean> getCartList(@Body RequestBody requestBody);

    @POST(AppHttpPath.DELETE_CART_COMMODITY)
    Observable<BaseResult> deleteCartCommodity(@Query("account") String account, @Query("token") String token, @Query("typeEnd") String typeEnd, @Query("id") List<Integer> ids);


    @GET(AppHttpPath.ALL_CITYS)
    Observable<CitysBean> getAllCitys(@Query("keywords") String keywords, @Query("subdistrict") String subdistrict, @Query("key") String key);


    /**
     * ????????????
     *
     * @return
     */
    @POST(AppHttpPath.LOGIN)
    Observable<UserBeanMall> login(@Body RequestBody requestBody);


    /**
     * ??????
     *
     * @return
     */
    @POST(AppHttpPath.REGIST)
    Observable<UserBeanMall> regist(@Body RequestBody requestBody);

    /**
     * ????????????
     *
     * @return
     */
    @POST(AppHttpPath.UPLOAD_FILES)
    Observable<UploadFileBean> uploadFiles(@Body RequestBody requestBody);

    @POST(AppHttpPath.MODIFY_PWD)
    Observable<BaseResult> modifyPwd(@Body RequestBody requestBody);

    /**
     * account  ?????????
     *
     * @return
     */
    @GET(AppHttpPath.GET_SMS_CODE + "/{phoneNumber}")
    Observable<BaseResult> getSMSCode(@Path("phoneNumber") String path);






    /*====================================================    ????????????   ==============================================================*/


    /**
     * account  ?????????
     *
     * @return
     */
    @POST(AppHttpPath.GET_USER_INFO)
    Observable<UserBeanMall> getUserInfo(@Body RequestBody requestBody);

    /**
     * ????????????
     *
     * @param requestBody
     * @return
     */
    @POST(AppHttpPath.MODIFY_USER_ACCOUNT)
    Observable<BaseResult> modifyAccount(@Body RequestBody requestBody);


    @POST(AppHttpPath.COMMIT_SUGGESTION)
    Observable<BaseResult> commitSuggestion(@Body RequestBody requestBody);

    /**
     * ??????????????????
     *
     * @param requestBody
     * @return
     */
    @POST(AppHttpPath.MODIFY_USER_INFO)
    Observable<BaseResult> modifyUserInfo(@Body RequestBody requestBody);

    @POST(AppHttpPath.LOGOUT)
    Observable<BaseResult> logout(@Body RequestBody requestBody);



    /*====================================================    ????????????   ==============================================================*/

    @POST(AppHttpPath.CREAT_ORDER_CART)
    Observable<CreatOrderBean> creatOrderCart(@Body RequestBody requestBody);

    @POST(AppHttpPath.CREAT_ORDER_BUY)
    Observable<CreatOrderBean> creatOrderBuy(@Body RequestBody requestBody);

    @POST(AppHttpPath.COMMIT_ORDER)
    Observable<ConfirmOrderBean> commitOrder(@Body RequestBody requestBody);

    @POST(AppHttpPath.CANCEL_ORDER)
    Observable<BaseResult> cancelOrder(@Body RequestBody requestBody);

    @POST(AppHttpPath.NOTICE_SEND)
    Observable<BaseResult> noticeSend(@Body RequestBody requestBody);

    @POST(AppHttpPath.DELETE_CANCEL_ORDER)
    Observable<BaseResult> deleteCancelOrder(@Body RequestBody requestBody);

    @POST(AppHttpPath.REQUEST_REFUND)
    Observable<BaseResult> requestRefund(@Body RequestBody requestBody);

    @POST(AppHttpPath.START_EVALUATE)
    Observable<BaseResult> startEvaluate(@Body RequestBody requestBody);


    @POST(AppHttpPath.CONFIRM_RECEIVED)
    Observable<BaseResult> confirmReceived(@Body RequestBody requestBody);

    @POST(AppHttpPath.ORDER_STATUS)
    Observable<BaseResult> getOrderStatus(@Body RequestBody requestBody);


    @POST(AppHttpPath.ORDER_LIST)
    Observable<OrderListBean> getOrderList(@Body RequestBody requestBody);

    @POST(AppHttpPath.ORDER_DETAIL)
    Observable<OrderDetailDataBean> getOrderDetail(@Body RequestBody requestBody);

    @POST(AppHttpPath.ORDER_STATUS_AMOUNT)
    Observable<OrderStatusAmountBean> getOrderStatusAmount(@Body RequestBody requestBody);

    @POST(AppHttpPath.GET_REFUND_REASON)
    Observable<RefundReasonBean> getRefundReasons(@Body RequestBody requestBody);


    @POST(AppHttpPath.ORDER_PAY_PUB_WEIXIN)
    Observable<OrderPayWxBean> payByWeixin(@Body RequestBody requestBody);

    @POST(AppHttpPath.ORDER_PAY_ZHIFUBAO)
    Observable<OrderPayZfbBean> payByZhifubao(@Body RequestBody requestBody);

    @POST(AppHttpPath.ORDER_PAY_PUB_ACCOUNT)
    Observable<BaseResult> payByPubAccount(@Body RequestBody requestBody);






    /*====================================================    ??????   ==============================================================*/

    @POST(AppHttpPath.SEND_MSG)
    Observable<BaseResult> sendMessage(@Body RequestBody requestBody);

    @POST(AppHttpPath.MESSAGE_READ)
    Observable<BaseResult> messageRead(@Body RequestBody requestBody);

    @POST(AppHttpPath.UNREAD_CONTACT_MSG)
    Observable<MessageListBean> getContactUnreadMsg(@Body RequestBody requestBody);

    @POST(AppHttpPath.NEWS_LIST)
    Observable<NewsListBean> getNewsList(@Body RequestBody requestBody);

    @POST(AppHttpPath.SEARCH_COMMODITY)
    Observable<CommodityDesListBean> startSearchCommodity(@Body RequestBody requestBody);

    @POST(AppHttpPath.SEARCH_SHOP)
    Observable<ShopListDataBean> startSearchShop(@Body RequestBody requestBody);





    /*====================================================    ??????   ==============================================================*/

    @POST(AppHttpPath.GET_LIVE_TYPE)
    Observable<LiveTypeListBean> getLiveType();

    @POST(AppHttpPath.GET_LIVE_LIST)
    Observable<LiveListBean> getLiveList(@Body RequestBody requestBody);


    /**
     * ????????????
     * @param requestBody
     * @return
     */
    @POST(AppHttpPath.START_LIVE)
    Observable<LiveResultBean> startLive(@Body RequestBody requestBody);

    /**
     * ????????????
     *
     * @return
     */
    @POST(AppHttpPath.LIVE_ROOM_COMMODITIES)
    Observable<ShopCommodityListBean> getLiveRoomCommodities(@Body RequestBody requestBody);



}