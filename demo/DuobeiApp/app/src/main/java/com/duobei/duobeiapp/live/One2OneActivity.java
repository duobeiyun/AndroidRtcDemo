package com.duobei.duobeiapp.live;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.bean.LiveInfo;
import com.duobei.duobeiapp.live.adapter.ChatAdapter;
import com.duobei.duobeiapp.utils.Constant;
import com.duobei.duobeiapp.utils.ToastUtils;
import com.duobeiyun.bean.ChatBean;
import com.duobeiyun.callback.DBLocalVideoCallback;
import com.duobeiyun.callback.LiveMessageCallback;
import com.duobeiyun.callback.OpenglSupport;
import com.duobeiyun.callback.VideoCallback;
import com.duobeiyun.opengles.GLFrameSurface;
import com.duobeiyun.player.LivePlayer;
import com.duobeiyun.type.RoleType;
import com.duobeiyun.type.StatusCode;
import com.duobeiyun.widget.JYVideoView;
import com.duobeiyun.widget.LivePlayerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 原生1v1直播
 */
public class One2OneActivity extends FragmentActivity implements View.OnClickListener, LiveMessageCallback, VideoCallback, DBLocalVideoCallback {

    private static final String TAG = One2OneActivity.class.getSimpleName();
    private static final String LIVEINFO = "liveInfo";
    private String roomId = "jzc02362d7644148fab3b994cbfeffa730";//"jzf66e00bd032747dba84bdf9af3d19e74";//"jzc02362d7644148fab3b994cbfeffa730";
    private String nickname = "zhangsan";//nickname开发者也需要自己维护
    private String uid = "fdafdafdaf8jfda8felr32erer32rewr32r"; //uid需要开发者自己维护
    private LivePlayer nativePlayer;
    private EditText msgEdit;
    private Button sendBtn;
    private String message;
    private RelativeLayout sendMsgLayout;
    private boolean isFirstRunning = true;
    private int role = RoleType.ROLE_TYPE_STUDENT;
    private NetWorkReceiver netWorkReceiver;
    private IntentFilter filter;
    private DrawerLayout mDrawerLayout;
    private ViewGroup mLeftDrawer;
    private ListView listView;
    private ChatAdapter adapter;
    private ActionBarDrawerToggle drawerToggle;
    private int cameraId = 1;
    private List<ChatBean> chatBeanList = new ArrayList<>(30);
    private List<ChatBean> teacherList = new ArrayList(30);
    private Handler chatHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        List<ChatBean> chatList = bundle.getParcelableArrayList("list");
                        for (ChatBean bean : chatList) {
                            chatBeanList.add(0, bean);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case 0x02://单条记录
                    ChatBean chatBean = (ChatBean) msg.obj;
                    chatBeanList.add(0, chatBean);
                    adapter.notifyDataSetChanged();
                    break;
                case 0x03://清空记录
                    chatBeanList.clear();
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private Button exchangeCamera;
    private LivePlayerView mPlayerView;
    private GLFrameSurface mTeachersurface;
    private GLFrameSurface studentLocalVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_2_one);
        registerReceiver();
        init();
        getEx();
        initPlayer();
        getPermisssion();
    }

    private void registerReceiver() {
        filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        netWorkReceiver = new NetWorkReceiver();
        registerReceiver(netWorkReceiver, filter);
    }

    private void init() {
        sendMsgLayout = (RelativeLayout) findViewById(R.id.sendMsgLayout);
        msgEdit = (EditText) findViewById(R.id.msgEdit);
        sendBtn = (Button) findViewById(R.id.sendMsg);
        sendBtn.setOnClickListener(this);
        studentLocalVideoView = (GLFrameSurface)findViewById(R.id.student);
        studentLocalVideoView.setZOrderOnTop(true);
        studentLocalVideoView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        listView = (ListView) findViewById(R.id.chatList);
        mPlayerView = (LivePlayerView) findViewById(R.id.nativeplayer);
        adapter = new ChatAdapter(this, chatBeanList, teacherList);
        listView.setAdapter(adapter);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawer = (ViewGroup) findViewById(R.id.left_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string._1v1Online, R.string._1vnOnline) {
            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        exchangeCamera = (Button) findViewById(R.id.btn_exchangecamera);
        exchangeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nativePlayer != null) {
                    cameraId = 1 - cameraId;
                    // nativePlayer.exchangeCamera(cameraId);
                }
            }
        });
        mTeachersurface = (GLFrameSurface) findViewById(R.id.gl_teacher);
    }

    private void getEx() {
        Intent intent = getIntent();
        if (intent != null) {
            LiveInfo liveInfo = (LiveInfo) intent.getSerializableExtra(LIVEINFO);
            this.uid = liveInfo.getUuid();
            this.roomId = liveInfo.getRoomId();
            this.nickname = liveInfo.getNickname();
        }
    }

    private void initPlayer() {
        try {
            nativePlayer = new LivePlayer(this.getApplicationContext(), mPlayerView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        nativePlayer.setVideoCallback(this);
        mTeachersurface.setVisibility(View.GONE);
        nativePlayer.setDbLocalVideoCallback(this);//本地视频开启与关闭回调
        try {
            //设置学生的显示区域
            nativePlayer.setLocalVideoFrameSurface(studentLocalVideoView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        useOpenglRenderVideo(); // 使用opengl 渲染视频,推荐使用此种方式优化渲染方式，减少cpu消耗

        nativePlayer.setAuth(Constant.PID, Constant.APPKEY);
        nativePlayer.setPlayInfo(uid, roomId, nickname, role, false, this);
        nativePlayer.canClientDraw(true);
        nativePlayer.setWeaterMarkText("哈哈", 5000);

    }

    private void useOpenglRenderVideo() {
        nativePlayer.setUseOpengl(true);
        try {
            //设置老师的显示区域
            nativePlayer.setTeacherFrameSurface(mTeachersurface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
            mDrawerLayout.closeDrawer(mLeftDrawer);
        } else {
            mDrawerLayout.openDrawer(mLeftDrawer);
        }
        return super.onOptionsItemSelected(item);
    }

    //查看权限
    private void getPermisssion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startLive();
            } else {
                showPermissionTip();
            }
        } else {
            startLive();
        }
    }

    //开启直播
    private void startLive() {
        if (nativePlayer != null) {
            try {
                nativePlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showPermissionTip() {
        new AlertDialog.Builder(this)
                .setTitle("权限通知")
                .setMessage("直播需要开启录音和拍照权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(One2OneActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 0);
                    }
                }).setCancelable(false).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startLive();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMsg:
                sendMessage();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            One2OneActivity.this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void sendMessage() {   //客户端发送消息
        if (!TextUtils.isEmpty(msgEdit.getText().toString().replace(" ", ""))) {
            message = msgEdit.getText().toString();
            nativePlayer.sendMessage(message);
            msgEdit.setText("");
        }
    }

    @Override
    protected void onPause() {
        if (nativePlayer != null) {
            nativePlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d("ygs", "destory");
        unregisterReceiver(netWorkReceiver);
        if (nativePlayer != null) {
            nativePlayer.release();
            nativePlayer = null;
        }
        if (chatHandler != null) {
            chatHandler.removeCallbacksAndMessages(null);
            chatHandler = null;
        }
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirstRunning) {
            if (nativePlayer != null) {
                nativePlayer.recovery();
            }
        }
        if (isFirstRunning) {
            isFirstRunning = false;
        }
    }

    @Override
    public void handleContent(ChatBean chatBean) { //返回一条直播聊天消息
        Message message = Message.obtain();
        message.what = 0x02;
        message.obj = chatBean;
        chatHandler.sendMessage(message);
    }

    @Override
    public void handleContent(ArrayList<ChatBean> list) { //初始化聊天消息列表
        Message message = Message.obtain();
        message.what = 0x01;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", list);
        message.setData(bundle);
        chatHandler.sendMessage(message);
    }

    @Override
    public void handleClearChat() { //清空聊天消息
        Message message = Message.obtain();
        message.what = 0x03;
        chatHandler.sendMessage(message);
    }

    @Override
    public void handleAnnounceMessage(String s) {

    }

    @Override
    public void connected() {

    }

    @Override
    public void connectFail(String s) {

    }


    @Override
    public void loading() {
        Toast.makeText(this, R.string.loading, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void kickoff() {  //重复登录
        Toast.makeText(this, R.string.account_repeat_login, Toast.LENGTH_SHORT).show();
        One2OneActivity.this.finish();
    }

    @Override
    public void networkNotConnected() {

    }

    @Override
    public void voteStart(int i) {

    }

    @Override
    public void voteEnd() {

    }

    @Override
    public void voteClose() {

    }

    @Override
    public void voteInfo(JSONObject jsonObject) {

    }

    @Override
    public void statusCode(int code, String msg) { //客户端状态回调
        //主要针对1v1
        if (code == StatusCode.FAILCAMERA) {
            // TODO: 2018/4/11 没有camera权限，可以在这对camera权限的重新申请
        }
        if (code == StatusCode.FAILMIC) {
            // TODO: 2018/4/11 没有mic权限 ，可以再这对mic权限的申请
        }
        /**
         * 访问的msg为默认SDK提供对外的文案，如果需要自定义处理，可以更具对应的状态吗做相应的修改
         */
        if (!"".equals(msg)) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void teacherOnline() {

    }

    @Override
    public void pptMessage(int arg2, int arg1) {

    }

    @Override
    public void onlineUserCount(int arg1) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            getWindow().setBackgroundDrawable(null);
        }
    }

    @Override
    public void showLocalVideo() {
        studentLocalVideoView.setVisibility(View.VISIBLE);
    }

    @Override
    public void closeLocalVideo() {
        studentLocalVideoView.setVisibility(View.GONE);
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
            if (nativePlayer != null) {
                nativePlayer.clear();
            }
        }
    }

    @Override
    public void showvideo(int role) {
        ToastUtils.showLongToast(this, "打开摄像头显示");
        if (role == RoleType.ROLE_TYPE_TEACHER) {
            mTeachersurface.setVisibility(View.VISIBLE);
        } else {
            studentLocalVideoView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidenVideo(int role) {
        ToastUtils.showLongToast(this, "关闭摄像头显示");
        if (role == RoleType.ROLE_TYPE_TEACHER) {
            mTeachersurface.setVisibility(View.GONE);
        } else {
            studentLocalVideoView.setVisibility(View.GONE);
        }
    }

    public static void openOne2OneActivity(Context context, LiveInfo liveInfo) {
        Intent intent = new Intent();
        intent.putExtra(LIVEINFO, liveInfo);
        intent.setClass(context, One2OneActivity.class);
        context.startActivity(intent);

    }
}
