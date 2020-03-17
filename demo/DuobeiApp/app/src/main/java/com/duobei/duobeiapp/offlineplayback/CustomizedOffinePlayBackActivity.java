package com.duobei.duobeiapp.offlineplayback;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.live.adapter.ChatAdapter;
import com.duobei.duobeiapp.utils.Constant;
import com.duobeiyun.bean.ChatBean;
import com.duobeiyun.callback.OfflinePlaybackMessageCallback;
import com.duobeiyun.callback.VideoCallback;
import com.duobeiyun.util.CommonUtils;
import com.duobeiyun.widget.OfflinePlaybackPlayer;
import com.duobeiyun.widget.OfflinePlayerView;
import com.duobeiyun.widget.offplayer_base.PlayerView;

import java.util.ArrayList;
import java.util.List;

/**
 离线回放
 */
public class CustomizedOffinePlayBackActivity extends FragmentActivity implements View.OnClickListener, OfflinePlaybackMessageCallback , VideoCallback {
    private Context mContext;
    private OfflinePlaybackPlayer player;

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

    //房间相关的
    private String roomId="jz21e173a5ee914f8ebff89dd5ab55688e", desKey = "MuAbYxEy";
    private ProgressBar load;
    private boolean isPlayerStart = false; // 判断是否开始播放
    private boolean isPlaying = false;//判断是否正在播放
    private OfflinePlayerView offlinePlayerView;
    private ChatAdapter adapter;
    private List<ChatBean> allChatList = new ArrayList();
    private FrameLayout videoFrame;
    private PlayerView studentVideoPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_customized_offine_play_back);
        this.mContext = this;
        initView();
        otherCallback();
        initPlayer();
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
        //竖直总时间
        portraitTotalTimeText = (TextView) findViewById(R.id.portrait_total_time);//竖向总时间
        portraitPlaybackSpeedBtn = (TextView) findViewById(R.id.portrait_playback_speed);//竖向回放速度控制
        portraitPlaybackSpeedBtn.setOnClickListener(this);
        //横向播放控制
        landscapePlaybackControlLayout = (RelativeLayout) findViewById(R.id.landscape_playback_control_layout);
        //横向播放和暂停
        landscapeControlBtn = (ImageButton) findViewById(R.id.landscape_control);//横向播放控制
        landscapeControlBtn.setOnClickListener(this);
        //横向当前时间
        landscapeCurrentTimeText = (TextView) findViewById(R.id.landscape_current_time);//横向当前时间
        //横向退出全屏
        landscapeFullScreenBtn = (ImageButton) findViewById(R.id.landscape_exit_fullscreen);//横向退出全屏
        landscapeFullScreenBtn.setOnClickListener(this);
        //横向倍速
        landscapePlaybackSpeedBtn = (TextView) findViewById(R.id.landscape_playback_speed);//横向回放速度控制
        landscapePlaybackSpeedBtn.setOnClickListener(this);
        //横向总时间
        landscapeTotalTimeText = (TextView) findViewById(R.id.landscape_total_time);//横向总时间
        //横向拖拽
        landscapeSeekbar = (SeekBar) findViewById(R.id.landscape_seekBar);//横向进度
        //聊天区域
        chatLayout = (RelativeLayout) findViewById(R.id.chat_layout);
        allMsgBtn = (RadioButton) findViewById(R.id.allMsg);
        teacherMsgBtn = (RadioButton) findViewById(R.id.teacherMsg);
        chatGroup = (RadioGroup) findViewById(R.id.chatGroup);

        chatList = (ListView) findViewById(R.id.chatList);
        load = (ProgressBar) findViewById(R.id.load);
        offlinePlayerView = (OfflinePlayerView) findViewById(R.id.player);
        player = new OfflinePlaybackPlayer(this.getApplicationContext(), offlinePlayerView);
        studentVideoPlayer = (PlayerView) findViewById(R.id.pv_student_video_player);
        videoFrame = (FrameLayout) findViewById(R.id.player_view);
        if (getOrientation(this)) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int width = layoutParams.width = CommonUtils.getScreenWidth(this);
            layoutParams.height = width * 3 / 4;
            layoutParams.width = width;
            offlinePlayerView.setLayoutParams(layoutParams);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isPlaying) {
            player.pause();
            pauseView();
        } else {
            player.play();
            playView();
        }
        isPlaying = !isPlaying;
    }

    @Override
    protected void onPause() {
        if (player != null) {
            player.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.destroy();
            player = null;
        }

    }

    public void otherCallback() {
        chatGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

            }
        });
    }
    public static void startCustomizedOffinePlayBackActivity(Context context, String roomId) {
        Intent intent = new Intent(context, CustomizedOffinePlayBackActivity.class);
        intent.putExtra("roomid", roomId);
        context.startActivity(intent);

    }
    public void initPlayer() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            roomId = bundle.getString("roomid");
        }
        player.init(Constant.DES_KEY);//des_key请向商务获取
        player.setVideoVideoContainer(videoFrame);
        player.setCallback(roomId, this);//roomId；即下载资源的课程的roomId;
        player.setSeekBar(portraitSeekbar);
        player.setLanSeekBar(landscapeSeekbar);
        player.setVideoCallback(this);
        player.setStudentVideoContainer(studentVideoPlayer);
        player.setWeaterMarkText("11", 1000);
    }

    private float speed = 1.0f;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.portrait_back || v.getId() == R.id.landscape_back) {
            if (!this.isFinishing()) {
                this.finish();
            }
        } else if (v.getId() == R.id.portrait_fullScreen || v.getId() == R.id.landscape_exit_fullscreen) {
            changeScreen();
        } else if (v.getId() == R.id.portrait_playback_speed || v.getId() == R.id.landscape_playback_speed) {
            if (speed == 1.0f) {
                speed = 2.0f;
            } else {
                speed = 1.0f;
            }
            player.setPlaybackSpeed(speed);
            landscapePlaybackSpeedBtn.setText(speed + "倍速");
            portraitPlaybackSpeedBtn.setText(speed + "倍速");
        } else if (v.getId() == R.id.landscape_control || v.getId() == R.id.portrait_control) {
            if (isPlaying) {
                player.pause();
                pauseView();
            } else {
                player.play();
                playView();
            }
            isPlaying = !isPlaying;
        }

    }

    private void pauseView() {
        landscapeControlBtn.setBackgroundResource(R.drawable.play);
        portraitControlBtn.setBackgroundResource(R.drawable.play);
    }

    private void playView() {
        landscapeControlBtn.setBackgroundResource(R.drawable.stop);
        portraitControlBtn.setBackgroundResource(R.drawable.stop);
    }

    @Override
    public void handleContentList(List<ChatBean> list, boolean b) {

    }

    @Override
    public void handleContent(ChatBean chatBean) {

    }

    @Override
    public void handleErrorMessage(Exception exception) {

    }

    @Override
    public void loadStart() {
        load.setVisibility(View.VISIBLE);

    }

    @Override
    public void loadFinish() {
        load.setVisibility(View.GONE);

    }

    @Override
    public void playFinish() {
        Toast.makeText(mContext, "播放完毕", Toast.LENGTH_SHORT).show();
        isPlaying = false;
        load.setVisibility(View.GONE);
    }

    @Override
    public void statusCode(int code) {

    }

    @Override
    public void exit() {

    }

    @Override
    public void currentTime(String timeStr, int progress) {
        landscapeCurrentTimeText.setText(timeStr);
        portraitCurrentTimeText.setText(timeStr);

    }

    @Override
    public void initPPT(String duration, String totalPage, String currentPage) {
        portraitTotalTimeText.setText(duration);
        landscapeTotalTimeText.setText(duration);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
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
            offlinePlayerView.setLayoutParams(layoutParams);
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
            offlinePlayerView.setLayoutParams(layoutParams);
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
    public void showvideo(int i) {

    }

    @Override
    public void hidenVideo(int i) {

    }
}
