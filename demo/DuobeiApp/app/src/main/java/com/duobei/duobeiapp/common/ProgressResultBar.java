package com.duobei.duobeiapp.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duobei.duobeiapp.R;

/***
 *** Created by  com.duobei.duobeiapp.common by yangge on 2017/10/26 
 *** mail:ge123.yang@gmail.com
 * 进度条view
 **/
public class ProgressResultBar extends RelativeLayout {

    private ProgressBar mPogressbar;
    private TextView mAnswerNumber;
    private TextView mAnswerPercent;

    public ProgressResultBar(Context context) {
        this(context, null);
    }

    public ProgressResultBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.progressbar_result, this, true);
        mPogressbar = (ProgressBar) findViewById(R.id.pb_progress_correct);
        mAnswerNumber = (TextView) findViewById(R.id.tv_answer_number);
        mAnswerPercent = (TextView) findViewById(R.id.tv_percent_number);
        mPogressbar.setProgress(0);
        mAnswerNumber.setText("0");
        mAnswerPercent.setText("0%");
    }

    public void updateProgress(int progress, String number, String percent) {
        mPogressbar.setProgress(progress);
        mAnswerNumber.setText(number);
        mAnswerPercent.setText(percent);
    }

    public void setProgressNumber(int progress) {
        if (mPogressbar != null) {
            mPogressbar.setProgress(progress);
        }

    }
    
    public void setanswerNumberText(String text) {
        if (mAnswerNumber!=null) {
            mAnswerNumber.setText(text);
        }
    }
    public void setanswerPercentText(String text) {
        if (mAnswerPercent!=null) {
            mAnswerPercent.setText(text);
        }
    }

}
