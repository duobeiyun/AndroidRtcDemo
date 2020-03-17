package com.duobei.duobeiapp.playback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.bean.PlaybackInfo;
import com.duobei.duobeiapp.playback.adapter.PlaybackAdapter;

import java.util.ArrayList;
import java.util.List;

/***
 ***Create DuobeiApp by yangge at 2017/5/15 
 **/
public class PlaybackListFragment extends Fragment {

    private RecyclerView recyclePlayback;
    private PlaybackAdapter adapter;
    private List<PlaybackInfo> list = new ArrayList<>();
    //模拟图片数组
    String[] imags = new String[]{"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1494860467543&di=c15204ede014ca857e45657b73599cda&imgtype=0&src=http%3A%2F%2Fpx.thea.cn%2FPublic%2FUpload%2F1597178%2FIntro%2F1446450700.png",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1494860508499&di=a00c933aa06691005595455e1960d02e&imgtype=0&src=http%3A%2F%2Fpx.thea.cn%2FPublic%2FUpload%2F2982644%2FIntro%2F1474353531.jpg"

    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPlaybackInfoList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_playback_list, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclePlayback = (RecyclerView) view.findViewById(R.id.rv_playback);
        recyclePlayback.setAdapter(adapter = new PlaybackAdapter(this.getContext(), list));
        recyclePlayback.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void getPlaybackInfoList() {
        PlaybackInfo info1 = new PlaybackInfo("info1", "jz00942c610ce6464ead7c7c72d4e8b0eb", "原生在线回放", "4:23:56", true, imags[1], true);
        PlaybackInfo info2 = new PlaybackInfo("info2", "jz00942c610ce6464ead7c7c72d4e8b0eb", "h5在线回放", "2:39:56", false, imags[0], false);
        list.add(info1);
        list.add(info2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        list.clear();
    }

    public static PlaybackListFragment getInstance() {
        return new PlaybackListFragment();
    }
}
