package com.duobei.duobeiapp.webrtc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dby.webrtc_1vn.activity.CusOnlinePlaybackActivity;
import com.dby.webrtc_1vn.activity.LiveActivity;
import com.dby.webrtc_1vn.activity.WebrtcOfflinePlaybackActivity;
import com.dby.webrtc_1vn.constant.Global_const;
import com.duobei.duobeiapp.R;
import com.duobeiyun.common.DBYHelper;

/**
 * 新版大班教室入口：
 * 集成条件需要接入:多贝sdk即可，webrtc_1vn*.aar里面有默认的ui可以提供参考使用
 * 如果不需要使用的话，可以自己任意修改开发
 * 注意点：目前sdk支持com，net俩种环境，sdk默认使用的为com环境，用户需要自己维护在
 * 什么课程什么使用环境
 * 此demo主要提供进入默认ui的接入方式，如有问题联系多贝人员即可
 */
public class WebrtcEntranceActivity extends AppCompatActivity {


    private int type = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webrtc_entrance);
        DBYHelper.getInstance().switchDomain(true);
    }

    /**
     * 进入直播回放
     *
     * @param view
     */
    public void enterLiveActivity(View view) {
        //方式一{
        if (type == 1) {
            //邀请码需要从服务器获取，昵称作为测试必须填充，且不能为空
            String inviteCode = "code";
            String name = "duobeiTest";
            Intent intent = new Intent(WebrtcEntranceActivity.this, LiveActivity.class);
            intent.putExtra(Global_const.INVITIED_CODE, inviteCode);
            intent.putExtra(Global_const.NICK_NICKNAME, name);
            this.startActivity(intent);
            return;
        }else if(type==2){
            String url="url";//从服务器获取
            Intent intent = new Intent(WebrtcEntranceActivity.this, LiveActivity.class);
            intent.putExtra(Global_const.URL,url);
            intent.putExtra(Global_const.NICK_NICKNAME, "aa");
            getApplicationContext().startActivity(intent);
        }

    }

    /**
     * 进入在线回放
     *
     * @param view
     */
    public void enterOnlinePlaybackActivity(View view) {
        if (type == 1) {
            //邀请码需要从服务器获取，昵称作为测试必须填充，且不能为空
            String inviteCode = "jzqvr4828";
            String name = "duobeiTest";
            Intent intent = new Intent(WebrtcEntranceActivity.this, CusOnlinePlaybackActivity.class);
            intent.putExtra(Global_const.INVITIED_CODE, inviteCode);
            intent.putExtra(Global_const.NICK_NICKNAME, name);
            this.startActivity(intent);
            return;
        }else {
            String url="url";//从服务器获取
            Intent intent = new Intent(getApplicationContext(), CusOnlinePlaybackActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Global_const.URL, url);
            getApplicationContext().startActivity(intent);
        }
    }

    /**
     * 进入离线回放
     *
     * @param view
     */
    public void enterOfflinePlaybackActivity(View view) {
        //需要用户去下载
        // roomid 需要从服务器获取
        String roomId="roomid";
        //解密密钥需要从机构后台获取
        String appsecret="appsecret";

        Intent intent = new Intent(this, WebrtcOfflinePlaybackActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("roomId", roomId);
        bundle.putString("appsecret", appsecret);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }
}
