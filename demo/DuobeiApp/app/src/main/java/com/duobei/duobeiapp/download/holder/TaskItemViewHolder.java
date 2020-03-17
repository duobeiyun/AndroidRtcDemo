package com.duobei.duobeiapp.download.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duobei.duobeiapp.DBYApplication;
import com.duobei.duobeiapp.R;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

/**
 * Created by Administrator on 2016/10/24.
 * 正在下载的viewholder
 */
public class TaskItemViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = TaskItemViewHolder.class.getSimpleName();

    public int position;// viewHolder position
    public int id; //download id

    public TextView taskNameTv;
    public TextView taskStatusTv;
    public ProgressBar taskPb;
    public ImageButton startBtn;
    public RelativeLayout playBtn;
    public boolean canPlay = false;
    private TextView totlaSize;
    public CheckBox checkbox;

    public TaskItemViewHolder(View itemView) {
        super(itemView);
        assignViews();
    }

    private void assignViews() {
        taskNameTv = (TextView) findViewById(R.id.task_name_tv);
        taskStatusTv = (TextView) findViewById(R.id.task_status_tv);
        taskPb = (ProgressBar) findViewById(R.id.task_pb);
        startBtn = (ImageButton) findViewById(R.id.ib_playbtn);
        playBtn = (RelativeLayout) findViewById(R.id.rl_root);
        totlaSize = (TextView) findViewById(R.id.task_total_size);
        checkbox = (CheckBox) findViewById(R.id.ck_check);
    }

    private View findViewById(final int id) {
        return itemView.findViewById(id);
    }

    public void update(final int id, final int position) {
        this.id = id;
        this.position = position;
    }

    public void updateZip() {
        taskStatusTv.setText("正在解压");
    }

    public void updateDownloaded() {
        taskPb.setMax(1);
        taskPb.setProgress(1);
        taskStatusTv.setText(R.string.tasks_manager_demo_status_completed);
        canPlay = true;
    }

    public void updateNotDownloaded(final int status, final long sofar, final long total) {
        if (sofar > 0 && total > 0) {
            final float percent = sofar
                    / (float) total;
            taskPb.setMax(100);
            taskPb.setProgress((int) (percent * 100));
        } else {
            taskPb.setMax(1);
            taskPb.setProgress(0);
        }

        switch (status) {
            case FileDownloadStatus.error:
                taskStatusTv.setText(R.string.tasks_manager_demo_status_error);
                break;
            case FileDownloadStatus.paused:
                taskStatusTv.setText(R.string.tasks_manager_demo_status_paused);
                break;
            default:
                taskStatusTv.setText(R.string.tasks_manager_demo_status_not_downloaded);
                break;
        }
    }

    public void updateDownloading(Context context, long speed, final int status, final long sofar, final long total) {
        final float percent = sofar
                / (float) total;
        taskPb.setMax(100);
        taskPb.setProgress((int) (percent * 100));
        totlaSize.setText(Formatter.formatFileSize(context, sofar) + "/" + Formatter.formatFileSize(context, total));
        switch (status) {
            case FileDownloadStatus.pending:
                taskStatusTv.setText(R.string.tasks_manager_demo_status_pending);
                break;
            case FileDownloadStatus.started:
                taskStatusTv.setText(R.string.tasks_manager_demo_status_started);
                break;
            case FileDownloadStatus.connected:
                taskStatusTv.setText(R.string.tasks_manager_demo_status_connected);
                break;
            case FileDownloadStatus.progress:
                taskStatusTv.setText(speed + "k/s");
                break;
            default:
                taskStatusTv.setText(DBYApplication.context.getString(
                        R.string.tasks_manager_demo_status_downloading, status));
                break;
        }
    }

    public void handleError(Throwable throwable) {
        //处理异常信息
    }

    public void handleError(Exception e) {
        //处理异常信息
    }

    /**
     * 编辑状态下
     */
    public void editStatus() {
        checkbox.setVisibility(View.VISIBLE);
    }

    /**
     * 正常状态下
     */
    public void nomalStatus() {
        checkbox.setVisibility(View.GONE);
    }


}
