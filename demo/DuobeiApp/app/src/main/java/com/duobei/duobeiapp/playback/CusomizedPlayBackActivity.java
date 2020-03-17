package com.duobei.duobeiapp.playback;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.utils.Constant;
import com.duobei.duobeiapp.utils.ToastUtils;
import com.duobeiyun.bean.ChatBean;
import com.duobeiyun.callback.OpenglSupport;
import com.duobeiyun.callback.PlaybackMessageCallback;
import com.duobeiyun.callback.VideoCallback;
import com.duobeiyun.opengles.GLFrameSurface;
import com.duobeiyun.player.PlaybackPlayer;
import com.duobeiyun.player.RoomInfoBean;
import com.duobeiyun.type.RoleType;
import com.duobeiyun.type.StatusCode;
import com.duobeiyun.util.CommonUtils;
import com.duobeiyun.widget.JYVideoView;
import com.duobeiyun.widget.PlaybackPlayerView;

import java.util.ArrayList;
import java.util.Random;

public class CusomizedPlayBackActivity extends FragmentActivity implements PlaybackMessageCallback, View.OnClickListener, VideoCallback {
    private Context mContext;

    //竖向导航
    private RelativeLayout portraitNaviLayout;
    private Button portraitBackBtn; //竖向返回按钮
    //横向导航
    private RelativeLayout landscapeNaviLayout;
    private ImageButton landscapeBackBtn;//横向返回按钮
    private TextView landscapeCourseTitleText;//横向课程名称
    //ppt显示
    private RelativeLayout pptLayout;

    //竖向播放控制
    private RelativeLayout portraitPlaybackControlLayout;
    private ImageButton portraitControlBtn;//竖向播放控制
    private ImageButton portraitFullScreenBtn;//竖向进入全屏
    private SeekBar portraitSeekbar;//竖向进度
    private TextView portraitCurrentTimeText;//竖向当前时间
    private TextView portraitTotalTimeText;//竖向总时间
    private TextView portraitPlaybackSpeedBtn;//竖向回放速度控制
    //横向播放控制
    private RelativeLayout landscapePlaybackControlLayout;
    private ImageButton landscapeControlBtn;//横向播放控制
    private TextView landscapeCurrentTimeText;//横向当前时间
    private ImageButton landscapeFullScreenBtn;//横向退出全屏
    private TextView landscapePlaybackSpeedBtn;//横向回放速度控制
    private TextView landscapeTotalTimeText;//横向总时间
    private SeekBar landscapeSeekbar;//横向进度

    //聊天区域
    private RelativeLayout chatLayout;
    private ListView chatList;
    private RadioGroup chatGroup;
    private RadioButton allMsgBtn, teacherMsgBtn;
    private ProgressBar load;


