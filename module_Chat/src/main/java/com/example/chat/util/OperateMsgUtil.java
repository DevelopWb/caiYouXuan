package com.example.chat.util;

import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juntai.disabled.basecomponent.bean.objectboxbean.MessageBodyBean;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2021-09-29 10:57
 * @UpdateUser: 更新者
 * @UpdateDate: 2021-09-29 10:57
 */
public class OperateMsgUtil {

    /**
     * 跳转类型
     */
    public static int TYPE = 888888;

    /**
     * 华为推送的时候 需要将推送相关的数据整理好之后发给服务端
     *
     * @return
     */
    public static String getHuaWeiPushIntentStr(MessageBodyBean messageBodyBean) {
        // TODO: 2022/5/6 华为推送点击事件
        Intent intent = null;
// Scheme协议（例如：pushscheme://com.huawei.codelabpush/deeplink?）需要您自定义
//        intent = new Intent(Intent.ACTION_VIEW);
//        if (messageBodyBean.getGroupId() > 0) {
//            //群组消息
//            intent.setData(Uri.parse("pushscheme://com.juntai.wisdom.im.groupchat/push?"));
//        } else {
//            if (messageBodyBean.getMsgType() == 4 || messageBodyBean.getMsgType() == 5) {
//                if (VideoRequestActivity.EVENT_CAMERA_REQUEST.equals(messageBodyBean.getEvent())) {
//                    intent.setData(Uri.parse("pushscheme://com.juntai.wisdom.im.videocall/push?"));
//                } else {
//                    intent.setData(Uri.parse("pushscheme://com.juntai.wisdom.im.privatechat/push?"));
//                }
//            } else {
//                intent.setData(Uri.parse("pushscheme://com.juntai.wisdom.im.privatechat/push?"));
//            }
//        }
//
//
//// 往intent中添加参数，用户可以根据自己的需求添加参数
//        intent.putExtra(BaseActivity.BASE_STRING, GsonTools.createGsonString(messageBodyBean));
//        intent.putExtra(VideoRequestActivity.IS_SENDER, false);
//// 应用必须带上该Flag，如果不添加该选项有可能会显示重复的消息
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        String intentUri = intent.toUri(Intent.URI_INTENT_SCHEME);

// 打印出的intentUri值就是设置到推送消息中intent字段的值
        return null;
    }

    /**
     * 华为推送的时候 需要将推送相关的数据整理好之后发给服务端
     *
     * @return
     */
    public static String getXiaomiPushIntentStr(MessageBodyBean messageBodyBean) {
        Intent intent = new Intent();
//// Scheme协议（例如：pushscheme://com.huawei.codelabpush/deeplink?）需要您自定义
//        ComponentName componentName = null;
//        if (messageBodyBean.getGroupId() > 0) {
//            //群组消息com.juntai.wisdom.im.chat_module.groupchat
//            componentName = new ComponentName("com.juntai.wisdom.im", "com.juntai.wisdom.im.chat_module.groupchat.GroupChatActivity");
//        } else {
//            if (messageBodyBean.getMsgType() == 4 || messageBodyBean.getMsgType() == 5) {
//                if (VideoRequestActivity.EVENT_CAMERA_REQUEST.equals(messageBodyBean.getEvent())) {
//                    componentName = new ComponentName("com.juntai.wisdom.im", "com.juntai.wisdom.im.chat_module.chat.videocall.VideoRequestActivity");
//                } else {
//                    componentName = new ComponentName("com.juntai.wisdom.im", "com.juntai.wisdom.im.chat_module.chat.PrivateChatActivity");
//                }
//            } else {
//                componentName = new ComponentName("com.juntai.wisdom.im", "com.juntai.wisdom.im.chat_module.chat.PrivateChatActivity");
//            }
//        }
//
//        intent.setComponent(componentName);//调用Intent的setComponent()方法实现传递
//
//// 往intent中添加参数，用户可以根据自己的需求添加参数
//        intent.putExtra(BaseActivity.BASE_STRING, GsonTools.createGsonString(messageBodyBean));
//
//// 应用必须带上该Flag，如果不添加该选项有可能会显示重复的消息
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        String intentUri = intent.toUri(Intent.URI_INTENT_SCHEME);
//
//// 打印出的intentUri值就是设置到推送消息中intent字段的值
        return null;
    }
//
//    /**
//     * 选中的 待编辑的消息
//     */
//    public static List<MultipleItem> selectedToEditMsgItems = new ArrayList<>();

