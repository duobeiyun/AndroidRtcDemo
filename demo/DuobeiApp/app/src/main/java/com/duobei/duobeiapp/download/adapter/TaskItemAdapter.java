package com.duobei.duobeiapp.download.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.duobei.duobeiapp.DBYApplication;
import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.control.TasksManager;
import com.duobei.duobeiapp.download.holder.TaskItemViewHolder;
import com.duobei.duobeiapp.utils.MessageEvent;
import com.duobeiyun.third.download.bean.TaskBean;
import com.duobeiyun.util.DuobeiYunClient;
import com.duobeiyun.util.FileUtil;
import com.duobeiyun.util.Unzip;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/24.
 */
public class TaskItemAdapter extends RecyclerView.Adapter<TaskItemViewHolder> {


    private Context mContext;
    private boolean isEdit = false;//是否编辑状态
    private boolean isStopAll = false;
    boolean isChooseAll = false;
    private int downingCount = 0;//正在缓存的个数
    DownlingCountListener downcountlistener;
    DeleteCountListener deletecountlistener;
    private Map<Integer, Boolean> choseMap = new HashMap<>();//存放勾选的checkbox
    private Map<Integer, TaskBean> videoMap = new HashMap<>();//存放勾选项的视频地址；
    private Map<Integer, Boolean> downMap = new HashMap<>();//正在下载的个数的集合；
    private int deletecount = 0;

    public TaskItemAdapter(Context context) {
        mContext = context;
    }

    /**
     * 初始化工作
     */
    public void init() {
        //将所有的checkbar的状态存放到chosemap中
        for (int i = 0; i < TasksManager.getImpl().getTaskCounts(); i++) {
            choseMap.put(i, false);
        }

        for (int i = 0; i < TasksManager.getImpl().getTaskCounts(); i++) {
            downMap.put(i, false);
        }
    }

