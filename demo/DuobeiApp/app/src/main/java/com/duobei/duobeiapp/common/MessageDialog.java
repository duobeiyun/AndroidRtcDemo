package com.duobei.duobeiapp.common;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.live.CustomizedLiveActivity;

import java.util.Timer;
import java.util.TimerTask;

/***
 *** Created by  com.duobei.duobeiapp.common by yangge on 2017/10/23 
 *** mail:ge123.yang@gmail.com
 * 主要负责输入聊天信息的Dialog
 **/
public class MessageDialog extends Dialog {

    private final EditText mInputMessage;
    private final TextView mSendMessg;
    private final TextView mCancel;
    private InputMethodManager imm;
    private Context mContext;
    CustomizedLiveActivity activity;
    private final RelativeLayout mRootview;
    private int mLastDiff = 0;

    public MessageDialog(@NonNull Context context, final CustomizedLiveActivity activity) {
        super(context, R.style.inputdialog);
        this.mContext = context;
        this.activity = activity;
        setContentView(R.layout.message_input_layout);
        mInputMessage = (EditText) findViewById(R.id.inputMsg);
        mSendMessg = (TextView) findViewById(R.id.sendMsg);
        mCancel = (TextView) findViewById(R.id.cancel);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mRootview = (RelativeLayout) findViewById(R.id.rl_messageroot);
        mSendMessg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInputMessage.getText().length() > 0) {
                    sendText("" + mInputMessage.getText());
                    imm.showSoftInput(mInputMessage, InputMethodManager.SHOW_FORCED);
                    dismiss();
                } else {
                    Toast.makeText(mContext, "输入的内容不能为空!", Toast.LENGTH_LONG).show();
                }
            }
        });
        mInputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mSendMessg.setBackgroundResource(R.drawable.message_send_btn_default);
                } else {
                    mSendMessg.setBackgroundResource(R.drawable.message_send_btn_choose);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*mInputMessage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_UP) {   // 忽略其它事件
                    return false;
                }

                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        if (mInputMessage.getText().length() > 0) {
                            sendText("" + mInputMessage.getText());
                            imm.showSoftInput(mInputMessage, InputMethodManager.SHOW_FORCED);
                            imm.hideSoftInputFromWindow(mInputMessage.getWindowToken(), 0);
                            dismiss();
                        } else {
                            Toast.makeText(mContext, "输入的内容不能为空!", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });*/
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

       /* mRootview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               
                dismiss();
            }
        });*/
    }

    private void sendText(String s) {
        activity.sendMessage(s);
    }


    @Override
    public void show() {
        super.show();
        activity.hidenMessageList(true);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager = (InputMethodManager) mInputMessage.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mInputMessage, 0);

            }

        }, 200);
    }

    @Override
    public void dismiss() {
        imm.hideSoftInputFromWindow(mInputMessage.getWindowToken(), 0);
        activity.hidenMessageList(false);
        super.dismiss();

    }

}
