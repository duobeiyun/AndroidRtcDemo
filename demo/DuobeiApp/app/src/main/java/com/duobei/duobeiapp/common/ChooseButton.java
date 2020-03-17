package com.duobei.duobeiapp.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.duobei.duobeiapp.R;

/***
 *** Created by  com.duobei.duobeiapp.common by yangge on 2017/10/27 
 *** mail:ge123.yang@gmail.com
 **/
public class ChooseButton extends RelativeLayout {

    private Button mOptionButton;
    private Button mMyourChooseButton;

    public ChooseButton(Context context) {
        this(context, null);
    }

    public ChooseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.answer_chose_button, this, true);
        mOptionButton = (Button) findViewById(R.id.btn_answer_options);
        mMyourChooseButton = (Button) findViewById(R.id.btn_your_answer);
    }

    public void setOptinsText(String text) {
        if (mOptionButton != null) {
            mOptionButton.setText(text);
        }

    }

    public Button getOptionButton() {
        if (mOptionButton != null) {
            return mOptionButton;
        }
        return null;
    }

    /**
     * 设置字体颜色
     */
    public void setOptintsColor(int resId) {
        if (mOptionButton != null) {
            mOptionButton.setTextColor(resId);
        }
    }

    /**
     * 设置按钮的背景
     */
    public void setOptinsBackground(int resId) {
        if (mOptionButton != null) {
            mOptionButton.setBackgroundResource(resId);
        }

    }

    public Button getYourChooseButton() {
        if (mMyourChooseButton != null) {
            return mMyourChooseButton;
        } else {
            return null;
        }

    }
}
