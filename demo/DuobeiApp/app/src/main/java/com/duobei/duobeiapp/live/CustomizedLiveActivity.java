package com.duobei.duobeiapp.live;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.bean.LiveInfo;
import com.duobei.duobeiapp.common.MessageDialog;
import com.duobei.duobeiapp.live.adapter.ChatAdapter;
import com.duobei.duobeiapp.live.widget.ChooseAnswerDialog;
import com.duobei.duobeiapp.live.widget.ChooseAnswerResultDialog;
import com.duobei.duobeiapp.live.widget.JudgetAnswerDialog;
import com.duobei.duobeiapp.live.widget.JudgetAnswerResultDialog;
import com.duobei.duobeiapp.utils.Constant;
import com.duobei.duobeiapp.utils.ToastUtils;
import com.duobeiyun.bean.ChatBean;
import com.duobeiyun.callback.ChatMsgRefreshCallback;
import com.duobeiyun.callback.DBLocalVideoCallback;
import com.duobeiyun.callback.FirstVideoFrameCallback;
import com.duobeiyun.callback.LiveMessageCallback;
import com.duobeiyun.callback.MicRequestCallback;
import com.duobeiyun.callback.OpenglSupport;
import com.duobeiyun.callback.RaiseHandCallback;
import com.duobeiyun.callback.VideoCallback;
import com.duobeiyun.callback.VideoPositionChange;
import com.duobeiyun.opengles.GLFrameSurface;
import com.duobeiyun.player.LivePlayer;
import com.duobeiyun.player.RoomInfoBean;
import com.duobeiyun.type.LiveMessage;
import com.duobeiyun.type.RoleType;
import com.duobeiyun.type.StatusCode;
import com.duobeiyun.util.CommonUtils;
import com.duobeiyun.widget.LivePlayerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播界面版本1vn Activity
 * (定制的界面版本不做封装处理；便于开发者自行修改界面和相应的处理逻辑)
 */
public class CustomizedLiveActivity extends Activity implements View.OnClickListener, LiveMessageCallback, VideoCallback, VideoPositionChange, DBLocalVideoCallback {
    private static final String OPENLIVE = "openlive";
    private static final String YQM = "yaoqingma";
    private Context mContext;
    //竖向导航
    private RelativeLayout portraitNaviLayout;
    private Button portraitBackBtn;
    private ImageButton portraitFullScreenBtn;
    //横向导航
    private RelativeLayout landscapeNaviLayout;
    private ImageButton landscapeBackBtn;//横向返回按钮
    private TextView landscapeCourseTitleText;//横向课程名称

    //聊天显示区域
    private RelativeLayout chatLayout;
    private RadioGroup chatGroup;
    private RadioButton allMsgBtn, teacherMsgBtn;
    private ListView chatList;
    private ImageButton chatSwitchBtn;

    private RelativeLayout rlportraitfullScreen;
    private RelativeLayout root;
    private LivePlayer player;
    private LivePlayerView mPlayerView;

    private boolean isFirstRunning = true;

    //与直播相关的信息(都是由开发者自己维护，后台获取)
    private String roomId;//= "jzf3a0ad046b62430280f9e41e4cfe8a9d";//romid
    private String nickname = android.os.Build.MANUFACTURER;//别名
    private String uid;//= "b230bd87-584a-4f3d-90b8-b4fafb9eab19";//唯一的uuid
    private ProgressBar load;
    private String Url = null;//通过url进教室，url产生方式请参考官方文档或者询问客服
    //聊天相关
    private int chatCapacity = 30;//屏幕上默认显示30条
    private List<ChatBean> allChatList = new ArrayList(chatCapacity);
    private List<ChatBean> teacherList = new ArrayList(chatCapacity);
    ChatAdapter adapter;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private RelativeLayout mRlrootlayout;
    private GLFrameSurface mTeachersurface;
    private GLFrameSurface mStudentSurface;
    private MessageDialog mInputMsgDialog;
    private ImageButton mAnswerButton;
    private ImageButton mHandUpButton;

