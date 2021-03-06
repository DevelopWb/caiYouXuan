package com.juntai.wisdom.project.mall.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.app_basemodule.net.AppHttpPath;
import com.example.app_basemodule.utils.UserInfoManager;
import com.example.chat.MainContract;
import com.example.chat.R;
import com.example.chat.bean.HomePageMenuBean;
import com.example.chat.chatmodule.ChatAdapter;
import com.example.chat.chatmodule.ChatMoreActionAdapter;
import com.example.chat.chatmodule.ChatPresent;
import com.example.chat.chatmodule.EditChatMsgAdapter;
import com.example.chat.util.MultipleItem;
import com.example.chat.util.OperateMsgUtil;
import com.example.live_moudle.util.ObjectBoxUtil;
import com.juntai.disabled.basecomponent.base.BaseActivity;
import com.juntai.disabled.basecomponent.bean.ContactBean;
import com.juntai.disabled.basecomponent.bean.MyMenuBean;
import com.juntai.disabled.basecomponent.bean.UploadFileBean;
import com.juntai.disabled.basecomponent.bean.objectboxbean.FileBaseInfoBean;
import com.juntai.disabled.basecomponent.bean.objectboxbean.MessageBodyBean;
import com.juntai.disabled.basecomponent.bean.objectboxbean.MessageListBean;
import com.juntai.disabled.basecomponent.utils.CalendarUtil;
import com.juntai.disabled.basecomponent.utils.DisplayUtil;
import com.juntai.disabled.basecomponent.utils.FileCacheUtils;
import com.juntai.disabled.basecomponent.utils.ImageLoadUtil;
import com.juntai.disabled.basecomponent.utils.LogUtil;
import com.juntai.disabled.basecomponent.utils.NotificationTool;
import com.juntai.disabled.basecomponent.utils.RxScheduler;
import com.juntai.disabled.basecomponent.utils.RxTask;
import com.juntai.disabled.basecomponent.utils.ToastUtils;
import com.juntai.disabled.basecomponent.utils.UrlFormatUtil;
import com.juntai.disabled.basecomponent.utils.eventbus.EventBusObject;
import com.juntai.disabled.basecomponent.widght.BaseBottomDialog;
import com.juntai.disabled.bdmap.act.LocateSelectionActivity;
import com.juntai.wisdom.project.mall.base.BaseAppActivity;
import com.juntai.wisdom.project.mall.base.displayPicVideo.PicVideoDisplayActivity;
import com.juntai.wisdom.project.mall.home.HomePageContract;
import com.juntai.wisdom.project.mall.utils.bannerImageLoader.BannerObject;
import com.negier.emojifragment.bean.Emoji;
import com.negier.emojifragment.fragment.EmojiFragment;
import com.negier.emojifragment.util.EmojiUtils;
import com.zyl.chathelp.audio.AudioPlayManager;
import com.zyl.chathelp.audio.AudioRecordManager;
import com.zyl.chathelp.audio.IAudioPlayListener;
import com.zyl.chathelp.audio.IAudioRecordListener;
import com.zyl.chathelp.utils.EmotionKeyboard;
import com.zyl.chathelp.video.CameraActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @aouther tobato
 * @description ??????  ????????????
 * @date 2022/5/19 14:48
 */
