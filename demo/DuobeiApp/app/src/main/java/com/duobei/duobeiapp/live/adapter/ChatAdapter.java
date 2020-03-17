package com.duobei.duobeiapp.live.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.utils.CommonUtils;
import com.duobeiyun.bean.ChatBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/19.
 */
public class ChatAdapter extends BaseAdapter {

    private List<ChatBean> list;//总的聊天集合
    private Context context;
    private boolean isShowAll = true;
    private List<ChatBean> teacherList;//老师的聊天集合


    public ChatAdapter(Context context, List<ChatBean> list, List<ChatBean> teacherList) {
        this.context = context;
        this.list = list;
        this.teacherList = teacherList;
    }

    public void setShowAll(boolean showAll) {
        isShowAll = showAll;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return isShowAll ? (list == null ? 0 : list.size()) : (teacherList.size());
    }

    @Override
    public Object getItem(int position) {
        if (!isShowAll) {
            return teacherList.get(position);
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_item, null);
            viewHolder = new ViewHolder();
            viewHolder.username = (TextView) convertView.findViewById(R.id.username);
            viewHolder.info = (TextView) convertView.findViewById(R.id.info);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.userImage = (ImageView) convertView.findViewById(R.id.chat_user_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (!isShowAll) {
            final ChatBean chat = teacherList.get(position);
            if (chat.getRole() == 1) { //老师
                viewHolder.username.setText("" + chat.getUsername());
                viewHolder.userImage.setBackgroundResource(R.drawable.chat_teacher);
                viewHolder.info.setText(chat.getMessage());
                viewHolder.time.setText(CommonUtils.long2DateString(chat.getTimestamp()));
            }
        } else {
            final ChatBean chat = list.get(position);
            viewHolder.info.setText(chat.getMessage());
            viewHolder.time.setText(CommonUtils.long2DateString(chat.getTimestamp()));

            if (chat.getRole() == 1) { //老师
                viewHolder.username.setText("" + chat.getUsername());
                viewHolder.userImage.setBackgroundResource(R.drawable.chat_teacher);
            } else if (chat.getRole() == 4) { //助教
                viewHolder.username.setText("" + chat.getUsername());
                viewHolder.userImage.setBackgroundResource(R.drawable.chat_assteacher);
            } else {  //学生或其他
                viewHolder.username.setText("" + chat.getUsername());
                if (chat.isMyself()) {
                    viewHolder.userImage.setBackgroundResource(R.drawable.chat_student);
                } else {
                    viewHolder.userImage.setBackgroundResource(R.drawable.chat_other_student);

                }

            }
        }
        return convertView;
    }

    static class ViewHolder {
        TextView username;
        TextView info;
        TextView time;
        ImageView userImage;
    }
}