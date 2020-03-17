package com.duobei.duobeiapp.playback.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.bean.PlaybackInfo;
import com.duobei.duobeiapp.playback.CusomizedPlayBackActivity;
import com.duobei.duobeiapp.utils.Constant;
import com.duobeiyun.def.controller.DefPlaybackActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 ***Create DuobeiApp by yangge at 2017/5/15 
 **/
public class PlaybackAdapter extends RecyclerView.Adapter<PlaybackAdapter.PlaybackViewHolder> {
    Context context;
    List<PlaybackInfo> list = new ArrayList<>();


    public PlaybackAdapter(Context context, List<PlaybackInfo> list) {
        this.context = context;
        this.list.addAll(list);
    }

    @Override
    public PlaybackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PlaybackViewHolder holder = new PlaybackViewHolder(LayoutInflater.from(
                parent.getContext())
                .inflate(R.layout.adapter_playback, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(PlaybackViewHolder holder, int position) {
        final PlaybackInfo info = list.get(position);
//        holder.ivPalybackImage
        holder.tvPlaybackClassName.setText(info.getTitle());
        String time = info.getTime();
        if (info.iscandown()) {
            time = time + " | 可离线下载";
            holder.rldown.setVisibility(View.VISIBLE);
        } else {
            holder.rldown.setVisibility(View.GONE);
        }
        Picasso.with(context).load(info.getPlay_imageurl()).error(R.drawable.error).config(Bitmap.Config.RGB_565).into(holder.ivPalybackImage);
        holder.tvPlaybackTimeDown.setText(time);
        holder.playbackroot.setTag(holder);
        holder.playbackroot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (info == null) {
                    return;
                }
                //原生在线回放
                if (info.isNativePlayback()) {

                    CusomizedPlayBackActivity.startCusomizedPlayBackActivity(context);
                    return;
                }
                //网页版在线回放
                Intent intent = new Intent(context, DefPlaybackActivity.class);
                String appKey = Constant.APPKEY;
                String partner = Constant.PID;
                String nickname = "来自火星的你";
                String roomId = info.getRoomId();
                String uid = info.getUuid();
                intent.putExtra("appKey", appKey);
                intent.putExtra("nickname", nickname);
                intent.putExtra("partner", partner);
                intent.putExtra("roomId", roomId);
                intent.putExtra("uid", uid);
                intent.putExtra("title", info.getTitle());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class PlaybackViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_palyback_image)
        ImageView ivPalybackImage;
        @BindView(R.id.rl_left)
        RelativeLayout rlLeft;
        @BindView(R.id.tv_playback_class_name)
        TextView tvPlaybackClassName;
        @BindView(R.id.tv_playback_time_down)
        TextView tvPlaybackTimeDown;
        @BindView(R.id.rl_playback_root)
        RelativeLayout playbackroot;
        @BindView(R.id.rl_down)
        RelativeLayout rldown;

        public PlaybackViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
