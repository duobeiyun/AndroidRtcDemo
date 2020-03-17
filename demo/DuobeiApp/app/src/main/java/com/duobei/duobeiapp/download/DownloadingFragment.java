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
import com.duobei.duobeiapp.download.adapter.TaskItemAdapter;
import com.duobei.duobeiapp.utils.CommonUtils;
import com.duobei.duobeiapp.utils.PreferencesUtils;
import com.duobeiyun.third.download.bean.TaskBean;
import com.duobeiyun.util.DuobeiYunClient;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/***
 ***Create duobeisdk by yangge at 2017/5/8 
 **/
public class DownloadingFragment extends Fragment {

    private TextView localVideo;
    private TextView videoEdit;
    private TextView sdSize;
    private RecyclerView recyclerView;
    private TaskItemAdapter adapter;

    private String[] roomIds = {"jza5e1cc3a5b5a415fafe1002254358d47",
            "jze68d51d73e1844bfbe1b686d4a7e6bba"+ DuobeiYunClient.VIDEO_FLAG,//视频课
            /*
            "jz7f83c4e637064b6aadb7eb13eb3061a0",
            "jz645c75a566d64099971a862ac5e38e18",
            "jz20d2eb843ea2417dada41c2243dd4eb4"*/};

    private String[] names = {"课程一", "课程二", "课程三", "课程四4eb4"};
    private ProgressBar load;
    public static final String FRIST = "first";
    private TextView tvstopAll;
    private RelativeLayout lltopeditcontainer;
    private TextView tvchoose;
    private TextView tvdone;
    private RelativeLayout lltop;
    private TextView tvdeleteTask;
    private Map<Integer, TaskBean> videoMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_downloading, null);
        initView(view);
        initclickListener();
        TasksManager.getImpl().onCreate(new WeakReference<>(this));
        /**
         *
         demo中只是做效果展示，此处指是第一次进入时才做存储操作，实际开发环境请自行处理
         */
        String first = PreferencesUtils.getString(this.getContext(), FRIST);
        if (!"1".equals(first)) {
            addTask();
        }
        PreferencesUtils.putString(this.getContext(), FRIST, "1");
        //TasksManager.getImpl().getAllTasks();
        // postNotifyDataChanged();
        return view;
    }

    private void initView(View viewroot) {
        localVideo = (TextView) viewroot.findViewById(R.id.tv_re_video);
        videoEdit = (TextView) viewroot.findViewById(R.id.tv_re_video_edit);
        sdSize = (TextView) viewroot.findViewById(R.id.tv_sd_size);
        recyclerView = (RecyclerView) viewroot.findViewById(R.id.rc_list);
        videoEdit.setEnabled(true);
        localVideo.setText("正在缓存(" + 0 + ")");
        sdSize.setText("占用空间" + Formatter.formatFileSize(getContext().getApplicationContext(),
                CommonUtils.getFileSize2(new File(CommonUtils.filePath)))
                + "M,可用空间" + CommonUtils.getCanUseSize(getContext().getApplicationContext()));

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(adapter = new TaskItemAdapter(this.getContext()));
        load = (ProgressBar) viewroot.findViewById(R.id.load);
        tvstopAll = (TextView) viewroot.findViewById(R.id.tv_stop_all);
        tvstopAll.setClickable(true);
        lltopeditcontainer = (RelativeLayout) viewroot.findViewById(R.id.ll_top_fored);
        lltop = (RelativeLayout) viewroot.findViewById(R.id.ll_top);
        tvchoose = (TextView) viewroot.findViewById(R.id.tv_re_video_fored);
        tvchoose.setClickable(true);
        tvdone = (TextView) viewroot.findViewById(R.id.tv_re_video_edit_fored);
        tvdone.setClickable(true);
        tvdeleteTask = (TextView) viewroot.findViewById(R.id.tv_delete_task);
        tvdeleteTask.setClickable(true);

    }


    public void addTask() {
        load.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < roomIds.length; i++) {
                    TasksManager.getImpl().addTask(roomIds[i], names[i]);

                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postNotifyDataChanged();
                        load.setVisibility(View.GONE);
                    }
                });
            }
        }).start();


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
        tvstopAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //暂停所有
                adapter.stopall();
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
                deleteTask();

            }
        });
        adapter.setDownlingCountListener(new TaskItemAdapter.DownlingCountListener() {
            @Override
            public void setDownlingCount(Map<Integer, Boolean> map) {
                int count = 0;
                for (Integer i : map.keySet()) {
                    if (map.get(i) == true) {
                        count++;
                    }
                }
                localVideo.setText("正在缓存(" + count + ")");
            }

        });
        adapter.setDeleteCountListener(new TaskItemAdapter.DeleteCountListener() {
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

    //便利checkbox集合；找到选中的选项；并根据找到的选项找到相应的TaskBean，存放到adapter.videoMap中。
    //再从中videoMap中便利删除数据库中所选择的item的信息，在更新item集合，更新adapter
    public void deleteTask() {
        Map<Integer, Boolean> chosemap = adapter.getchoseMap();
        for (Integer key : chosemap.keySet()) {
            if (chosemap.get(key) == true) {
                adapter.getVideoMap().put(key, getPositionTaskInfo(key));
            }

        }
        //遍历videoMap，从数据库中删除相关信息
        videoMap.putAll(adapter.getVideoMap());
        for (Integer vk : videoMap.keySet()) {
            TasksManager.getImpl().deleteTask(videoMap.get(vk));
            TasksManager.getImpl().getAllTasks().remove(vk);
        }
        adapter.notifyDataSetChanged();
        videoMap.clear();
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
            tvstopAll.setVisibility(View.GONE);
            tvdeleteTask.setVisibility(View.VISIBLE);

        } else {
            lltop.setVisibility(View.VISIBLE);
            lltopeditcontainer.setVisibility(View.GONE);
            tvstopAll.setVisibility(View.VISIBLE);
            tvdeleteTask.setVisibility(View.GONE);
        }

    }

    public void postNotifyDataChanged() {
        if (adapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static DownloadingFragment getInstance() {
        return new DownloadingFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TasksManager.getImpl().onDestroy();
        FileDownloader.getImpl().pauseAll();
    }
}