    private int answerType = 0;//定义一个答题的类型；默认设置为0，通过该值判断点击答题按钮时开启哪一个dialog
    private JudgetAnswerDialog mJudgetAnswerDialog;
    private JudgetAnswerResultDialog mJudgetAnswerResultDialog;
    private boolean isAnswerd = false;//用户判断用户是否已经提交答题
    private boolean isStopAnswer = false;//老师是否停止答题
    private int mYourChooseOptions = -1;
    private ChooseAnswerDialog mMChooseAnswerDialog;
    private ChooseAnswerResultDialog mMChooseResultDialog;
    private JSONObject mLastObject;
    private GLFrameSurface localVideoSurface;//本地视频显示图像

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_customized_live);
        mContext = this.getApplicationContext();
        getIntnetEx();
        initView();
        initPlayerAndSetCallback();
        setOtherCallback();
        setPlayerChat();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (player != null) {
            try {
                player.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setPlayerChat() {
        player.setChatMsgMaxCountLimit(1000);
        player.setChatMsgFreshTime(1000);
        player.setChatMsgRefreshCallback(new ChatMsgRefreshCallback() {
            @Override
            public void refreshChatMsg(ArrayList<ChatBean> allMsg2) {//返回的是所有的老师信息
                allChatList.clear();
                allChatList.addAll(allMsg2);
                adapter.notifyDataSetChanged();
                int allMsgCount = allChatList.size();
                int teacherMsgCount = teacherList.size();
                allMsgBtn.setText(String.format("全部聊天记录(%d)", allMsgCount));
                teacherMsgBtn.setText(String.format("只看老师(%d)", player.getChatModule().getAllTeacherChatMsg().size()));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!isFirstRunning) {
            if (player != null) {
                player.recovery();
            }
        }
        if (isFirstRunning) {
            isFirstRunning = false;
        }
        allChatList.clear();
        teacherList.clear();
    }

    @Override
    protected void onPause() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (player != null) {
            player.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            load.setVisibility(View.VISIBLE);
            if (player != null) {
                player.stopApi();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getIntnetEx() {
        Intent intent = getIntent();
        LiveInfo liveInfo = (LiveInfo) intent.getSerializableExtra(OPENLIVE);
        if (liveInfo != null) {
            this.roomId = liveInfo.getRoomId();
            this.uid = liveInfo.getUuid();
            this.nickname = liveInfo.getNickname();
        }
        String stringExtra = intent.getStringExtra(YQM);
        if (stringExtra != null) {
            this.mYqcode = stringExtra;
        }

    }

    /**
     * 初始化布局文件
     */
    public void initView() {
        //播放器
        mPlayerView = (LivePlayerView) findViewById(R.id.liveplayer);
        if (getOrientation(this)) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int width = layoutParams.width = CommonUtils.getScreenWidth(this);
            layoutParams.height = width * 3 / 4;
            mPlayerView.setLayoutParams(layoutParams);
        }
        //竖屏
        portraitNaviLayout = (RelativeLayout) findViewById(R.id.portrait_navi_layout);
        portraitBackBtn = (Button) findViewById(R.id.portrait_back);
        portraitBackBtn.setOnClickListener(this);
        portraitFullScreenBtn = (ImageButton) findViewById(R.id.portrait_fullScreen);
        portraitFullScreenBtn.setOnClickListener(this);
        portraitFullScreenBtn.setClickable(true);
        //横屏
        landscapeNaviLayout = (RelativeLayout) findViewById(R.id.landscape_navi_layout);
        landscapeBackBtn = (ImageButton) findViewById(R.id.landscape_back);
        landscapeBackBtn.setOnClickListener(this);
        landscapeCourseTitleText = (TextView) findViewById(R.id.landscape_course_title);
        //聊天
        chatLayout = (RelativeLayout) findViewById(R.id.chat_layout);
        allMsgBtn = (RadioButton) findViewById(R.id.allMsg);
        teacherMsgBtn = (RadioButton) findViewById(R.id.teacherMsg);
        chatGroup = (RadioGroup) findViewById(R.id.chatGroup);
        chatList = (ListView) findViewById(R.id.chatList);
        chatSwitchBtn = (ImageButton) findViewById(R.id.chatSwitch);
        chatSwitchBtn.setOnClickListener(this);
        //显示老师视频
        rlportraitfullScreen = (RelativeLayout) findViewById(R.id.rl_portrait_fullScreen);
        root = (RelativeLayout) findViewById(R.id.rl_root);
        root.setOnClickListener(this);
        load = (ProgressBar) findViewById(R.id.load);
        adapter = new ChatAdapter(mContext, allChatList, teacherList);
        chatList.setAdapter(adapter);

        mRlrootlayout = (RelativeLayout) findViewById(R.id.rl_roots);
        mTeachersurface = (GLFrameSurface) findViewById(R.id.dbs_teacher);
        mTeachersurface.setVisibility(View.GONE);
        mStudentSurface = (GLFrameSurface) findViewById(R.id.dbs_student);
        mStudentSurface.setVisibility(View.GONE);
        mAnswerButton = (ImageButton) findViewById(R.id.ib_live_answer);
        mAnswerButton.setOnClickListener(this);
        mHandUpButton = (ImageButton) findViewById(R.id.ib_live_handup);
        mHandUpButton.setOnClickListener(this);
        Button btnVideoChange = (Button) findViewById(R.id.btn_video_change);
        btnVideoChange.setOnClickListener(this);
        findViewById(R.id.btn_close_localvideo).setOnClickListener(this);
        localVideoSurface = (GLFrameSurface) findViewById(R.id.dbs_local_camera);
    }

    /**
     * 设置相应的回调
     */
    public void setOtherCallback() {
        chatGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.allMsg) {
                    adapter.setShowAll(true);

                } else if (checkedId == R.id.teacherMsg) {
                    adapter.setShowAll(false);
                }
            }
        });

        if (player == null) {
            return;
        }
        /**
         * 学生举手回调
         */
        player.setRaiseHandCallback(new RaiseHandCallback() {
            @Override
            public void handSuccess() {
                Toast.makeText(mContext, "举手成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void handFail() {
                Toast.makeText(mContext, "举手失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void downHand() {
                Toast.makeText(mContext, "老师取消了你的举手", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void banHand() {
                Toast.makeText(mContext, "老师禁止了课堂举手", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestCamera() {
                getPermisssion();
            }
        });

        /**
         * 老师给麦的回调
         */
        player.setMicRequestCallback(new MicRequestCallback() {
            @Override
            public void startSendMic() {
                Toast.makeText(mContext, "你的举手已经通过，当前可以发言", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closeMic() {
                Toast.makeText(mContext, "老师关闭了你的麦克风", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String mYqcode = "";

    private void getPermisssion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                pushCamera(StatusCode.OPEN_CAMERA_OK);
            } else {
                showPermissionTip();
            }
        } else {
            pushCamera(StatusCode.OPEN_CAMERA_OK);
        }
    }

    private void showPermissionTip() {
        new AlertDialog.Builder(this)
                .setTitle("权限通知")
                .setMessage("直播需要开启录音和拍照权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(CustomizedLiveActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 0);
                    }
                }).setCancelable(false).show();
    }

    public void initPlayerAndSetCallback() {
        //第一步，初始化player
        try {
            player = new LivePlayer(this.getApplicationContext(), mPlayerView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.setVideoCallback(this);
        player.setVideoPositionChange(this);
        mTeachersurface.setVisibility(View.GONE);
        useOpenglRenderVideo(); // 使用opengl 渲染视频,推荐使用此种方式优化渲染方式，减少cpu消耗
        player.setDbLocalVideoCallback(this);//本地视频开启与关闭回调
        int role = RoleType.ROLE_TYPE_STUDENT;//角色，目前Android端只有学生，直接设置RoleType.ROLE_TYPE_STUDENT
        RoomInfoBean roomInfoBean = null;
        if (!TextUtils.isEmpty(mYqcode)) {
            //推荐 : 邀请码的方式；进入教室
            roomInfoBean = new RoomInfoBean.RoomInfoTypeInviteCode(nickname, true, mYqcode).generateRoomInfoBean();
        } else if (!TextUtils.isEmpty(Url)) {
            //推荐 : 使用的进入教室的方式，通过服务端获取对应教室的authinfo信息。
            roomInfoBean = new RoomInfoBean.RoomInfoTypeURL(nickname, true, Url).generateRoomInfoBean();
        } else {
            //客户端传递参数的方式进入教室，该方式不建议在正式环境使用
            roomInfoBean = new RoomInfoBean.RoomInfoTypeRoomid(nickname, true, Constant.PID, Constant.APPKEY
                    , roomId, role, uid).generateRoomInfoBean();
        }
        player.setRoomInfoBean(roomInfoBean);
        player.setLiveMessageCallback(this);
        player.setFirstVideoFrameCallback(new FirstVideoFrameCallback() {
            @Override
            public void receiveVideoFirstFrame(int type, int handle) {
                //设置接受到视频帧第一帧的回调
            }
        });
    }

    private void useOpenglRenderVideo() {
        try {
            player.setTeacherFrameSurface(mTeachersurface);//远端老师的头像
            player.setStudentFrameSurface(mStudentSurface);//远端学生的头像
            player.setLocalVideoFrameSurface(localVideoSurface);//本地视频渲染头像
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.setOpenglSupport(new OpenglSupport() {
            @Override
            public void unSupportUseOpenGL(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showLongToast(CustomizedLiveActivity.this, message);
                        mTeachersurface.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private boolean showvideo = true;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.portrait_fullScreen) {
            changeScreen();
        } else if (v.getId() == R.id.portrait_back) {
            if (!this.isFinishing()) {
                this.finish();
            }
        } else if (v.getId() == R.id.btn_video_change) {
            showvideo = !showvideo;
            int value2 = player.openVideoRec(showvideo);
            log(value2 == 0 ? "举手消息发送到服务器" : "点击频率太过频繁");
        } else if (v.getId() == R.id.landscape_back) {
            changeScreen();
        } else if (v.getId() == R.id.chatSwitch) {
//            openChatInput();
            openMessageDialog();
        } else if (v.getId() == R.id.ib_live_answer) {
            //答题
            if (answerType == 30) {
                openJudgetAnswerDialog();
            } else if (answerType == 301) {
                if (mJudgetAnswerResultDialog != null && !mJudgetAnswerResultDialog.isShowing()) {
                    openJudgetAnswerResultDialog();
                }
            } else {
                if (answerType == 0) {
                    return;
                }
                if (!isAnswerd) {
                    openChooseAnswerDialog(answerType);
                } else {
                    if (isStopAnswer) {
                        openChooseAnswerResultDialog(true);
                        if (mMChooseResultDialog == null || !mMChooseResultDialog.isShowing()) {
                            openChooseAnswerResultDialog(false);
                        }
                        mMChooseResultDialog.getAnswerResult(mLastObject, answerType, mYourChooseOptions);

                    } else {

                        openChooseAnswerResultDialog(false);
                    }
                }
            }
//            openJudgetAnswerResultDialog();
        } else if (v.getId() == R.id.ib_live_handup) {
            //举手
            handup();
        } else if (v.getId() == R.id.btn_close_localvideo) {
            int value = player.closeLocalAudioVideo();
            log(value == 0 ? "btn_close_localvideo 点击成功" : "btn_close_localvideo 点击频繁");
        }
    }

    private void log(String msg) {
        Log.e("duobeiliveact", msg);
    }

    public void handup() {
        if (player != null) {
            int value3 = player.raiseHand();
            log(value3 == 0 ? "举手消息发送到服务器" : "点击频率太过频繁");
        }
    }

    /**
     * 聊天窗口
     */
    private void openMessageDialog() {
        if (isfullScreen) {
            return;
        }
        mInputMsgDialog = new MessageDialog(this, this);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputMsgDialog.getWindow().getAttributes();

        lp.width = display.getWidth() - 10; //设置宽度
        lp.gravity = Gravity.BOTTOM;
        mInputMsgDialog.getWindow().setAttributes(lp);
        mInputMsgDialog.setCancelable(true);
        mInputMsgDialog.setCanceledOnTouchOutside(true);
        mInputMsgDialog.show();

    }

    /**
     * 判断题dialog
     */
    private void openJudgetAnswerDialog() {
        if (isfullScreen) {
            return;
        }
        if (mJudgetAnswerDialog == null) {
            mJudgetAnswerDialog = new JudgetAnswerDialog(this);

        }

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mJudgetAnswerDialog.getWindow().getAttributes();

        lp.width = display.getWidth() - 10; //设置宽度
        mJudgetAnswerDialog.getWindow().setAttributes(lp);
        mJudgetAnswerDialog.setCancelable(true);
//        judgetAnswerDialog.setCanceledOnTouchOutside(true);
        mJudgetAnswerDialog.show();
    }

    /**
     * 判断题结果dialog
     */
    private void openJudgetAnswerResultDialog() {
        if (isfullScreen) {
            return;
        }
        if (mJudgetAnswerResultDialog == null) {

            mJudgetAnswerResultDialog = new JudgetAnswerResultDialog(this);
        }

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mJudgetAnswerResultDialog.getWindow().getAttributes();

        lp.width = display.getWidth() - 10; //设置宽度
        mJudgetAnswerResultDialog.getWindow().setAttributes(lp);
        mJudgetAnswerResultDialog.setCancelable(true);
//        judgetAnswerDialog.setCanceledOnTouchOutside(true);
        mJudgetAnswerResultDialog.show();
    }

    /**
     * 选择题dialog
     */
    private void openChooseAnswerDialog(int type) {
        if (isfullScreen) {
            return;
        }
        if (mMChooseAnswerDialog == null) {

            mMChooseAnswerDialog = new ChooseAnswerDialog(this);
        }

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mMChooseAnswerDialog.getWindow().getAttributes();

        lp.width = display.getWidth() - 10; //设置宽度
        mMChooseAnswerDialog.getWindow().setAttributes(lp);
        mMChooseAnswerDialog.setCancelable(true);
//        judgetAnswerDialog.setCanceledOnTouchOutside(true);
        mMChooseAnswerDialog.show();
        mMChooseAnswerDialog.drawAnswerDialog(type);
    }

    /**
     * 选择题dialog
     */
    private void openChooseAnswerResultDialog(boolean isStop) {
        if (isfullScreen) {
            return;
        }
        if (mMChooseResultDialog == null) {

            mMChooseResultDialog = new ChooseAnswerResultDialog(this);
        }

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mMChooseResultDialog.getWindow().getAttributes();

        lp.width = display.getWidth() - 10; //设置宽度
        mMChooseResultDialog.getWindow().setAttributes(lp);
        mMChooseResultDialog.setCancelable(true);
//        judgetAnswerDialog.setCanceledOnTouchOutside(true);
        mMChooseResultDialog.show();
        mMChooseResultDialog.stopAnswer(isStop);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeScreen(newConfig);
    }

    private boolean isfullScreen = false;

    private void changeScreen(Configuration newConfig) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isfullScreen = true;
            //sendDeliyMessage();
        } else {
            isfullScreen = false;
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            //横向显示
            landscapeNaviLayout.setVisibility(View.VISIBLE);
            landscapeCourseTitleText.setVisibility(View.VISIBLE);
//            //竖向隐藏
            portraitNaviLayout.setVisibility(View.GONE);
            portraitFullScreenBtn.setBackgroundResource(R.drawable.back);
            //聊天内容隐藏
            chatLayout.setVisibility(View.GONE);
//            chatSwitchBtn.setVisibility(View.GONE);
            mHandUpButton.setVisibility(View.GONE);
            mAnswerButton.setVisibility(View.GONE);
            mPlayerView.setLayoutParams(layoutParams);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //横向隐藏
            landscapeNaviLayout.setVisibility(View.GONE);
            landscapeCourseTitleText.setVisibility(View.GONE);
            //竖向显示
            portraitNaviLayout.setVisibility(View.VISIBLE);
            portraitFullScreenBtn.setBackgroundResource(R.drawable.fullscreen);
            //聊天内容显示
            chatLayout.setVisibility(View.VISIBLE);
            chatSwitchBtn.setVisibility(View.VISIBLE);
            mHandUpButton.setVisibility(View.VISIBLE);
            mAnswerButton.setVisibility(View.VISIBLE);
            //全屏按钮区域
            rlportraitfullScreen.setVisibility(View.VISIBLE);
            int width = layoutParams.width = CommonUtils.getScreenWidth(this);
            layoutParams.height = width * 3 / 4;
            mPlayerView.setLayoutParams(layoutParams);
        }

    }

    /**
     * 返回屏幕的方向
     *
     * @param context
     * @return true 为 portrait  false 为 landscape
     */
    public static boolean getOrientation(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    // 全屏显示（横屏显示）
    public void changeScreen() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (getOrientation(this)) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isfullScreen = true;
            mPlayerView.setLayoutParams(layoutParams);
        } else {
            isfullScreen = false;
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            int width = layoutParams.width = CommonUtils.getScreenWidth(this);
            layoutParams.height = width * 3 / 4;
            mPlayerView.setLayoutParams(layoutParams);
        }

    }

    /*******************直播相关的回调**********************/

    @Override
    public void loading() {//直播初始化加载，可以设置相应的加载提示
        load.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleContent(ChatBean chatBean) {//返回单条消息（一般是自己发出的消息）
        allChatList.add(0, chatBean);
        addChatItem();
    }

    @Override
    public void handleContent(ArrayList<ChatBean> list) {//放回别人发送过来的消息
        allChatList.addAll(list);
        addChatItem();
    }

    @Override
    public void handleClearChat() {//断线从新连接时候的相关操作（可以做清除聊天信息之类的）

    }

    @Override
    public void handleAnnounceMessage(String message) {//教室的公告信息的展示

    }

    @Override
    public void connected() {//连接成功状态
//        Toast.makeText(mContext, "连接成功", Toast.LENGTH_SHORT).show();
        load.setVisibility(View.GONE);
        Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectFail(String msg) {//连接失败的信息
        load.setVisibility(View.GONE);
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void kickoff() {//重复登录
        Toast.makeText(mContext, "你的账号子啊其他地方登录", Toast.LENGTH_SHORT).show();
        if (!CustomizedLiveActivity.this.isFinishing()) {
            isFirstRunning = true;
            CustomizedLiveActivity.this.finish();
        }

    }

    @Override
    public void networkNotConnected() {//网络未连接
        Toast.makeText(mContext, "网络未连接", Toast.LENGTH_SHORT).show();
    }

    /**
     * @param type 题型
     *             10  单选 AB两个选项
     *             11  单选 ABC三个选项
     *             12  单选 ABCD四个选项
     *             13  单选 ABCDE五个选项
     *             30  判断 √或×
     */
    @Override
    public void voteStart(int type) {//答题开始
        answerType = type;
        if (type == 30) {
            openJudgetAnswerDialog();
        } else {
            openChooseAnswerDialog(type);
        }
        isStopAnswer = false;
        mAnswerButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void voteEnd() {//答题结束
//        answerType = 0;
        if (mJudgetAnswerDialog != null) {
            if (mJudgetAnswerDialog.isShowing()) {
                mJudgetAnswerDialog.dismiss();
            }
        }
        if (mMChooseAnswerDialog != null) {
            if (mMChooseAnswerDialog.isShowing()) {
                mMChooseAnswerDialog.dismiss();

            }
        }
        isAnswerd = true;
        isStopAnswer = true;
    }

    @Override
    public void voteClose() {//答题关闭
        answerType = 0;
        if (mJudgetAnswerResultDialog != null) {
            if (mJudgetAnswerResultDialog.isShowing()) {
                mJudgetAnswerResultDialog.dismiss();
            }
        }
        if (mMChooseResultDialog != null) {
            if (mMChooseResultDialog.isShowing()) {
                mMChooseResultDialog.dismiss();
            }
        }
        isAnswerd = false;
        mAnswerButton.setVisibility(View.GONE);
    }

    /**
     * @param jsonObject 如果是两个选项的单选，则只需取得A,B选项值，三个选项的单选，取A,B,C的值，以此类推
     *                   如果是判断，则需A，B的值，A表示选择√的人数，B表示选择×的人数
     *                   {"A":0,"B":1,"C":0,"D":0,"E":0}
     */
    @Override
    public void voteInfo(JSONObject jsonObject) {//答题的结果信息
        mLastObject = jsonObject;
        if (isAnswerd) {
            if (answerType == 301) {
                if (mJudgetAnswerResultDialog == null || !mJudgetAnswerResultDialog.isShowing()) {
                    openJudgetAnswerResultDialog();
                }
                mJudgetAnswerResultDialog.getResultMessge(jsonObject, mYourChooseOptions);
            } else {
                if (mMChooseResultDialog == null || !mMChooseResultDialog.isShowing()) {
                    openChooseAnswerResultDialog(false);
                }
                mMChooseResultDialog.getAnswerResult(jsonObject, answerType, mYourChooseOptions);
            }

        }
    }

    @Override
    public void statusCode(int code, String msg) {//相应的状态吗的回调
        log("code:" + code + ",msg:" + msg);
        if (code == LiveMessage.START_DBY_RESULT) {
            load.setVisibility(View.GONE);
        } else if (code == StatusCode.FAILCAMERA) {
            // TODO: 2018/4/11 没有camera权限，可以在这对camera权限的重新申请
        } else if (code == StatusCode.FAILMIC) {
            // TODO: 2018/4/11 没有mic权限 ，可以再这对mic权限的申请
        } else if (code == StatusCode.TEACHER_HANDED_FINISH_CLASS) {
            // TODO: 2019/12/20 //老师主动下课
        } else if (code == StatusCode.CLOSE_AUDIO_VIDEO_RESULT) {
            // 客户端主动申请下台结果
        }
        if (code == StatusCode.APPS_CONNECT_FATAL) {
            // 网络情况非常差
            load.setVisibility(View.VISIBLE);
            if (player != null) {
                player.stopApi();
            }
            return;
        }
        /**
         * 访问的msg为默认SDK提供对外的文案，如果需要自定义处理，可以更具对应的状态吗做相应的修改
         */
        if (!"".equals(msg)) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
        if (code == StatusCode.STOPSUCCESS) {//api退出成功
            if (player != null) {
                load.setVisibility(View.GONE);
                player.release();
            }
        } else {
            // TODO: 2019/5/16 退出失败提示
        }
    }

   /* //移除exit
    @Override
    public void exit() {//退出直播

    }*/

    @Override
    public void teacherOnline() {//老师是否在线

    }

    @Override
    public void pptMessage(int currentpage, int totalpage) {//ppt的相关信息

    }

    @Override
    public void onlineUserCount(int countnumber) {//当前在线的人数

    }

    private void addChatItem() {
        teacherList.clear();
        for (ChatBean bean : allChatList) {
            if (bean.getRole() == RoleType.ROLE_TYPE_TEACHER) {
                teacherList.add(bean);
            }
        }
        notifyAdapter();
    }

    private void sendMessage2Main(int what, int arg1, int arg2, Object obj) {
        Message message = Message.obtain();
        message.what = what;
        message.arg1 = arg1;
        message.arg2 = arg2;
        message.obj = obj;
        mhandler.sendMessage(message);
    }

    /**
     * 更新adapter相关
     */
    private void notifyAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                int allMsgCount = allChatList.size();
                int teacherMsgCount = teacherList.size();
                allMsgBtn.setText(String.format("全部聊天记录(%d)", allMsgCount));
                teacherMsgBtn.setText(String.format("只看老师(%d)", teacherMsgCount));
            }
        });

    }

    /**
     * //开启当前activity
     *
     * @param context
     */
    public static void startCustomizedActivity(Context context, LiveInfo liveInfo) {
        Intent i = new Intent(context, CustomizedLiveActivity.class);
        i.putExtra(OPENLIVE, liveInfo);
        context.startActivity(i);
    }

    public static void startCustomizedActivity(Context context, String code) {
        Intent intent = new Intent(context, CustomizedLiveActivity.class);
        intent.putExtra(YQM, code);
        context.startActivity(intent);

    }

    /**
     * 发送消息
     *
     * @param s
     */
    public void sendMessage(String s) {
        if (player != null) {
            player.sendMessage(s);
        }
    }

    public void hidenMessageList(boolean hiden) {
        if (hiden) {
            chatList.setVisibility(View.GONE);

        } else {
            chatList.setVisibility(View.VISIBLE);
        }

    }

    public void vote(int an) {
        mYourChooseOptions = an;
        if (player != null) {
            player.vote(an);
        }
        if (answerType == 30) {
            answerType = 301;
        }
        isAnswerd = true;
    }

    @Override
    public void showvideo(int role) {
        ToastUtils.showLongToast(this, "打开摄像头显示");
        if (role == RoleType.ROLE_TYPE_TEACHER) {
            mTeachersurface.setVisibility(View.VISIBLE);
            mTeachersurface.onResume();
        } else if (role == RoleType.ROLE_TYPE_STUDENT) {
            mStudentSurface.setVisibility(View.VISIBLE);
            mStudentSurface.onResume();
        }
    }

    @Override
    public void hidenVideo(int role) {
        ToastUtils.showLongToast(this, "关闭摄像头显示");
        if (role == RoleType.ROLE_TYPE_TEACHER) {
            mTeachersurface.setVisibility(View.GONE);
            mTeachersurface.onPause();
        } else {
            mStudentSurface.setVisibility(View.GONE);
            mStudentSurface.onPause();
        }
    }

    @Override
    public void videoPositionChange(int position) {
        int video2Full = 1;
        if (position == video2Full) {
            // 全屏
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //更新ui，视频全屏
                }
            });
        } else {
            // 缩小
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //更新ui，PPT全屏
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean isStartLive = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isStartLive = false;
                    }
                }
                if (isStartLive) {
                    pushCamera(StatusCode.OPEN_CAMERA_OK);
                } else {
                    pushCamera(StatusCode.NO_CAMERA_PERMISSION);
                    Toast.makeText(this, "请到setting中手动给予权限", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void pushCamera(int code) {
        if (player != null) {
            /**
             * 该回调说明：
             *        收到该回调，（在android6.0以上需要检查是否有摄像头权限、麦克风权限。）询问用户是否开启视频连麦。
             *        然后调用 player.setUserCameraStatus(int);给sdk;告诉SDK当前用户的状态。
             *        如果用户允许上麦。则先传递给SDK用户的摄像头的surfaceview.调用setSurfaceView（）在调用setUserCameraStatus方法
             * 参数说明：
             *  StatusCode.OPEN_CAMERA_OK  用户允许打开摄像头
             *  StatusCode.USER_REFUSE     用户拒绝上麦
             *  StatusCode.NO_CAMERA_PERMISSION  用户没有没有摄像头权限
             */
            player.setUserCameraStatus(code);//如果学生允许上麦，传true,否则传递false;
        }
    }

    @Override
    public void showLocalVideo() {
        localVideoSurface.setVisibility(View.VISIBLE);
    }

    @Override
    public void closeLocalVideo() {
        localVideoSurface.setVisibility(View.GONE);
    }
}
