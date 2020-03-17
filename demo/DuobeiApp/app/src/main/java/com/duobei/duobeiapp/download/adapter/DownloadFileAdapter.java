package com.duobei.duobeiapp.download.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.control.TasksManager;
import com.duobei.duobeiapp.download.holder.DownLoadFileHolder;
import com.duobei.duobeiapp.offlineplayback.CustomizedOffinePlayBackActivity;
import com.duobei.duobeiapp.utils.CommonUtils;
import com.duobeiyun.third.download.bean.TaskBean;
import com.duobeiyun.util.DuobeiYunClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/***
 ***Create 已经下载的文件列表adapter by yangge at 2017/5/8 
 *
 * 本地缓存列表的实现思路：
 *          下载完成以后；将获取的相关的信息保存到本地数据库；
 *          对本地缓存的操作依赖于对本地数据库的操作
 *
 **/
public class DownloadFileAdapter extends RecyclerView.Adapter<DownLoadFileHolder> {

    private Context mContext;
    private boolean isEdit = false;//是否编辑状态
    private Map<Integer, Boolean> choseMap = new HashMap<>();//存放勾选的checkbox
    private Map<Integer, TaskBean> videoMap = new HashMap<>();//存放勾选项的视频地址；
    DeleteCountListener deletecountlistener;

    public DownloadFileAdapter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 初始化工作
     */
    public void init() {
        //将所有的checkbar的状态存放到chosemap中
        for (int i = 0; i < TasksManager.getImpl().getLocalCount(); i++) {
            choseMap.put(i, false);
        }
    }

    @Override
    public DownLoadFileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DownLoadFileHolder holder = new DownLoadFileHolder(LayoutInflater.from(
                parent.getContext())
                .inflate(R.layout.adapter_downloadfile, parent, false));

        return holder;

    }

    @Override
    public void onBindViewHolder(DownLoadFileHolder holder, final int position) {
        TaskBean taskBean = TasksManager.getImpl().getOne(position);
        holder.update(taskBean.getId(), position);
        holder.rlRoot.setTag(holder);
        holder.check.setTag(holder);
        holder.tvClassName.setText(taskBean.getName());
        String url = TasksManager.getImpl().getById(holder.id).getUrl();
        String roomId = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        holder.totalSize.setText(Formatter.formatFileSize(mContext, (long) CommonUtils.getFileSize2(new File(DuobeiYunClient.savePath + roomId))));
        holder.rlRoot.setOnClickListener(openPlayerListener);
        if (isEdit) {
            holder.check.setVisibility(View.VISIBLE);
        } else {
            holder.check.setVisibility(View.GONE);
        }
        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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

        holder.check.setChecked(choseMap.get(position));
    }

    private View.OnClickListener openPlayerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() == null) {
                return;
            }
            DownLoadFileHolder holder = (DownLoadFileHolder) v.getTag();
            String url = TasksManager.getImpl().getById(holder.id).getUrl();
            String roomId = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
            if (DuobeiYunClient.isResourceExist(roomId) && DuobeiYunClient.isUnzipSuccess(roomId)) {
//                Intent intent = new Intent(AppContext.context, Playback1vNActivity.class);
//                Intent intent = new Intent(AppContext.context, DefOfflinePlaybackActivity.class);
               /* Intent intent = new Intent(DBYApplication.context, CustomizedOffinePlayBackActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putString("roomId", roomId);
                bundle.putString("desKey", "MuAbYxEy");
                bundle.putString("title", "大学英语四六级");
                intent.putExtras(bundle);
                DBYApplication.context.startActivity(intent);*/
                CustomizedOffinePlayBackActivity.startCustomizedOffinePlayBackActivity(mContext, roomId);
            } else {
                Toast.makeText(mContext, "资源文件正在准备中", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public int getItemCount() {
        return TasksManager.getImpl().getLocalCount();
    }

    /**
     * 编辑正在下载的item
     */
    public void editItems() {
        isEdit = true;
        notifyDataSetChanged();
    }

    public void editDone() {
        isEdit = false;
        notifyDataSetChanged();
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

    public TaskBean getPostionInfo(int postion) {

        return TasksManager.getImpl().getLocalBean(postion);
    }

    //################################删除个数---start#################
    public interface DeleteCountListener {
        void deleteCount(Map<Integer, Boolean> map);
    }

    public void setDeleteCountListener(DeleteCountListener listener) {
        this.deletecountlistener = listener;
    }
    //################################删除个数---end#################
}
