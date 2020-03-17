package com.duobei.duobeiapp.live.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.bean.LiveInfo;
import com.duobei.duobeiapp.live.CustomizedLiveActivity;
import com.duobei.duobeiapp.live.One2OneActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 ***Create DuobeiApp by yangge at 2017/5/15 
 **/
public class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.LiveViewHolder> {
    Context context;
    List<LiveInfo> list = new ArrayList<>();


    public LiveAdapter(Context context, List<LiveInfo> list) {
        this.context = context;
        this.list.addAll(list);

    }

    @Override
    public LiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LiveViewHolder holder = new LiveViewHolder(LayoutInflater.from(
                parent.getContext())
                .inflate(R.layout.adapter_live, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final LiveViewHolder holder, int position) {
        final LiveInfo liveInfo = list.get(position);
        holder.liveroot.setTag(holder);
        Picasso.with(context).load(liveInfo.getLiveImage()).error(R.drawable.error).config(Bitmap.Config.RGB_565).into(holder.ivLiveImage);
        holder.tvClasName.setText(liveInfo.getTitle());
        if (liveInfo.isLiveStart()) {
            holder.tvStart.setVisibility(View.VISIBLE);
            holder.tvClassStartTime.setText("已与xx:xx开始直播");
            holder.liveroot.setClickable(true);

        } else {
            holder.tvStart.setVisibility(View.GONE);
            holder.tvClassStartTime.setText("将于xx:xx日xx:xx进行直播");
            holder.liveroot.setClickable(false);

        }
        holder.liveroot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!liveInfo.isLiveStart()) {
                    Toast.makeText(context, "课程还没开始", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (liveInfo.isIs1vN()) {
                    CustomizedLiveActivity.startCustomizedActivity(context, liveInfo);
                    return;
                }
                One2OneActivity.openOne2OneActivity(context, liveInfo);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class LiveViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_live_image)
        ImageView ivLiveImage;
        @BindView(R.id.rl_left)
        RelativeLayout rlLeft;
        @BindView(R.id.tv_clas_name)
        TextView tvClasName;
        @BindView(R.id.tv_class_start_time)
        TextView tvClassStartTime;
        @BindView(R.id.ll_live_root)
        RelativeLayout liveroot;
        @BindView(R.id.tv_is_start)
        TextView tvStart;

        public LiveViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