    /**
     * 下载监听
     */
    private FileDownloadListener taskDownloadListener = new FileDownloadSampleListener() {

        private TaskItemViewHolder checkCurrentHolder(final BaseDownloadTask task) {
            final TaskItemViewHolder tag = (TaskItemViewHolder) task.getTag();
            if (tag.id != task.getId()) {
                return null;
            }
            return tag;
        }

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.pending(task, soFarBytes, totalBytes);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }
            tag.updateDownloading(mContext, task.getSpeed(), FileDownloadStatus.pending, soFarBytes
                    , totalBytes);
            tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_pending);
        }

        @Override
        protected void started(BaseDownloadTask task) {
            super.started(task);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }
            tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_started);
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            super.connected(task, etag, isContinue, soFarBytes, totalBytes);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            tag.updateDownloading(mContext, task.getSpeed(), FileDownloadStatus.connected, soFarBytes
                    , totalBytes);
            tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_connected);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.progress(task, soFarBytes, totalBytes);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }
            tag.updateDownloading(mContext, task.getSpeed(), FileDownloadStatus.progress, soFarBytes
                    , totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            System.out.println(e.getMessage());
            super.error(task, e);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }
            tag.handleError(e);
            tag.updateNotDownloaded(FileDownloadStatus.error, task.getLargeFileSoFarBytes()
                    , task.getLargeFileTotalBytes());
            TasksManager.getImpl().removeTaskForViewHolder(task.getId());
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.paused(task, soFarBytes, totalBytes);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }
            tag.updateNotDownloaded(FileDownloadStatus.paused, soFarBytes, totalBytes);
            tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_paused);
            TasksManager.getImpl().removeTaskForViewHolder(task.getId());
        }

        @Override
        protected void completed(final BaseDownloadTask task) {
            super.completed(task);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }
            tag.updateZip();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        if(!task.getUrl().contains(DuobeiYunClient.VIDEO_FLAG)) {
                            Unzip.unzip(new File(FileDownloadUtils.getDefaultSaveFilePath(task.getUrl())), new File(DuobeiYunClient.savePath+File.separator+DuobeiYunClient.NORMAL_DIR));
                        }else{
                            Unzip.unzip(new File(FileDownloadUtils.getDefaultSaveFilePath(task.getUrl())), new File(DuobeiYunClient.savePath+File.separator+DuobeiYunClient.VIDEO_DIR));
                        }                        FileUtil.deleteFile(FileDownloadUtils.getDefaultSaveFilePath(task.getUrl()));
                        String url = TasksManager.getImpl().getById(tag.id).getUrl();
                        String roomId = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
                        //// TODO: 2017/5/10 这完全可以不用这样去校验是否解压成功，可以设置相应的回调；
                        /// TODO: 2017/5/10 在回调中设置是否解压成功，之前person的逻辑，请自行修改。本类只做了相应的改动，没有做大的变动
                        DuobeiYunClient.createTxtFlag(roomId);
                        TasksManager.getImpl().updateTaskStatus(url, 1);
                        EventBus.getDefault().post(new MessageEvent());
                    } catch (IOException e) {
                        tag.handleError(e);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    tag.updateDownloaded();
                    TasksManager.getImpl().removeTaskForViewHolder(task.getId());
                }
            }.execute();
        }
    };

    @Override
    public TaskItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskItemViewHolder holder = new TaskItemViewHolder(
                LayoutInflater.from(
                        parent.getContext())
                        .inflate(R.layout.item_tasks_manager, parent, false));


        // holder.playBtn.setOnClickListener(playOnclickListener);

        return holder;
    }

    private View.OnClickListener playOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() == null) {
                return;
            }
            TaskItemViewHolder holder = (TaskItemViewHolder) v.getTag();
            String url = TasksManager.getImpl().getById(holder.id).getUrl();
            String roomId = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
            if (DuobeiYunClient.isResourceExist(roomId) && DuobeiYunClient.isUnzipSuccess(roomId)) {
                /*Intent intent = new Intent(DBYApplication.context, Playback1vNActivity.class);
//                Intent intent = new Intent(AppContext.context, DefOfflinePlaybackActivity.class);
//                Intent intent = new Intent(AppContext.context, CustomizedOffinePlayBackActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putString("roomId", roomId);
                bundle.putString("desKey", "MuAbYxEy");
                bundle.putString("title", "大学英语四六级");
                intent.putExtras(bundle);
                DBYApplication.context.startActivity(intent);*/
            } else {
                Toast.makeText(mContext, "资源文件正在准备中", Toast.LENGTH_SHORT).show();
            }
        }
    };

    boolean isdowning = false;

    private View.OnClickListener taskActionOnClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            if (v.getTag() == null) {
                return;
            }
            final TaskItemViewHolder holder = (TaskItemViewHolder) v.getTag();

            if (isdowning) {
                FileDownloader.getImpl().pause(holder.id);
                holder.startBtn.setBackground(mContext.getDrawable(R.drawable.playbtn));
                isdowning = false;

            } else {
                isdowning = true;
                holder.startBtn.setBackground(mContext.getDrawable(R.drawable.pause));
                final TaskBean model = TasksManager.getImpl().get(holder.position);
                final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
                        .setPath(model.getPath())
                        .setCallbackProgressTimes(100)
                        .setListener(taskDownloadListener);
                TasksManager.getImpl()
                        .addTaskForViewHolder(task);
                TasksManager.getImpl()
                        .updateViewHolder(holder.id, holder);
                task.start();
            }
            downcountlistener.setDownlingCount(downMap);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(TaskItemViewHolder holder, final int position) {
        final TaskBean model = TasksManager.getImpl().get(position);
        if (isEdit == true) {
            holder.editStatus();
        } else {
            holder.nomalStatus();
        }

        holder.update(model.getId(), position);
        holder.startBtn.setTag(holder);
        holder.playBtn.setTag(holder);
        holder.taskNameTv.setText(model.getName());

        TasksManager.getImpl()
                .updateViewHolder(holder.id, holder);

        holder.startBtn.setEnabled(true);


        if (TasksManager.getImpl().isReady()) {
            final int status = TasksManager.getImpl().getStatus(model.getId(), model.getPath());
            if (status == FileDownloadStatus.pending || status == FileDownloadStatus.started ||
                    status == FileDownloadStatus.connected) {
                // k开始任务但是文件未创建
                holder.updateDownloading(mContext, status, 0, TasksManager.getImpl().getSoFar(model.getId())
                        , TasksManager.getImpl().getTotal(model.getId()));
            } else if (!new File(model.getPath()).exists() &&
                    !new File(FileDownloadUtils.getTempPath(model.getPath())).exists()) {
                // 文件不存在
                holder.updateNotDownloaded(status, 0, 0);
            } else if (TasksManager.getImpl().isDownloaded(status)) {
                // 文件已经存在
                holder.updateDownloaded();
            } else if (status == FileDownloadStatus.progress) {
                // 下载中
               /* holder.updateDownloading(mContext, status, 0, TasksManager.getImpl().getSoFar(model.getId())
                        , TasksManager.getImpl().getTotal(model.getId()));*/
            } else {
                //未开始
                holder.updateNotDownloaded(status, TasksManager.getImpl().getSoFar(model.getId())
                        , TasksManager.getImpl().getTotal(model.getId()));
            }
        } else {
            holder.taskStatusTv.setText(R.string.tasks_manager_demo_status_loading);
        }
        if (isStopAll) {
            holder.startBtn.setBackground(mContext.getDrawable(R.drawable.playbtn));
        }
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //用map集合保存
                choseMap.put(position, isChecked);

                deletecountlistener.deleteCount(choseMap);
            }
        });
        // 设置CheckBox的状态
        if (choseMap.get(position) == null) {
            choseMap.put(position, false);
        }

        if (downMap.get(position) == null) {
            downMap.put(position, false);
        }

        holder.checkbox.setChecked(choseMap.get(position));

        holder.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() == null) {
                    return;
                }
                final TaskItemViewHolder holder = (TaskItemViewHolder) v.getTag();

                if (isdowning) {
                    FileDownloader.getImpl().pause(holder.id);
                    holder.startBtn.setBackground(mContext.getDrawable(R.drawable.playbtn));
                    isdowning = false;
                    downMap.put(position, false);

                } else {
                    isdowning = true;
                    holder.startBtn.setBackground(mContext.getDrawable(R.drawable.pause));
                    final TaskBean model = TasksManager.getImpl().get(holder.position);
                    final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
                            .setPath(model.getPath())
                            .setCallbackProgressTimes(100)
                            .setListener(taskDownloadListener);
                    TasksManager.getImpl()
                            .addTaskForViewHolder(task);
                    TasksManager.getImpl()
                            .updateViewHolder(holder.id, holder);
                    task.start();
                    downMap.put(position, true);
                }
                downcountlistener.setDownlingCount(downMap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return TasksManager.getImpl().getTaskCounts();
    }

    /**
     * 返回选中的集合
     *
     * @return
     */
    public Map<Integer, Boolean> getchoseMap() {
        return choseMap;
    }

    /**
     * 返回视频地址的集合
     *
     * @return
     */
    public Map<Integer, TaskBean> getVideoMap() {
        return videoMap;
    }

    /**
     * 编辑正在下载的item
     */
    public void editItems() {
        isEdit = true;
        notifyDataSetChanged();
    }

    /**
     * 暂停所有的下载
     */
    public void stopall() {
        FileDownloader.getImpl().pauseAll();
        isStopAll = true;
        notifyDataSetChanged();
        downingCount = 0;
        downMap.clear();
        downcountlistener.setDownlingCount(downMap);
    }

    public void editDone() {
        isEdit = false;
        notifyDataSetChanged();
    }

    public void choose(boolean chooseAll) {
        this.isChooseAll = chooseAll;
        notifyDataSetChanged();
    }

    public TaskBean getPostionInfo(int postion) {

        return TasksManager.getImpl().get(postion);
    }

//################################缓存个数回---start#################

    /**
     * 正在缓存的个数回调
     */
    public interface DownlingCountListener {
        void setDownlingCount(Map<Integer, Boolean> map);
    }

    /**
     * 设置下载的listener
     */
    public void setDownlingCountListener(DownlingCountListener listener) {
        this.downcountlistener = listener;
    }
    //################################缓存个数回---end#################

    //################################删除个数---start#################
    public interface DeleteCountListener {
        void deleteCount(Map<Integer, Boolean> map);
    }

    public void setDeleteCountListener(DeleteCountListener listener) {
        this.deletecountlistener = listener;
    }
    //################################删除个数---end#################

}