public class ChatActivity extends BaseAppActivity<NewsPresent> implements View.OnClickListener,
        HomePageContract.IHomePageView, EmojiFragment.OnEmojiClickListener, BaseAppActivity.OnFileUploadStatus {

    private ContactBean contactBean;
    private BaseBottomDialog.OnItemClick onItemClick;

    private RecyclerView mRecyclerview;
    private ChatAdapter chatAdapter;
    //    private List<PrivateMsgBean> news = new ArrayList<>();//???????????????
    private ImageView mIvAudio;
    private EditText mContentEt;
    /**
     * ?????? ??????
     */
    private Button mBtnAudio;
    private ImageView mIvEmo;
    private ImageView mIvMore;
    /**
     * ??????
     */
    private TextView mTvSend;
    private FrameLayout mChatFl;
    private LinearLayout mLlRoot;
    private Fragment mEmojiFg;
    private EmotionKeyboard mEmotionKeyboard;
    private LinearLayout mContentLl;
    private RecyclerView mMoreActionRv;
    private LinearLayout mEmojiLl;
    private int mDuration = 0;
    public final static int REQUEST_TAKE_PHOTO = 1001;

    private ChatMoreActionAdapter moreActionAdapter;

    //@?????????
    List<Integer> atUsers = new ArrayList<>();
    private TextView mQuoteContentTv;
    private ImageView mCloseQuoteIv;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        allPicVideoPath.clear();
        initAdapterData(intent);

    }

    /**
     * ????????????????????????
     */
    ArrayList<BannerObject> allPicVideoPath = new ArrayList<>();


    @Override
    protected NewsPresent createPresenter() {
        return new NewsPresent();
    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_chat;
    }

    @Override
    public void initView() {
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (MotionEvent.ACTION_MOVE == action) {
                    hideBottomAndKeyboard();
                    if (mEmotionKeyboard != null) {
                        if (mEmotionKeyboard.isSoftInputShown()) {
                            mEmotionKeyboard.hideSoftInput();
                        }
                    }
                    mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);

                }
                return false;
            }
        });
        chatAdapter = new ChatAdapter(null);
        LinearLayoutManager managere = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //?????????????????????????????? ?????????????????????????????? ????????????
        managere.setStackFromEnd(true);
        mRecyclerview.setLayoutManager(managere);
        mRecyclerview.setAdapter(chatAdapter);
        mIvAudio = (ImageView) findViewById(R.id.ivAudio);
        mIvAudio.setOnClickListener(this);
        mContentEt = (EditText) findViewById(R.id.content_et);
        mContentLl = (LinearLayout) findViewById(R.id.llContent);
        mBtnAudio = (Button) findViewById(R.id.btnAudio);
        mIvEmo = (ImageView) findViewById(R.id.ivEmo);
        mIvMore = (ImageView) findViewById(R.id.ivMore);
        mTvSend = (TextView) findViewById(R.id.tvSend);
        mQuoteContentTv = (TextView) findViewById(R.id.quote_content_tv);
        mCloseQuoteIv = (ImageView) findViewById(R.id.close_quote_iv);
        mTvSend.setOnClickListener(this);
        mCloseQuoteIv.setOnClickListener(this);
        mChatFl = (FrameLayout) findViewById(R.id.chat_fl);
        mLlRoot = (LinearLayout) findViewById(R.id.llRoot);
        mEmojiLl = (LinearLayout) findViewById(R.id.emoji_ll);
        mMoreActionRv = (RecyclerView) findViewById(R.id.more_action_rv);
        initMoreActionAdapter();
        mEmojiFg = getSupportFragmentManager().findFragmentById(R.id.emoji_fg);
        initListener();
        initEmotionKeyboard();
        initAudioRecordManager();
        initAudioListener();
        mContentEt.setOnTouchListener(new View.OnTouchListener() {

            //?????????????????? ?????????+  ??????????????????????????????????????? ????????????????????????????????????
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hideBottomAndKeyboard();
                    if (mEmotionKeyboard != null) {
                        mEmotionKeyboard.showSoftInput();
                    }
                    if (!mEmojiLl.isShown()) {
                        mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
                    }
                    scrollRecyclerview();
                }
                return false;
            }
        });
        initAdapterClick();
    }

    @Override
    public void initData() {
        initAdapterData(getIntent());
    }

    @Override
    public void onEvent(EventBusObject eventBusObject) {
        super.onEvent(eventBusObject);
        switch (eventBusObject.getEventKey()) {
            case EventBusObject.MESSAGE_BODY:
                MessageBodyBean messageBody = (MessageBodyBean) eventBusObject.getEventObj();
                if (messageBody.getFromUserId() == contactBean.getUserId() || messageBody.getToUserId() == contactBean.getUserId()) {
                    //?????????????????????????????????????????? ????????????notification
                    NotificationTool.SHOW_NOTIFICATION = false;
                    // : 2022/5/21 ????????????????????????
                    mPresenter.messageRead(getBaseBuilder().add("msgId", String.valueOf(messageBody.getMsgId())).build(), AppHttpPath.MESSAGE_READ);
                    messageBody.setRead(true);
//                    HawkProperty.setRedPoint(mContext, -1);
                    addDateTag(mPresenter.findPrivateChatRecordLastMessage(messageBody.getFromUserId()),
                            messageBody);
                    initAdapterDataFromMsgTypes(messageBody);
                    scrollRecyclerview();
                } else {
                    NotificationTool.SHOW_NOTIFICATION = true;
                }
                ObjectBoxUtil.addMessage(messageBody);


                break;
            default:
                break;
        }
    }

    public void initAdapterClick() {

        chatAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                MultipleItem multipleItem = (MultipleItem) adapter.getData().get(position);
                switch (multipleItem.getItemType()) {
                    case MultipleItem.ITEM_CHAT_DATE_MSG:
                        break;
                    default:
                        MessageBodyBean messageBodyBean = (MessageBodyBean) multipleItem.getObject();
                        if (chatAdapter.isEdit()) {
                            if (messageBodyBean.isSelected()) {
                                messageBodyBean.setSelected(false);
                            } else {
                                messageBodyBean.setSelected(true);
                            }
                            chatAdapter.notifyItemChanged(position);
                            return;
                        }
                        int id = view.getId();
                        if (id == R.id.sender_pic_video_iv || id == R.id.receiver_pic_video_iv) {
                            displayPicVideo(messageBodyBean);
                        } else if (id == R.id.audio_bg_rl) {
                            ImageView ivAudio = (ImageView) adapter.getViewByPosition(mRecyclerview, position,
                                    R.id.ivAudio);
                            AudioPlayManager.getInstance().stopPlay();
                            String audioUri = UrlFormatUtil.getImageOriginalUrl(messageBodyBean.getContent());
                            AudioPlayManager.getInstance().startPlay(mContext, Uri.parse(audioUri),
                                    new IAudioPlayListener() {
                                        @Override
                                        public void onStart(Uri var1) {
                                            // : 2022/4/10 ??????????????????????????????
                                            if (ivAudio != null && ivAudio.getDrawable() instanceof AnimationDrawable) {
                                                AnimationDrawable animation = (AnimationDrawable) ivAudio.getDrawable();
                                                animation.start();
                                            }
                                        }

                                        @Override
                                        public void onStop(Uri var1) {
                                            if (ivAudio != null && ivAudio.getDrawable() instanceof AnimationDrawable) {
                                                AnimationDrawable animation = (AnimationDrawable) ivAudio.getDrawable();
                                                animation.stop();
                                                animation.selectDrawable(0);
                                            }

                                        }

                                        @Override
                                        public void onComplete(Uri var1) {
                                            if (ivAudio != null && ivAudio.getDrawable() instanceof AnimationDrawable) {
                                                AnimationDrawable animation = (AnimationDrawable) ivAudio.getDrawable();
                                                animation.stop();
                                                animation.selectDrawable(0);
                                            }
                                        }
                                    });
                        }

                        break;
                }
            }
        });


        chatAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter msgAdapter, View view, int msgPosition) {
                MultipleItem multipleItem = (MultipleItem) msgAdapter.getData().get(msgPosition);
                MessageBodyBean operateingMsgBean = (MessageBodyBean) multipleItem.getObject();

                switch (view.getId()) {

                    default:
                        View currentView = null;
                        PopupWindow editPopupWindow = null;
                        View popView = LayoutInflater.from(mContext).inflate(R.layout.home_pop, null);
                        LinearLayout topLl = popView.findViewById(R.id.pop_bg_ll);
                        topLl.setBackgroundResource(R.mipmap.edit_msg_bg);

                        RecyclerView recyclerView = popView.findViewById(R.id.home_pop_rv);
                        EditChatMsgAdapter editChatMsgAdapter = new EditChatMsgAdapter(R.layout.edit_chat_msg_item);
                        List<HomePageMenuBean> arrays = mPresenter.getEditChatMsgMenus(operateingMsgBean);
                        GridLayoutManager manager = new GridLayoutManager(mContext, arrays.size());
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setAdapter(editChatMsgAdapter);
                        editChatMsgAdapter.setNewData(arrays);
                        if (arrays.size() > 4) {
                            editPopupWindow = new PopupWindow(popView, DisplayUtil.dp2px(mContext, 180), DisplayUtil.dp2px(mContext, 100));
                        } else {
                            editPopupWindow = new PopupWindow(popView, DisplayUtil.dp2px(mContext, 100), DisplayUtil.dp2px(mContext, 50));

                        }
                        editPopupWindow.setOutsideTouchable(true);

                        int id = view.getId();
                        if (id == R.id.sender_content_tv) {
                            currentView = msgAdapter.getViewByPosition(mRecyclerview, msgPosition,
                                    R.id.sender_content_tv);
                        } else if (id == R.id.receiver_content_tv) {
                            currentView = msgAdapter.getViewByPosition(mRecyclerview, msgPosition,
                                    R.id.receiver_content_tv);
                        } else if (id == R.id.sender_pic_video_iv) {//????????????
                            currentView = msgAdapter.getViewByPosition(mRecyclerview, msgPosition,
                                    R.id.sender_pic_video_iv);
                        } else if (id == R.id.receiver_pic_video_iv) {
                            currentView = msgAdapter.getViewByPosition(mRecyclerview, msgPosition,
                                    R.id.receiver_pic_video_iv);
                        } else if (id == R.id.audio_bg_rl) {
                            currentView = msgAdapter.getViewByPosition(mRecyclerview, msgPosition,
                                    R.id.audio_bg_rl);
                        } else {
                            currentView = view;
                        }
                        int[] location = new int[2];
                        currentView.getLocationOnScreen(location);
                        //??????????????????
                        editPopupWindow.showAtLocation(currentView, Gravity.NO_GRAVITY,
                                location[0] + currentView.getWidth() / 2 - editPopupWindow.getWidth() / 2,
                                location[1] - editPopupWindow.getHeight());
                        PopupWindow finalEditPopupWindow = editPopupWindow;
                        editChatMsgAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                HomePageMenuBean menuBean = (HomePageMenuBean) adapter.getItem(position);
                                finalEditPopupWindow.dismiss();
                                switch (menuBean.getName()) {
                                    case MainContract.COPY_MSG:
                                        // : 2022-02-12 ????????????
                                        copy(operateingMsgBean.getContent());

                                        break;
                                    case MainContract.DELETE_MSG:
                                        // : 2022-01-21  ??????????????????
                                        showAlertDialog("?????????????", "??????", "??????", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                msgAdapter.remove(msgPosition);
                                                ObjectBoxUtil.deleteMessage(operateingMsgBean);
                                            }
                                        });

                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        break;

                }
                return true;
            }

        });


    }

    //????????????
    private void displayPicVideo(MessageBodyBean messageBodyBean) {
        // : 2022/5/23 ?????????????????????

        int position = 0;
        for (int i = 0; i < allPicVideoPath.size(); i++) {
            BannerObject bannerObject = allPicVideoPath.get(i);

            String eventKey = bannerObject.getEventKey();
            if (BannerObject.BANNER_TYPE_IMAGE.equals(eventKey)) {
                if (messageBodyBean.getContent().equals(bannerObject.getPicPath())) {
                    position = i;
                    break;
                }

            } else if (BannerObject.BANNER_TYPE_VIDEO.equals(eventKey)) {
                BannerObject.VideoBean videoBean = bannerObject.getVideoBean();
                if (videoBean != null && !TextUtils.isEmpty(videoBean.getVideoPath())) {
                    if (messageBodyBean.getContent().equals(videoBean.getVideoPath())) {
                        position = i;
                        break;
                    }
                }
            }

        }
        PicVideoDisplayActivity.startPicVideoPlayActivity(mContext,allPicVideoPath,position);


    }

    /**
     * ????????????
     */
    private void scrollRecyclerview() {
        mRecyclerview.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerview.scrollToPosition(chatAdapter.getData().size() - 1);
            }
        }, 500);
    }

    @Override
    protected void selectedPicsAndEmpressed(List<String> icons) {
        super.selectedPicsAndEmpressed(icons);
        if (icons.size() > 0) {
            if (fileSizeIsOk(icons)) {
                // : 2022-03-09 ??????????????????????????????

                for (String picPath : icons) {
                    if (1 == FileCacheUtils.getFileType(picPath)) {
                        uploadPicFile(picPath);
                    } else if (2 == FileCacheUtils.getFileType(picPath)) {
                        uploadVideoFile(picPath);
                    }
                }

            }
        }
    }

    private boolean fileSizeIsOk(List<String> icons) {
        for (String icon : icons) {
            File file = new File(icon);
            try {
                long fileSize = FileCacheUtils.getFileSize(file);
                if (FileCacheUtils.isOutInMaxLength(fileSize)) {
                    ToastUtils.toast(mContext, "??????????????????500M?????????");
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void initMoreActionAdapter() {
        moreActionAdapter = new ChatMoreActionAdapter(R.layout.item_more_action);
        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        mMoreActionRv.setLayoutManager(manager);
        mMoreActionRv.setAdapter(moreActionAdapter);
        moreActionAdapter.setNewData(mPresenter.getMoreActionMenus());
        moreActionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MyMenuBean myMenuBean = (MyMenuBean) adapter.getData().get(position);
                switch (myMenuBean.getName()) {
                    case ChatPresent.MORE_ACTION_PIC:
                        //  ??????????????????????????????
                        choseImageAndVideo(0, ChatActivity.this, 6);
                        break;
                    case ChatPresent.MORE_ACTION_TAKE_PHOTO:
                        // : 2021-11-23 ?????? ???????????????
                        startActivityForResult(new Intent(mContext, CameraActivity.class), REQUEST_TAKE_PHOTO);
                        break;
                    case ChatPresent.MORE_ACTION_LOCATION:
                        // : 2021-11-23 ????????????
                        startActivityForResult(new Intent(mContext, LocateSelectionActivity.class)
                                .putExtra(LocateSelectionActivity.RIGHT_CONTENT, "??????"), BASE_REQUEST_RESULT);

                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void initAdapterData(Intent intent) {

        setOnFileUploadStatus(this);
        contactBean = intent.getParcelableExtra(BaseActivity.BASE_PARCELABLE);
        // : 2022/5/19 ???????????????
//        UserInfoManager.initContacts(contactBean);
// : 2022/5/19 ???????????????????????????
        setTitleName(contactBean.getNickname());
        chatAdapter.setNewData(null);
        List<MessageBodyBean> arrays = mPresenter.findPrivateChatRecordList(contactBean.getUserId());
        if (arrays != null && arrays.size() > 0) {
            for (int i = 0; i < arrays.size(); i++) {
                MessageBodyBean startBean = arrays.get(i);
                if (!startBean.isRead()) {
                    startBean.setRead(true);
                    //?????????????????????
                    ObjectBoxUtil.addMessage(startBean);
                }
                initAdapterDataFromMsgTypes(startBean);
                if (i < arrays.size() - 1) {
                    MessageBodyBean endBean = arrays.get(i + 1);
                    addDateTag(startBean, endBean);
                }
            }
        }
        scrollRecyclerview();
        mPresenter.getContactUnreadMsg(getBaseBuilder().add("toUserId", String.valueOf(contactBean.getUserId())).build(), AppHttpPath.UNREAD_CONTACT_MSG);

    }

    private void initAdapterDataFromMsgTypes(MessageBodyBean messageBean) {
        switch (messageBean.getMsgType()) {
            case 0:
                chatAdapter.addData(new MultipleItem(MultipleItem.ITEM_CHAT_TEXT_MSG, messageBean));
                break;
            case 1:
                allPicVideoPath.add(new BannerObject(BannerObject.BANNER_TYPE_IMAGE, messageBean.getContent()));
                chatAdapter.addData(new MultipleItem(MultipleItem.ITEM_CHAT_PIC_VIDEO, messageBean));
                break;
            case 2:
                allPicVideoPath.add(new BannerObject(BannerObject.BANNER_TYPE_VIDEO, new BannerObject.VideoBean(messageBean.getContent(), messageBean.getVideoCover())));
                chatAdapter.addData(new MultipleItem(MultipleItem.ITEM_CHAT_PIC_VIDEO, messageBean));
                break;
            case 3:
                //????????????
                if (UserInfoManager.getUserId() == messageBean.getFromUserId()) {
                    chatAdapter.addData(new MultipleItem(MultipleItem.ITEM_SEND_AUDIO, messageBean));
                } else {
                    chatAdapter.addData(new MultipleItem(MultipleItem.ITEM_RECEIVE_AUDIO, messageBean));
                }
                break;
            case 5:
            default:
                break;
        }
    }


    /**
     * ??????????????????
     *
     * @param startBean
     * @param endBean
     */
    private void addDateTag(MessageBodyBean startBean, MessageBodyBean endBean) {
        String startTime = null;
        if (startBean == null) {
            startTime = CalendarUtil.getCurrentTime();
        } else {
            startTime = CalendarUtil.formatSystemCurrentMillis(startBean.getCreateTime());
        }
        String endTime = CalendarUtil.formatSystemCurrentMillis(endBean.getCreateTime());
        if (CalendarUtil.getGapMinutes(startTime, endTime) > 5) {
            //??????????????????5??????  ???????????????
            chatAdapter.addData(new MultipleItem(MultipleItem.ITEM_CHAT_DATE_MSG, new MessageBodyBean(endTime, 100)));
        }
    }


    @Override
    public void onSuccess(String tag, Object o) {
        switch (tag) {
            case MainContract.UPLOAD_AUDIO_FILE:
                //??????????????????

                List<String> audioPaths = (List<String>) o;
                if (audioPaths != null && !audioPaths.isEmpty()) {
                    for (String picPath : audioPaths) {
                        MessageBodyBean messageBodyBean = OperateMsgUtil.getPrivateMsg(3, contactBean.getUserId(),
                                contactBean.getAccount(), contactBean.getNickname(),
                                contactBean.getHeadPortrait(), picPath);
                        messageBodyBean.setDuration(String.valueOf(mDuration));
                        addDateTag(mPresenter.findPrivateChatRecordLastMessage(messageBodyBean.getFromUserId()), messageBodyBean);
                        chatAdapter.addData(new MultipleItem(MultipleItem.ITEM_SEND_AUDIO, messageBodyBean));
                        ObjectBoxUtil.addMessage(messageBodyBean);
                        mPresenter.sendPrivateMessage(OperateMsgUtil.getMsgBuilder(messageBodyBean).build(),
                                AppHttpPath.SEND_MSG);


                    }
                }
                scrollRecyclerview();
                break;
            case AppHttpPath.UNREAD_CONTACT_MSG:
                MessageListBean messageListBean = (MessageListBean) o;
                if (messageListBean != null) {
                    List<MessageBodyBean> messageBodyBeans = messageListBean.getData();
                    if (messageBodyBeans != null && !messageBodyBeans.isEmpty()) {
                        for (int i = 0; i < messageBodyBeans.size(); i++) {
                            MessageBodyBean startBean = messageBodyBeans.get(i);
                            startBean.setId(0);
                            startBean.setRead(true);
                            if (i < messageBodyBeans.size() - 1) {
                                MessageBodyBean endBean = messageBodyBeans.get(i + 1);
                                addDateTag(startBean, endBean);
                            }
                            ObjectBoxUtil.addMessage(startBean);
                        }
                        initAdapterData(getIntent());
                    }
                }

                break;
            default:
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initListener() {
        mContentEt.addTextChangedListener(new TextWatcher() {
            boolean isAt = false;//?????????@

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String content = s.toString();
                if (after == 0) {
                    if (isAt) {
                        return;
                    }
                    if (content.endsWith("\u3000")) {
                        //??????@?????????
                        if (!atUsers.isEmpty()) {
                            atUsers.remove(atUsers.size() - 1);
                        }
                        isAt = true;
                        content = content.substring(0, content.lastIndexOf("@"));
                        mContentEt.setText(content);
                        mContentEt.setSelection(content.length());
                    } else {
                        isAt = false;
                    }
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // : 2022-02-08 ??????????????????@???????????????
                String content = s.toString();
                if (TextUtils.isEmpty(content) || !content.endsWith("\u3000")) {
                    isAt = false;
                }
                content = content.substring(start, s.length());


            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if (!TextUtils.isEmpty(content)) {
                    mTvSend.setVisibility(View.VISIBLE);
                    mIvMore.setVisibility(View.GONE);
                } else {
                    mTvSend.setVisibility(View.GONE);
                    mIvMore.setVisibility(View.VISIBLE);
                }


            }
        });
    }


    //-------------------------------------------------??????--------------------------------------------------------------

    //?????????????????????
    private void initAudioRecordManager() {
        AudioRecordManager.getInstance(this).setMaxVoiceDuration(60);
        final File audioDir = new File(Environment.getExternalStorageDirectory(), "AUDIO");
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }
        AudioRecordManager.getInstance(this).setAudioSavePath(audioDir.getAbsolutePath());
        AudioRecordManager.getInstance(this).setAudioRecordListener(new IAudioRecordListener() {

            private TextView mTimerTV;
            private TextView mStateTV;
            private ImageView mStateIV;
            private PopupWindow mRecordWindow;

            @Override
            public void initTipView() {
                View view = View.inflate(mContext, R.layout.popup_audio_wi_vo, null);
                mStateIV = (ImageView) view.findViewById(R.id.rc_audio_state_image);
                mStateTV = (TextView) view.findViewById(R.id.rc_audio_state_text);
                mTimerTV = (TextView) view.findViewById(R.id.rc_audio_timer);
                mRecordWindow = new PopupWindow(view, -1, -1);
                mRecordWindow.showAtLocation(mLlRoot, 17, 0, 0);
                mRecordWindow.setFocusable(true);
                mRecordWindow.setOutsideTouchable(false);
                mRecordWindow.setTouchable(false);
            }

            @Override
            public void setTimeoutTipView(int counter) {
                if (this.mRecordWindow != null) {
                    this.mStateIV.setVisibility(View.GONE);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_rec);
                    this.mStateTV.setBackgroundResource(R.drawable.bg_voice_popup);
                    this.mTimerTV.setText(String.format("%s", new Object[]{Integer.valueOf(counter)}));
                    this.mTimerTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void setRecordingTipView() {
                if (this.mRecordWindow != null) {
                    this.mStateIV.setVisibility(View.VISIBLE);
                    this.mStateIV.setImageResource(R.mipmap.ic_volume_1);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_rec);
                    this.mStateTV.setBackgroundResource(R.drawable.bg_voice_popup);
                    this.mTimerTV.setVisibility(View.GONE);
                }
            }

            @Override
            public void setAudioShortTipView() {
                if (this.mRecordWindow != null) {
                    mStateIV.setImageResource(R.mipmap.ic_volume_wraning);
                    mStateTV.setText(R.string.voice_short);
                }
            }

            @Override
            public void setCancelTipView() {
                if (this.mRecordWindow != null) {
                    this.mTimerTV.setVisibility(View.GONE);
                    this.mStateIV.setVisibility(View.VISIBLE);
                    this.mStateIV.setImageResource(R.mipmap.ic_volume_cancel);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_cancel);
                    this.mStateTV.setBackgroundResource(R.drawable.corner_voice_style);
                }
            }

            @Override
            public void destroyTipView() {
                if (this.mRecordWindow != null) {
                    this.mRecordWindow.dismiss();
                    this.mRecordWindow = null;
                    this.mStateIV = null;
                    this.mStateTV = null;
                    this.mTimerTV = null;
                }
            }

            @Override
            public void onStartRecord() {//?????????????????????
            }

            @Override
            public void onFinish(Uri audioPath, int duration) {
                mDuration = duration;
                //????????????
                File file = new File(audioPath.getPath());
                if (!file.exists() || file.length() == 0L) {
                    return;
                }
                mPresenter.uploadFile(MainContract.UPLOAD_AUDIO_FILE, file.getAbsolutePath());
            }

            @Override
            public void onAudioDBChanged(int db) {
                switch (db / 5) {
                    case 0:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_1);
                        break;
                    case 1:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_2);
                        break;
                    case 2:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_3);
                        break;
                    case 3:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_4);
                        break;
                    case 4:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_5);
                        break;
                    case 5:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_6);
                        break;
                    case 6:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_7);
                        break;
                    default:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_8);
                }
            }
        });
    }

    //????????????
    @SuppressLint("ClickableViewAccessibility")
    private void initAudioListener() {
        mBtnAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AudioRecordManager.getInstance(mContext).startRecord();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isCancelled(view, motionEvent)) {
                            AudioRecordManager.getInstance(mContext).willCancelRecord();
                        } else {
                            AudioRecordManager.getInstance(mContext).continueRecord();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        AudioRecordManager.getInstance(mContext).stopRecord();
                        AudioRecordManager.getInstance(mContext).destroyRecord();
                        break;
                }
                return false;
            }
        });
    }

    private boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        if (event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth()
                || event.getRawY() < location[1] - 40) {
            return true;
        }

        return false;
    }

    private void initEmotionKeyboard() {
        mEmotionKeyboard = EmotionKeyboard.with(this);
        mEmotionKeyboard.bindToEditText(mContentEt);
        mEmotionKeyboard.bindToContent(mContentLl);
        mEmotionKeyboard.setEmotionLayout(mChatFl);
        mEmotionKeyboard.bindToEmotionButton(mIvEmo, mIvMore);
        mEmotionKeyboard.setOnEmotionButtonOnClickListener(new EmotionKeyboard.OnEmotionButtonOnClickListener() {
            @Override
            public boolean onEmotionButtonOnClickListener(View view) {
                int id = view.getId();
                if (id == R.id.ivEmo) {//????????????
                    if (!mEmojiLl.isShown()) {
                        //????????????????????????
                        if (mMoreActionRv.isShown()) {
                            //????????????????????????
                            showEmotionLayout();
                            hideMoreLayout();
                            hideAudioButton();
                            return true;
                        }
                    } else if (mEmojiLl.isShown() && !mMoreActionRv.isShown()) {
                        mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
                        return false;
                    }
                    showEmotionLayout();
                    hideMoreLayout();
                    hideAudioButton();
                } else if (id == R.id.ivMore) {
                    if (!mMoreActionRv.isShown()) {
                        if (mEmojiLl.isShown()) {
                            showMoreLayout();
                            hideEmotionLayout();
                            hideAudioButton();
                            return true;
                        }
                    }
                    hideEmotionLayout();
                    hideAudioButton();
                    showMoreLayout();
                }
                return false;
            }
        });
    }

    //----------------------------------------------------??????---------------------------------------------------------------------


    private void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showAudioButton() {
        mBtnAudio.setVisibility(View.VISIBLE);
        mContentEt.setVisibility(View.GONE);
        mIvAudio.setImageResource(R.mipmap.ic_cheat_keyboard);

        if (mChatFl.isShown()) {
            if (mEmotionKeyboard != null) {
                mEmotionKeyboard.interceptBackPress();
            }
        } else {
            if (mEmotionKeyboard != null) {
                mEmotionKeyboard.hideSoftInput();
            }
        }
    }

    private void hideAudioButton() {
        mBtnAudio.setVisibility(View.GONE);
        mContentEt.setVisibility(View.VISIBLE);
        mIvAudio.setImageResource(R.mipmap.ic_cheat_voice);
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        int index = mContentEt.getSelectionStart();
        Editable editableText = mContentEt.getEditableText();
        editableText.insert(index, emoji.getName());
        displayEmoji(mContentEt, index + emoji.getName().length());
    }

    @Override
    public void onEmojiDelete() {
        String content = mContentEt.getText().toString();
        int index = mContentEt.getSelectionStart();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        if ("]".equals(content.substring(index - 1, index))) {
            int lastIndexOf = content.lastIndexOf("[", index - 1);
            if (lastIndexOf == -1) {
                onKeyDownDelete(mContentEt);
            } else {
                mContentEt.getText().delete(lastIndexOf, index);
                displayEmoji(mContentEt, lastIndexOf);
            }
            return;
        }
        onKeyDownDelete(mContentEt);
    }

    public void onKeyDownDelete(EditText editText) {
        int index = mContentEt.getSelectionStart();
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL);
        editText.onKeyDown(KeyEvent.KEYCODE_DEL, keyEvent);
        displayEmoji(mContentEt, index - 1);
    }

    public void displayEmoji(TextView textView, int indexSelection) {
        EmojiUtils.showEmojiTextView(this, textView, textView.getText().toString(), 20);
        if (textView instanceof EditText) {
            ((EditText) textView).setSelection(indexSelection);
        }
    }

    private void showEmotionLayout() {
        mEmojiLl.setVisibility(View.VISIBLE);
        mIvEmo.setImageResource(R.mipmap.ic_cheat_keyboard);
    }

    private void hideEmotionLayout() {
        mEmojiLl.setVisibility(View.GONE);
        mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
    }

    private void showMoreLayout() {
        if (!mMoreActionRv.isShown()) {
            mMoreActionRv.setVisibility(View.VISIBLE);
        }
    }

    private void hideMoreLayout() {
        mMoreActionRv.setVisibility(View.GONE);
    }

    private void hideBottomAndKeyboard() {
        mEmojiLl.setVisibility(View.GONE);
        mMoreActionRv.setVisibility(View.GONE);
        if (mEmotionKeyboard != null) {
            mEmotionKeyboard.interceptBackPress();
        }
    }


    @Override
    public void onUploadProgressChange(UploadFileBean uploadFileBean, int percent) {
        hideBottomAndKeyboard();
        MessageBodyBean messageBodyBean = uploadFileBean.getMessageBodyBean();
        if (messageBodyBean != null) {
            messageBodyBean.setUploadProgress(percent);
            chatAdapter.notifyItemChanged(messageBodyBean.getAdapterPosition(), R.id.sender_progress);
        }

    }

    @Override
    public void onUploadFinish(UploadFileBean uploadFileBean) {
        if (uploadFileBean.getCode() != 0) {
            ToastUtils.toast(mContext, "????????????");
            return;
        }
        MessageBodyBean messageBodyBean = uploadFileBean.getMessageBodyBean();
        List<String> filePaths = uploadFileBean.getFilePaths();
        if (filePaths != null && filePaths.size() > 0) {
            // : 2022/4/10 ??????????????????????????????

            switch (messageBodyBean.getMsgType()) {
                case 1:
                    //????????????
                    /**
                     * ????????????????????????????????????
                     */
                    messageBodyBean.setContent(filePaths.get(0));
                    addDateTag(mPresenter.findPrivateChatRecordLastMessage(messageBodyBean.getFromUserId()),
                            messageBodyBean);
                    chatAdapter.addData(new MultipleItem(MultipleItem.ITEM_CHAT_PIC_VIDEO, messageBodyBean));
                    scrollRecyclerview();
                    ObjectBoxUtil.addMessage(messageBodyBean);
                    allPicVideoPath.add(new BannerObject(BannerObject.BANNER_TYPE_IMAGE, messageBodyBean.getContent()));
                    break;
                case 2:
                    //????????????
                    String fileName = getSavedFileName(filePaths.get(0));
                    if (fileName.startsWith(ImageLoadUtil.IMAGE_TYPE_VIDEO_THUM)) {
                        LogUtil.d("???????????????" + messageBodyBean.getContent());
                        //???????????????
                        messageBodyBean.setVideoCover(filePaths.get(0));
                        messageBodyBean.setContent(null);
                        ImageLoadUtil.getExifOrientation(mContext, FileCacheUtils.getAppImagePath(true) + fileName, new ImageLoadUtil.OnImageLoadSuccess() {
                            @Override
                            public void loadSuccess(int width, int height) {
                                FileBaseInfoBean fileBaseInfoBean = ImageLoadUtil.getVideoFileBaseInfo(messageBodyBean.getLocalCatchPath());
                                messageBodyBean.setRotation(width > height ? "0" : "90");
                                messageBodyBean.setDuration(fileBaseInfoBean.getDuration());
                                addDateTag(mPresenter.findPrivateChatRecordLastMessage(messageBodyBean.getFromUserId()),
                                        messageBodyBean);
                                chatAdapter.addData(new MultipleItem(MultipleItem.ITEM_CHAT_PIC_VIDEO, messageBodyBean));
                                messageBodyBean.setAdapterPosition(chatAdapter.getData().size() - 1);
                                ObjectBoxUtil.addMessage(messageBodyBean);
                                scrollRecyclerview();
                                //??????????????????
                                mUploadUtil.submit(ChatActivity.this, new UploadFileBean(messageBodyBean.getLocalCatchPath(), messageBodyBean));
                            }
                        });
                    } else {
                        LogUtil.d("????????????" + messageBodyBean.getContent());
                        //????????????
                        messageBodyBean.setContent(filePaths.get(0));
                        allPicVideoPath.add(new BannerObject(BannerObject.BANNER_TYPE_VIDEO, new BannerObject.VideoBean(messageBodyBean.getContent(), messageBodyBean.getVideoCover())));

                    }
                    break;
                case 6:
                    //??????
                default:
                    messageBodyBean.setContent(filePaths.get(0));
                    break;
            }
            ObjectBoxUtil.addMessage(messageBodyBean);
            if (!TextUtils.isEmpty(messageBodyBean.getContent())) {
                mPresenter.sendPrivateMessage(OperateMsgUtil.getMsgBuilder(messageBodyBean).build(), AppHttpPath.SEND_MSG);

            }

        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivAudio) {//???????????????
            if (mBtnAudio.isShown()) {
                hideAudioButton();
                mContentEt.requestFocus();
                if (mEmotionKeyboard != null) {
                    mEmotionKeyboard.showSoftInput();
                    scrollRecyclerview();
                }
            } else {
                showAudioButton();
                hideEmotionLayout();
                hideMoreLayout();
            }
        } else if (id == R.id.tvSend) {//???????????????????????????
            sendNormalMsg(contactBean, getTextViewValue(mContentEt));
            mContentEt.setText("");
            mQuoteContentTv.setTag(null);
        }
    }


    /**
     * ??????????????????
     */
    private void sendNormalMsg(ContactBean toContactBean, String content) {
        MessageBodyBean messageBody = OperateMsgUtil.getPrivateMsg(0, toContactBean.getUserId(), toContactBean.getAccount(),
                toContactBean.getNickname(), toContactBean.getHeadPortrait(), content);
        mPresenter.sendPrivateMessage(OperateMsgUtil.getMsgBuilder(messageBody).build(), AppHttpPath.SEND_MSG);
        addDateTag(mPresenter.findPrivateChatRecordLastMessage(messageBody.getFromUserId()), messageBody);
        chatAdapter.addData(new MultipleItem(MultipleItem.ITEM_CHAT_TEXT_MSG, messageBody));
        scrollRecyclerview();
        ObjectBoxUtil.addMessage(messageBody);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == 101) {//??????
                    String path = data.getStringExtra("path");
                    uploadPicFile(path);
                } else if (resultCode == 102) {//?????????
                    String path = data.getStringExtra("path");
                    uploadVideoFile(path);
                } else if (resultCode == 103) {
                    Toast.makeText(this, "?????????????????????~", Toast.LENGTH_SHORT).show();
                }
                break;


        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    /**
     * ??????????????????
     *
     * @param picPath
     */
    private void uploadPicFile(String picPath) {
        hideBottomAndKeyboard();
        //??????????????????


        ImageLoadUtil.getExifOrientation(mContext, picPath, new ImageLoadUtil.OnImageLoadSuccess() {
            @Override
            public void loadSuccess(int width, int height) {
                MessageBodyBean messageBodyBean = OperateMsgUtil.getPrivateMsg(1, contactBean.getUserId(),
                        contactBean.getAccount(), contactBean.getNickname(),
                        contactBean.getHeadPortrait(), "");
                messageBodyBean.setRotation(width > height ? "0" : "90");
                messageBodyBean.setLocalCatchPath(picPath);
                messageBodyBean.setAdapterPosition(chatAdapter.getData().size() - 1);
                mUploadUtil.submit(ChatActivity.this, new UploadFileBean(picPath, messageBodyBean));


            }
        });


    }

    /**
     * ??????????????????
     *
     * @param picPath
     */
    private void uploadVideoFile(String picPath) {
        hideBottomAndKeyboard();

        //??????????????????

        // : 2022/4/10 ?????????????????????????????????  ?????????????????????????????????????????????????????? ?????????????????? ??????????????????????????????????????????

        MessageBodyBean messageBodyBean = OperateMsgUtil.getPrivateMsg(2, contactBean.getUserId(),
                contactBean.getAccount(), contactBean.getNickname(),
                contactBean.getHeadPortrait(), "");
        messageBodyBean.setLocalCatchPath(picPath);
        RxScheduler.doTask(this, new RxTask<String>() {
            @Override
            public String doOnIoThread() {
                Bitmap videoBitmap = ImageLoadUtil.getVideoThumbnail(picPath);
                return FileCacheUtils.saveBitmap(videoBitmap, ImageLoadUtil.getVideoThumbnailName(messageBodyBean.getCreateTime()), true);
            }

            @Override
            public void doOnUIThread(String str) {
                //?????????????????????????????????????????????
                mUploadUtil.submit(ChatActivity.this, new UploadFileBean(str, messageBodyBean));

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        allPicVideoPath.clear();
        AudioPlayManager.getInstance().stopPlay();
        AudioRecordManager.getInstance(this).setAudioRecordListener(null);
        AudioPlayManager.getInstance().release();
    }
}

