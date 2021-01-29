package com.duobei.duobeiapp.download;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.control.TasksManager;
import com.duobei.duobeiapp.download.adapter.DownloadFileAdapter;
import com.duobei.duobeiapp.download.bean.TaskBean;
import com.duobei.duobeiapp.utils.CommonUtils;
import com.duobei.duobeiapp.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/***
 ***Create duobeisdk by yangge at 2017/5/8 
 * 注意：
 *      离线的ui资源是从下载的文件中获取的
 **/
public class DownloadFragment extends Fragment {

    private TextView localVideo;
    private TextView videoEdit;
    private TextView sdSize;
    private RecyclerView donwloadRecycle;
    DownloadFileAdapter adapter;
    private RelativeLayout lltopeditcontainer;
    private TextView tvchoose;
    private TextView tvdone;
    private RelativeLayout lltop;
    private TextView tvdeleteTask;
    private Map<Integer, TaskBean> videoMap = new HashMap<>();
    private ProgressBar load;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_download, null);
        initView(view);
        initclickListener();
        return view;
    }

    private void initView(View viewroot) {
        localVideo = (TextView) viewroot.findViewById(R.id.tv_re_video);
        videoEdit = (TextView) viewroot.findViewById(R.id.tv_re_video_edit);
        sdSize = (TextView) viewroot.findViewById(R.id.tv_sd_size);
        videoEdit.setEnabled(true);
        localVideo.setText("已缓存视频(" + TasksManager.getImpl().getLocalCount() + ")");
        sdSize.setText("占用空间" + Formatter.formatFileSize(getContext().getApplicationContext(),
                CommonUtils.getFileSize2(new File(CommonUtils.filePath)))
                + ",可用空间" + CommonUtils.getCanUseSize(getContext().getApplicationContext()));

        TasksManager.getImpl().getAllDownloadTask();
        donwloadRecycle = (RecyclerView) viewroot.findViewById(R.id.rc_download_list);
        donwloadRecycle.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        donwloadRecycle.setAdapter(adapter = new DownloadFileAdapter(this.getContext()));

        lltopeditcontainer = (RelativeLayout) viewroot.findViewById(R.id.ll_top_fored);
        lltop = (RelativeLayout) viewroot.findViewById(R.id.ll_top);
        tvchoose = (TextView) viewroot.findViewById(R.id.tv_re_video_fored);
        tvchoose.setClickable(true);
        tvdone = (TextView) viewroot.findViewById(R.id.tv_re_video_edit_fored);
        tvdone.setClickable(true);
        tvdeleteTask = (TextView) viewroot.findViewById(R.id.tv_delete_task);
        tvdeleteTask.setClickable(true);
        load = (ProgressBar) viewroot.findViewById(R.id.load);
    }

    boolean chooseAll = false;

    private void initclickListener() {
        videoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //编辑
                adapter.editItems();
                topStatusChange(true);
            }
        });
        tvdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //编辑完成
                adapter.editDone();
                topStatusChange(false);
            }
        });
        tvchoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseAll) {
                    chooseAll = false;
                    Map<Integer, Boolean> map = adapter.getchoseMap();
                    for (int i = 0; i < map.size(); i++) {
                        map.put(i, true);
                        adapter.notifyDataSetChanged();
                    }
                    tvdeleteTask.setText("删除(" + map.size() + ")");
                    tvchoose.setText("全不选");
                } else {
                    chooseAll = true;
                    Map<Integer, Boolean> m = adapter.getchoseMap();
                    for (int i = 0; i < m.size(); i++) {
                        m.put(i, false);
                        adapter.notifyDataSetChanged();
                    }
                    tvdeleteTask.setText("删除(" + 0 + ")");
                    tvchoose.setText("全选");
                }
                //adapter.choose(chooseAll);
            }
        });
        tvdeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load.setVisibility(View.VISIBLE);
                new DeleteThread().start();

            }
        });

        adapter.setDeleteCountListener(new DownloadFileAdapter.DeleteCountListener() {
            @Override
            public void deleteCount(Map<Integer, Boolean> map) {
                int count = 0;
                if (map != null && map.size() > 0) {
                    for (Integer k : map.keySet()) {
                        if (map.get(k) == true) {
                            count++;
                        }

                    }
                }
                tvdeleteTask.setText("删除(" + count + ")");
            }

        });
    }

    //根据选中的集合；根据下标找到相应的TaskBean;然后更新数据库的状态。删除相应文件夹下的文件（关于数据库中的更新操作，
    // 此处demo只是做演示，实际更具开发环境自己决定数据库的存储策略）
    public void deleteTask() {

        Map<Integer, Boolean> chosemap = adapter.getchoseMap();
        for (Integer key : chosemap.keySet()) {
            if (chosemap.get(key) == true) {
                adapter.getVideoMap().put(key, getPositionTaskInfo(key));
                adapter.getchoseMap().remove(key);

            }

        }
        //遍历videoMap，从数据库中更新相关信息
        videoMap.putAll(adapter.getVideoMap());
        for (Integer vk : videoMap.keySet()) {
            TaskBean taskBean = videoMap.get(vk);
            if (taskBean == null) {
                return;
            }
            String url = taskBean.getUrl();
            TasksManager.getImpl().updateTaskStatus(url, 0);
            String roomId = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
            TasksManager.getImpl().getAllDownloadTask().remove(vk);
            //删除文件夹下的课程资源
            CommonUtils.deleteDir(new File(CommonUtils.filePath + File.separator + roomId));
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                videoMap.clear();
                load.setVisibility(View.GONE);
            }
        });


    }

    private class DeleteThread extends Thread {
        @Override
        public void run() {
            super.run();
            deleteTask();
        }
    }

    /**
     * 根据位置获取相应的TaskBean的相关信息
     *
     * @param position
     * @return
     */
    public TaskBean getPositionTaskInfo(int position) {
        return adapter.getPostionInfo(position);
    }

    public void topStatusChange(boolean isEdit) {
        if (isEdit) {
            lltop.setVisibility(View.GONE);
            lltopeditcontainer.setVisibility(View.VISIBLE);
            tvdeleteTask.setVisibility(View.VISIBLE);

        } else {
            lltop.setVisibility(View.VISIBLE);
            lltopeditcontainer.setVisibility(View.GONE);
            tvdeleteTask.setVisibility(View.GONE);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        /* Do something */
        if (adapter != null) {
            adapter.getItemCount();
            adapter.notifyDataSetChanged();
        }
    }

    ;

    public static DownloadFragment getInstance() {
        return new DownloadFragment();
    }
}
