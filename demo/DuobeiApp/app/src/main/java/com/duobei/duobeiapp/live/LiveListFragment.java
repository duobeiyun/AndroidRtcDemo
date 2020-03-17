package com.duobei.duobeiapp.live;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dby.webrtc_1vn.activity.login.WebrtcInputInviteCodeActivity;
import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.bean.LiveInfo;
import com.duobei.duobeiapp.live.adapter.LiveAdapter;
import com.duobei.duobeiapp.webrtc.WebrtcEntranceActivity;
import com.duobeiyun.common.DBYHelper;

import java.util.ArrayList;
import java.util.List;

/***
 ***Create DuobeiApp by yangge at 2017/5/15 
 **/
public class LiveListFragment extends Fragment {

    private RecyclerView livereycycleView;
    LiveAdapter adapter;
    List<LiveInfo> list = new ArrayList<>();
    //模拟图片数组
    String[] imags = new String[]{"http://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1494860467543&di=c15204ede014ca857e45657b73599cda&imgtype=0&src=http%3A%2F%2Fpx.thea.cn%2FPublic%2FUpload%2F1597178%2FIntro%2F1446450700.png",
            "http://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1494860508499&di=a00c933aa06691005595455e1960d02e&imgtype=0&src=http%3A%2F%2Fpx.thea.cn%2FPublic%2FUpload%2F2982644%2FIntro%2F1474353531.jpg"
    };
    private EditText mEt_code;
    private Button mBtngoLive;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLiveList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_live_list, null);
        initView(view);
        return view;
    }


    private void initView(View view) {
        livereycycleView = (RecyclerView) view.findViewById(R.id.rv_live);
        mEt_code = (EditText) view.findViewById(R.id.et_ecode);
        mBtngoLive = (Button) view.findViewById(R.id.btn_go_live);
        livereycycleView.setAdapter(adapter = new LiveAdapter(this.getContext(), list));
        livereycycleView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mBtngoLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mEt_code.getText().toString().trim();
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(LiveListFragment.this.getContext(), "邀请码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                CustomizedLiveActivity.startCustomizedActivity(LiveListFragment.this.getActivity(), s);
            }
        });

        /**
         * 注意进入新版大班课程需要切换到net环境，此时sdk会整体切换到net环境中
         * 如果用户需要兼容com环境需要自己维护环境切换
         * 如果只使用某一种环境，则可以忽略
         * */
        view.findViewById(R.id.tv_webrtc_entrance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBYHelper.getInstance().switchDomain(true);
                Intent intent = new Intent(LiveListFragment.this.getContext(), WebrtcEntranceActivity.class);
                startActivity(intent);
            }
        });
    }

    //模拟直播列表数据
    public void getLiveList() {
        LiveInfo liveInfo1 = new LiveInfo("test", "jzf3a0ad046b62430280f9e41e4cfe8a9d", "1vn测试课程",
                true, imags[0], "test1", true);

        LiveInfo liveInfo3 = new LiveInfo("test2", "jz3014954bc6ae487e91875cb12fde7d21", "1v1测试课程",
                true, imags[1], "test1", false);

        list.add(liveInfo1);
        list.add(liveInfo3);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        list.clear();
    }

    public static LiveListFragment getInstance() {
        return new LiveListFragment();
    }

}
