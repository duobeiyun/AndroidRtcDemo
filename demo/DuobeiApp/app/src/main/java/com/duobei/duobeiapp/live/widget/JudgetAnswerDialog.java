package com.duobei.duobeiapp.live.widget;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.common.AnswerType;
import com.duobei.duobeiapp.live.CustomizedLiveActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *** Created by  com.duobei.duobeiapp.live.widget by yangge on 2017/10/26 
 *** mail:ge123.yang@gmail.com
 * 判断题的dialog
 **/
public class JudgetAnswerDialog extends Dialog {

    @BindView(R.id.ib_answer_title)
    TextView mIbAnswerTitle;
    @BindView(R.id.ib_answer_close)
    ImageButton mIbAnswerClose;
    @BindView(R.id.ib_answer_correct)
    ImageButton mIbAnswerCorrect;
    @BindView(R.id.ib_answer_error)
    ImageButton mIbAnswerError;
    @BindView(R.id.rl_answer_list)
    RelativeLayout mRlAnswerList;
    @BindView(R.id.tv_disp)
    TextView mTvDisp;
    @BindView(R.id.rl_cotent)
    RelativeLayout mRlCotent;
    @BindView(R.id.rl_answer_submit)
    RelativeLayout mRlAnswerSubmit;
    CustomizedLiveActivity mActivity;

    int result = -1;

    public JudgetAnswerDialog(@NonNull CustomizedLiveActivity context) {
        super(context, R.style.inputdialog);
        setContentView(R.layout.answer_dialog_judget);
        this.mActivity = context;
        ButterKnife.bind(this);
        initview();
    }


    private void initview() {
        mIbAnswerCorrect.setBackgroundResource(R.drawable.shap_answer_judget_normal);
        mIbAnswerError.setBackgroundResource(R.drawable.shap_answer_judget_normal);
        mRlAnswerSubmit.setBackgroundResource(R.drawable.shap_answer_judget_button_normal);
        mRlAnswerSubmit.setClickable(false);
    }

    /**
     * 初始化相应的点击事件
     */
    @OnClick({R.id.ib_answer_close, R.id.ib_answer_correct, R.id.ib_answer_error, R.id.rl_answer_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_answer_close:
                JudgetAnswerDialog.this.dismiss();
                break;
            case R.id.ib_answer_correct:
                //正确答案
                result = chooseOptions(true, AnswerType.RESULTTRUE);
                break;
            case R.id.ib_answer_error:
                //错误答案
                result = chooseOptions(false, AnswerType.RESULTFALSE);
                break;
            case R.id.rl_answer_submit:
                //提交所选的答案
                if (result == -1) {
                    Toast.makeText(mActivity, "你提交的答案有问题", Toast.LENGTH_SHORT).show();
                }
                mActivity.vote(result);
                //答题成功以后关闭dialog;防止重复答题
                if (JudgetAnswerDialog.this.isShowing()) {
                    dismiss();

                }
                break;
        }

    }


    private int chooseOptions(boolean iscurrect, int result) {
        if (iscurrect) {
            mIbAnswerCorrect.setBackgroundResource(R.drawable.shap_answer_judget_choose);
            mIbAnswerError.setBackgroundResource(R.drawable.shap_answer_judget_normal);
        } else {
            mIbAnswerError.setBackgroundResource(R.drawable.shap_answer_judget_choose);
            mIbAnswerCorrect.setBackgroundResource(R.drawable.shap_answer_judget_normal);
        }
        mRlAnswerSubmit.setBackgroundResource(R.drawable.shap_answer_judget_button_submit);
        mRlAnswerSubmit.setClickable(true);
        return result;
    }


    @Override
    public void show() {
        super.show();
        initview();
    }
}