    //    private String pid = "20160823160537323146";
//    private String appkey = "ece47bc1626e47b8953e2fee2a4adc45";//"17e9549286434910980d1b80305c2943";
    private String juid = Build.MANUFACTURER + Build.BOARD + new Random(100).nextInt();//uuid是全局唯一的id；不能包含特殊符号。
    private String jnickname = Build.MANUFACTURER + Build.BOARD;
    private String jroomId = "jzbf0a105d24334975a5a6574337957341";//"jzc6cb7da0b03e41bc9e4866d4142c6877"; //"jz113bde43264644fa87a134f9e9240d6c";//"jz2afcb6b2c788453c87f5c1cf6800a91b";
    private String onejroomId = "jzbfe0b350bc4c468bb1c41a917c9a79e1"; //"jz1ef7696f042e47f8a52956a8312c1d9c";//"jz2afcb6b2c788453c87f5c1cf6800a91b";
    private int playMod = 0;// 0在线，1离线
    private int jrole = 2;
    private int timeOut = 30000;
    private boolean isFirstRunning = true;
    private NetWorkReceiver netWorkReceiver;
    private PlaybackPlayer playbackPlayer;
    private boolean isPlayerStart = false;
    private LinearLayout videogroup;
    //    是否是1vn
    private boolean isoneToMore = false;
    private PlaybackPlayerView mPlaybackPlayerView;
    private GLFrameSurface mTeachersurface;
    private GLFrameSurface mStudentsurface;
    private boolean useOpenGl = true;
    private TextView mChangeUrl;
    private LinearLayout mLlUrlList;
    private String mYqcode = null;
    private String Url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_cusomized_playback);
        this.mContext = this.getApplicationContext();
        initView();
        initPlayer();
        OtherCallback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirstRunning) {
            if (playbackPlayer != null) {
                playbackPlayer.recovery();
            }
        }
        if (isFirstRunning) {
            isFirstRunning = false;
        }
    }

    @Override
    protected void onPause() {
        if (playbackPlayer != null) {
            playbackPlayer.pause();
        }
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playbackPlayer != null) {
            playbackPlayer.release();
            playbackPlayer = null;
        }
    }

    public void initView() {
        //竖向导航
        portraitNaviLayout = (RelativeLayout) findViewById(R.id.portrait_navi_layout);
        portraitBackBtn = (Button) findViewById(R.id.portrait_back); //竖向返回按钮
        portraitBackBtn.setOnClickListener(this);
        //横向导航
        landscapeNaviLayout = (RelativeLayout) findViewById(R.id.landscape_navi_layout);
        landscapeBackBtn = (ImageButton) findViewById(R.id.landscape_back);//横向返回按钮
        landscapeBackBtn.setOnClickListener(this);
        //课程名称
        landscapeCourseTitleText = (TextView) findViewById(R.id.landscape_course_title);//横向课程名称
        //ppt显示
        pptLayout = (RelativeLayout) findViewById(R.id.pptLayout);

        //竖向播放控制
        portraitPlaybackControlLayout = (RelativeLayout) findViewById(R.id.portrait_playback_control_layout);
        //竖直播放和暂停
        portraitControlBtn = (ImageButton) findViewById(R.id.portrait_control);//竖向播放控制
        portraitControlBtn.setOnClickListener(this);
        //竖直全屏
        portraitFullScreenBtn = (ImageButton) findViewById(R.id.portrait_fullScreen);//竖向进入全屏
        portraitFullScreenBtn.setOnClickListener(this);
        //竖直拖拽
        portraitSeekbar = (SeekBar) findViewById(R.id.portrait_seekBar);//竖向进度
        //竖直当前时间
        portraitCurrentTimeText = (TextView) findViewById(R.id.portrait_current_time);//竖向当前时间
        portraitCurrentTimeText.setText("00:00");
        //竖直总时间
        portraitTotalTimeText = (TextView) findViewById(R.id.portrait_total_time);//竖向总时间
        portraitTotalTimeText.setText("00:00");
        portraitPlaybackSpeedBtn = (TextView) findViewById(R.id.portrait_playback_speed);//竖向回放速度控制
        portraitPlaybackSpeedBtn.setOnClickListener(this);
        //横向播放控制
        landscapePlaybackControlLayout = (RelativeLayout) findViewById(R.id.landscape_playback_control_layout);
        //横向播放和暂停
        landscapeControlBtn = (ImageButton) findViewById(R.id.landscape_control);//横向播放控制
        landscapeControlBtn.setOnClickListener(this);
        //横向当前时间
        landscapeCurrentTimeText = (TextView) findViewById(R.id.landscape_current_time);//横向当前时间
        landscapeCurrentTimeText.setText("00:00");
        //横向退出全屏
        landscapeFullScreenBtn = (ImageButton) findViewById(R.id.landscape_exit_fullscreen);//横向退出全屏
        landscapeFullScreenBtn.setOnClickListener(this);
        //横向倍速
        landscapePlaybackSpeedBtn = (TextView) findViewById(R.id.landscape_playback_speed);//横向回放速度控制
        landscapePlaybackSpeedBtn.setOnClickListener(this);
        //横向总时间
        landscapeTotalTimeText = (TextView) findViewById(R.id.landscape_total_time);//横向总时间
        landscapeTotalTimeText.setText("00:00");
        //横向拖拽
        landscapeSeekbar = (SeekBar) findViewById(R.id.landscape_seekBar);//横向进度

        //播放器
        mPlaybackPlayerView = (PlaybackPlayerView) findViewById(R.id.backplayer);

        mChangeUrl = (TextView) findViewById(R.id.tv_change_url);
        mChangeUrl.setClickable(true);
        mChangeUrl.setOnClickListener(this);
        mLlUrlList = (LinearLayout) findViewById(R.id.ll_url_list);

        if (getOrientation(this)) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int width = layoutParams.width = CommonUtils.getScreenWidth(this);
            layoutParams.height = width * 3 / 4;
            mPlaybackPlayerView.setLayoutParams(layoutParams);
        }

        //聊天区域
        chatLayout = (RelativeLayout) findViewById(R.id.chat_layout);
        allMsgBtn = (RadioButton) findViewById(R.id.allMsg);
        teacherMsgBtn = (RadioButton) findViewById(R.id.teacherMsg);
        chatGroup = (RadioGroup) findViewById(R.id.chatGroup);

        chatList = (ListView) findViewById(R.id.chatList);

        load = (ProgressBar) findViewById(R.id.load);

        videogroup = (LinearLayout) findViewById(R.id.videogroup);
        if (!isoneToMore) {
            videogroup.setVisibility(View.VISIBLE);
            chatList.setVisibility(View.GONE);
        } else if (isoneToMore) {
            videogroup.setVisibility(View.GONE);
            chatList.setVisibility(View.VISIBLE);
        }
        mTeachersurface = (GLFrameSurface) findViewById(R.id.gl_teacher);
        mStudentsurface = (GLFrameSurface) findViewById(R.id.gl_student);
    }

    //初始化播放器
    public void initPlayer() {
        int screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams layoutParams = mTeachersurface.getLayoutParams();
        layoutParams.width = screenWidth / 2;
        mTeachersurface.setLayoutParams(layoutParams);
        mStudentsurface.setLayoutParams(layoutParams);
        try {
            playbackPlayer = new PlaybackPlayer(this.getApplicationContext(), mPlaybackPlayerView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        playbackPlayer.setVideoCallback(this);
        mTeachersurface.setVisibility(View.GONE);
        mStudentsurface.setVisibility(View.GONE);

        useOpenglRenderVideo(); // 使用opengl 渲染视频,推荐使用此种方式优化渲染方式，减少cpu消耗
        RoomInfoBean roomInfoBean = null;
        if (!TextUtils.isEmpty(mYqcode)) {
            roomInfoBean = new RoomInfoBean.RoomInfoTypeInviteCode(jnickname, true, mYqcode).generateRoomInfoBean();
        } else if (!TextUtils.isEmpty(Url)) {
            roomInfoBean = new RoomInfoBean.RoomInfoTypeURL(jnickname, true, Url).generateRoomInfoBean();
        } else {
            playbackPlayer.authInit(Constant.CHAPID, Constant.CHAAPPKEY);
            int role = RoleType.ROLE_TYPE_STUDENT;//角色，目前Android端只有学生，直接设置RoleType.ROLE_TYPE_STUDENT
            roomInfoBean = new RoomInfoBean.RoomInfoTypeRoomid(jnickname, true, Constant.CHAPID, Constant.CHAAPPKEY
                    , jroomId, role, juid).generateRoomInfoBean();
        }
        //统一使用接口设置room信息
        playbackPlayer.setRoomInfoBean(roomInfoBean);
        playbackPlayer.setPlaybackMessageCallback(this);
        playbackPlayer.setSeekBar(portraitSeekbar);
        playbackPlayer.setLanSeekBar(landscapeSeekbar);
//        int urlcount = playbackPlayer.getxxx();
        showplayUrlList(3);
    }

    private void useOpenglRenderVideo() {
        useOpenGl = true;
        playbackPlayer.setUseOpengl(useOpenGl);
        try {
            playbackPlayer.setTeacherFrameSurface(mTeachersurface);
            playbackPlayer.setStudentFrameSurface(mStudentsurface);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void OtherCallback() {
        chatGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

            }
        });
    }


    @Override
    public void loadStart() {//开始加载
        load.setVisibility(View.VISIBLE);
        pauseView();
    }

    @Override
    public void handleContent(ChatBean chatBean) {//返回单条聊天信息

    }

    @Override
    public void handleContent(ArrayList<ChatBean> list) {//放回聊天的集合（在回放中应该只有这个）

    }

    @Override
    public void networkNotConnected() {//网络未连接
        Toast.makeText(mContext, "网络未连接", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleClearChat() {//清理聊天信息

    }

    @Override
    public void kickoff() {//账号多处登录
        load.setVisibility(View.GONE);
    }

    @Override
    public void pptMessage(int currentpage, int totalpage) {

    }


    @Override
    public void connectFail(String s) {
        load.setVisibility(View.GONE);
        Toast.makeText(mContext, "" + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connected() {//加载完毕
        load.setVisibility(View.GONE);
        playView();
        isPlayerStart = true;
    }


    @Override
    public void statusCode(int code, String msg) {
        if (!"".equals(msg)) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
        if (code == StatusCode.TEACHER_HANDED_FINISH_CLASS) {
//老师主动下课
        }
        switch (code) {
            case StatusCode.APPS_CONNECT_FATAL:
                // 网络情况非常差
                load.setVisibility(View.VISIBLE);
                finish();
                break;
            case StatusCode.PB_LOCALPATH_ERR:
            case StatusCode.PLAYBACK_DATA_OPEN_FAIL:
            case StatusCode.PLAYBACK_DATA_EMPTY_CONTENT: {
                //下载文件失败建议切换线路
            }
            break;
            case StatusCode.STOPSUCCESS: {
                load.setVisibility(View.GONE);
                playbackPlayer.release();
                showFinishTip();
            }
        }
    }

    private void showFinishTip() {
        new AlertDialog.Builder(this)
                .setTitle("正常退出教室")
                .setMessage("正常程序退出教室，sdk已经销毁，必须点击确定，退出重进")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!CusomizedPlayBackActivity.this.isFinishing()) {
                            CusomizedPlayBackActivity.this.finish();
                        }
                    }
                }).setCancelable(false).show();
    }

    @Override
    public void getTotalTime(String totalTime) {
        portraitTotalTimeText.setText(totalTime);
        landscapeTotalTimeText.setText(totalTime);
    }

    @Override
    public void currentTime(String playTime) {
        landscapeCurrentTimeText.setText(playTime);
        portraitCurrentTimeText.setText(playTime);
    }

    @Override
    public void playPuase(boolean isplay) {
        if (isplay) {
            playView();
        } else {
            pauseView();
        }
    }

    @Override
    public void playFinish() {
        landscapeCurrentTimeText.setText("00:00");
        portraitCurrentTimeText.setText("00:00");
        isPlayerStart = false;
    }

    public void pauseView() {
        landscapeControlBtn.setBackgroundResource(R.drawable.play);
        portraitControlBtn.setBackgroundResource(R.drawable.play);
    }

    public void playView() {
        landscapeControlBtn.setBackgroundResource(R.drawable.stop);
        portraitControlBtn.setBackgroundResource(R.drawable.stop);
    }

    private float speed = 1.0f;
    boolean isplay = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.portrait_back:
            case R.id.landscape_back:
                destroyPlayer();
                break;
            case R.id.portrait_fullScreen:
            case R.id.landscape_exit_fullscreen:
                changeScreen();
                break;
            case R.id.portrait_playback_speed:
            case R.id.landscape_playback_speed: {
                if (speed == 1.0f) {
                    speed = 2.0f;
                } else {
                    speed = 1.0f;
                }
                playbackPlayer.setSpeedPlay(speed);
                landscapePlaybackSpeedBtn.setText(speed + "倍速");
                portraitPlaybackSpeedBtn.setText(speed + "倍速");
            }
            break;
            case R.id.landscape_control:
            case R.id.portrait_control: {
                if (!isPlayerStart) {
                    playbackPlayer.startPlayback();
                } else {
                    isplay = !isplay;
                    playbackPlayer.play(isplay);
                }
            }
            break;
            case R.id.tv_change_url: {
                int counts = playbackPlayer.getCanChangeUrlCounts();
                if (counts == 0) {
                    Toast.makeText(CusomizedPlayBackActivity.this, "没有可以切换的线路 : ", Toast.LENGTH_LONG).show();
                    return;
                }
                if (currentRounte >= counts) {
                    currentRounte = 0;
                }
                playbackPlayer.changePlaybackURL(currentRounte);
                Toast.makeText(CusomizedPlayBackActivity.this, "在线回放已经切换 : " + currentRounte, Toast.LENGTH_LONG).show();
                currentRounte++;
                speed = 1.0f;
            }
            break;
        }
    }

    private int currentRounte = 0;//当前线路

    //播放源的列表
    private void showplayUrlList(int count) {
        for (int i = 0; i < count; i++) {
            TextView textView = new TextView(this);
            textView.setText("线路" + i);
            mLlUrlList.addView(textView);

        }
        mLlUrlList.setVisibility(View.GONE);


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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (playbackPlayer != null) {

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //横向显示
                landscapeNaviLayout.setVisibility(View.VISIBLE);
                landscapePlaybackControlLayout.setVisibility(View.VISIBLE);
                landscapeCourseTitleText.setVisibility(View.VISIBLE);
                //竖向隐藏
                portraitNaviLayout.setVisibility(View.GONE);
                portraitFullScreenBtn.setBackgroundResource(R.drawable.back);
                portraitPlaybackControlLayout.setVisibility(View.GONE);
                portraitPlaybackSpeedBtn.setVisibility(View.GONE);
                //聊天内容隐藏
                chatLayout.setVisibility(View.GONE);
                mPlaybackPlayerView.setLayoutParams(layoutParams);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                //横向隐藏
                landscapeNaviLayout.setVisibility(View.GONE);
                landscapePlaybackControlLayout.setVisibility(View.GONE);
                landscapeCourseTitleText.setVisibility(View.GONE);
                //竖向显示
                portraitNaviLayout.setVisibility(View.VISIBLE);
                portraitFullScreenBtn.setBackgroundResource(R.drawable.fullscreen);
                portraitPlaybackSpeedBtn.setVisibility(View.VISIBLE);
                portraitPlaybackControlLayout.setVisibility(View.VISIBLE);
                //聊天内容显示
                chatLayout.setVisibility(View.VISIBLE);


                int width = layoutParams.width = CommonUtils.getScreenWidth(this);
                layoutParams.height = width * 3 / 4;
                mPlaybackPlayerView.setLayoutParams(layoutParams);
            }
        }
    }

    // 全屏显示（横屏显示）
    public void changeScreen() {
        if (getOrientation(this)) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static void startCusomizedPlayBackActivity(Context context) {
        Intent intent = new Intent(context, CusomizedPlayBackActivity.class);
        context.startActivity(intent);

    }

    class NetWorkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                NetworkInfo[] infos = manager.getAllNetworkInfo();
                if (infos != null) {
                    for (int i = 0; i < infos.length; i++) {
                        NetworkInfo.State state = infos[i].getState();
                        if (NetworkInfo.State.CONNECTED == state) {
                            return;
                        }
                    }
                }
            }
            if (playbackPlayer != null) {
                playbackPlayer.clear();
            }
        }
    }

    @Override
    public void showvideo(int role) {
        ToastUtils.showLongToast(this, "打开摄像头显示");
        if (useOpenGl) {
            if (role == RoleType.ROLE_TYPE_TEACHER) {
                mTeachersurface.onResume();
                mTeachersurface.setVisibility(View.VISIBLE);
            } else {
                mStudentsurface.onResume();
                mStudentsurface.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void hidenVideo(int role) {
        ToastUtils.showLongToast(this, "关闭摄像头显示");
        if (useOpenGl) {
            if (role == RoleType.ROLE_TYPE_TEACHER) {
                mTeachersurface.onPause();
                mTeachersurface.setVisibility(View.GONE);
            } else {
                mStudentsurface.onPause();
                mStudentsurface.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            destroyPlayer();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void destroyPlayer() {
        load.setVisibility(View.VISIBLE);
        if (playbackPlayer != null) {
            playbackPlayer.stopPlayback();
        }
    }
}