    /**
     * 获取私聊文本消息
     *
     * @param toUserId
     * @param toUserAccout
     * @param toNickName
     * @param content
     * @param msgType      msgType":"消息类型（ 0：text；1：image；2：video；3：语音；4：直播）,
     * @return
     */
    public static MessageBodyBean getPrivateMsg(int msgType, int toUserId, String toUserAccout, String toNickName, String toHead, String content) {
        MessageBodyBean messageBody = new MessageBodyBean();
        messageBody.setContent(content);
        messageBody.setCreateTime(String.valueOf(System.currentTimeMillis()));
        messageBody.setFromAccount(ChatUserInfoManager.getUser().getAccount());
        messageBody.setFromNickname(ChatUserInfoManager.getUser().getNickname());
        messageBody.setFromHead(ChatUserInfoManager.getUser().getHeadPortrait());
        messageBody.setFromUserId(ChatUserInfoManager.getUserId());
        messageBody.setRead(true);
        messageBody.setToAccount(toUserAccout);
        messageBody.setToNickname(toNickName);
        messageBody.setToHead(toHead);
        messageBody.setToUserId(toUserId);
        messageBody.setMsgType(msgType);
        return messageBody;
    }


    public static MultipartBody.Builder getMsgBuilder(MessageBodyBean messageBodyBean) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token",ChatUserInfoManager.getUserToken())
                .addFormDataPart("account",ChatUserInfoManager.getAccount())
                .addFormDataPart("typeEnd","app_buy")
                .addFormDataPart("userId", String.valueOf(ChatUserInfoManager.getUserId()))
                .addFormDataPart("type", "1")
                .addFormDataPart("fromUserId", String.valueOf(messageBodyBean.getFromUserId()))
                .addFormDataPart("fromAccount", messageBodyBean.getFromAccount())
                .addFormDataPart("fromNickname", messageBodyBean.getFromNickname())
                .addFormDataPart("fromHead", messageBodyBean.getFromHead())
                .addFormDataPart("toUserId", String.valueOf(messageBodyBean.getToUserId()))
                .addFormDataPart("toAccount", TextUtils.isEmpty(messageBodyBean.getToAccount()) ? "0" : messageBodyBean.getToAccount())
                .addFormDataPart("toNickname", messageBodyBean.getToNickname())
                .addFormDataPart("rotation", messageBodyBean.getRotation())
                .addFormDataPart("toHead", messageBodyBean.getToHead())
                .addFormDataPart("content", messageBodyBean.getContent())
                .addFormDataPart("duration", messageBodyBean.getDuration())
                .addFormDataPart("videoCover", messageBodyBean.getVideoCover())
//                .addFormDataPart("hwPushIntentUrl", OperateMsgUtil.getHuaWeiPushIntentStr(messageBodyBean))
//                .addFormDataPart("xiaomiPushIntentUrl", OperateMsgUtil.getXiaomiPushIntentStr(messageBodyBean))
                .addFormDataPart("msgType", String.valueOf(messageBodyBean.getMsgType()));
        if (messageBodyBean.getFromUserId() == ChatUserInfoManager.getUserId()) {
            //我发送的信息
            //收藏的时候需要上传
            builder.addFormDataPart("localCatchPath", String.valueOf(messageBodyBean.getLocalCatchPath()));
        }
        return builder;

    }

    /**
     * 获取各种消息类型展示的内容
     *0：text；1：image；2：video；3：语音；4：直播
     * @param messageBodyBean
     * @return
     */
    public static String getContent(MessageBodyBean messageBodyBean) {
        switch (messageBodyBean.getMsgType()) {
            case 0:
                return String.format("%s", messageBodyBean.getContent());
            case 1:
                return String.format("%s", "[图片]");
            case 2:
                return String.format("%s", "[视频]");
            case 3:
                return String.format("%s", "[语音]");
            case 4:
                return String.format("%s", "[直播]");

            default:
                break;
        }
        return null;
    }

    public static List<MessageBodyBean> changeGsonToList(String gsonString) {
        Gson gson = new Gson();
        List<MessageBodyBean> list = gson.fromJson(gsonString, new TypeToken<List<MessageBodyBean>>() {
        }.getType());
        return list;
    }
}
