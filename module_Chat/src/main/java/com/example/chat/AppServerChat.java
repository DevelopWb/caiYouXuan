package com.example.chat;


import com.example.chat.bean.UploadFileBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * responseBody里的数据只能调用(取出)一次，第二次为空。可赋值给新的变量使用
 */
public interface AppServerChat {

    /**
     * 上传文件
     * todo  这个地址需要更换线上的
     * @return
     */
    @POST("http://192.168.124.148:8080/jt-mall/uploadFile/upload")
    Observable<UploadFileBean> uploadFiles(@Body RequestBody requestBody);








}