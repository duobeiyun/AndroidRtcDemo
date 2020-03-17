package com.duobei.duobeiapp.control;


import android.text.TextUtils;
import android.util.SparseArray;

import com.duobei.duobeiapp.download.DownloadingFragment;
import com.duobei.duobeiapp.download.holder.TaskItemViewHolder;
import com.duobeiyun.third.download.bean.TaskBean;
import com.duobeiyun.util.DuobeiYunClient;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/24.
 * 下载的管理类
 * 注意：
 * 下载部分知识demo采用的思路；请开发者根据自己的业务需要选择与业务相符合的下载文件存储方案
 */
public class TasksManager {

    public static WeakReference<DownloadingFragment> reference;
    private TasksManagerDBController dbController;
    //所有的视频集合
    private List<TaskBean> modelList = new ArrayList<>();
    //已经缓存的视频集合
    private List<TaskBean> downloadTaskList = new ArrayList<>();

    private final static class HolderClass {
        private final static TasksManager INSTANCE = new TasksManager();
    }

    public static TasksManager getImpl() {
        return HolderClass.INSTANCE;
    }

    private TasksManager() {
        dbController = new TasksManagerDBController();
        getAllTasks();
        getAllDownloadTask();
        init();
    }

    private void init() {
        if (modelList.size() <= 0) {
            for (int i = 0; i < modelList.size(); i++) {
                final String url = modelList.get(i).getUrl();
                final String name = modelList.get(i).getName();
                addTask(url, name);
            }
        }
    }

    /**
     * 获取所有的视频
     *
     * @return
     */
    public List<TaskBean> getAllTasks() {
        modelList.clear();
        modelList.addAll(dbController.getAllTasks());
        return modelList;
    }

    /**
     * 更新视频的状态
     *
     * @param url
     */
    public void updateTaskStatus(String url, int isdown) {
        dbController.updateCourseState(url, isdown);
    }

    /**
     * 获取所有的已经下载的视频资源
     */
    public List<TaskBean> getAllDownloadTask() {
        downloadTaskList.clear();
        downloadTaskList.addAll(dbController.getDoloadTask());
        return downloadTaskList;
    }

    /**
     * 获取缓存到本地的集合大小
     *
     * @return
     */
    public int getLocalCount() {
        return downloadTaskList.size();
    }

    /**
     * 获取单个已经下载的资源
     */
    public TaskBean getOne(int postion) {
        return downloadTaskList.get(postion);
    }

    private SparseArray<BaseDownloadTask> taskSparseArray = new SparseArray<>();

    public void addTaskForViewHolder(final BaseDownloadTask task) {
        taskSparseArray.put(task.getId(), task);
    }

    public void removeTaskForViewHolder(final int id) {
        taskSparseArray.remove(id);
    }

    public void updateViewHolder(final int id, final TaskItemViewHolder holder) {
        final BaseDownloadTask task = taskSparseArray.get(id);
        if (task == null) {
            return;
        }
        task.setTag(holder);
    }

    public void releaseTask() {
        taskSparseArray.clear();
    }

    private FileDownloadConnectListener listener;

    private void registerServiceConnectionListener(final WeakReference<DownloadingFragment>
                                                           activityWeakReference) {
        reference = activityWeakReference;
        if (listener != null) {
            FileDownloader.getImpl().removeServiceConnectListener(listener);
        }
        listener = new FileDownloadConnectListener() {
            @Override
            public void connected() {
                if (activityWeakReference == null || activityWeakReference.get() == null) {
                    return;
                }
                activityWeakReference.get().postNotifyDataChanged();
            }

            @Override
            public void disconnected() {
                if (activityWeakReference == null || activityWeakReference.get() == null) {
                    return;
                }
                activityWeakReference.get().postNotifyDataChanged();
            }
        };
        FileDownloader.getImpl().addServiceConnectListener(listener);
    }

    private void unregisterServiceConnectionListener() {
        FileDownloader.getImpl().removeServiceConnectListener(listener);
        listener = null;
    }

    public void onCreate(final WeakReference<DownloadingFragment> activityWeakReference) {
        if (!FileDownloader.getImpl().isServiceConnected()) {
            FileDownloader.getImpl().bindService();
            registerServiceConnectionListener(activityWeakReference);
        }
    }

    public void onDestroy() {
        unregisterServiceConnectionListener();
        releaseTask();
    }

    public boolean isReady() {
        return FileDownloader.getImpl().isServiceConnected();
    }

    public TaskBean get(final int position) {
        return modelList.get(position);
    }

    public TaskBean getLocalBean(int position) {
        return downloadTaskList.get(position);
    }

    public TaskBean getById(final int id) {
        for (TaskBean model : modelList) {
            if (model.getId() == id) {
                return model;
            }
        }
        return null;
    }

    /**
     * @param status Download Status
     * @return has already downloaded
     * @see FileDownloadStatus
     */
    public boolean isDownloaded(final int status) {
        return status == FileDownloadStatus.completed;
    }

    public int getStatus(final int id, String path) {
        return FileDownloader.getImpl().getStatus(id, path);
    }

    public long getTotal(final int id) {
        return FileDownloader.getImpl().getTotal(id);
    }

    public long getSoFar(final int id) {
        return FileDownloader.getImpl().getSoFar(id);
    }


    public int getTaskCounts() {
        return modelList.size();
    }

    public TaskBean addTask(final String roomId, final String name) {
        return addTask(getUrl(roomId), createPath(getUrl(roomId)), name);
    }

    public String getUrl(String roomId) {
        return DuobeiYunClient.getDownloadUrl(roomId);
    }

    public TaskBean addTask(final String url, final String path, final String name) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path) || TextUtils.isEmpty(name)) {
            return null;
        }
        final int id = FileDownloadUtils.generateId(url, path);
        TaskBean model = getById(id);
        if (model != null) {
            return model;
        }
        final TaskBean newModel = dbController.addTask(url, path, name);
        if (newModel != null) {
            modelList.add(newModel);
        }
        return newModel;
    }

    public boolean deleteTask(TaskBean bean) {
        if (bean == null) {
            return false;
        }
        try {
            dbController.deleteTask(bean.getUrl());
            modelList.remove(bean);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public String createPath(final String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return FileDownloadUtils.getDefaultSaveFilePath(url);
    }
}
