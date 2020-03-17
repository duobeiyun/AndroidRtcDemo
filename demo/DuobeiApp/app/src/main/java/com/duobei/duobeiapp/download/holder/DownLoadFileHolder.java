package com.duobei.duobeiapp.download.holder;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duobei.duobeiapp.R;


/***
 ***Create 已下载文件的holder by yangge at 2017/5/8 
 **/
public class DownLoadFileHolder extends ViewHolder {

    public int position;// viewHolder position
    public int id; //download id
    public ImageView iamge;
    public RelativeLayout rlRoot;
    public CheckBox check;
    public TextView tvClassName;
    public TextView totalSize;

    public DownLoadFileHolder(View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        rlRoot = (RelativeLayout) findViewById(R.id.ll_root);
        iamge = (ImageView) findViewById(R.id.iv_image);
        check = (CheckBox) findViewById(R.id.ck_check);
        tvClassName = (TextView) findViewById(R.id.tv_classname);
        totalSize = (TextView) findViewById(R.id.tv_totalsize);


    }
    public void update(final int id, final int position) {
        this.id = id;
        this.position = position;
    }
    private View findViewById(final int id) {
        return itemView.findViewById(id);

    }
}
