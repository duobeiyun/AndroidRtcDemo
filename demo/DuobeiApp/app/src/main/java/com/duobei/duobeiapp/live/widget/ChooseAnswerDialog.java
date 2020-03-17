package com.duobei.duobeiapp.live.widget;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.common.ChooseButton;
import com.duobei.duobeiapp.live.CustomizedLiveActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 *** Created by  com.duobei.duobeiapp.live.widget by yangge on 2017/10/26 
 *** mail:ge123.yang@gmail.com
 * 选择题的答题dialog
 **/
public class ChooseAnswerDialog extends Dialog {

    @BindView(R.id.ib_answer_title)
    TextView mIbAnswerTitle;
    @BindView(R.id.ib_answer_close)
    ImageButton mIbAnswerClose;
    @BindView(R.id.rl_answer_list)
    LinearLayout mRlAnswerList;
    @BindView(R.id.tv_disp)
    TextView mTvDisp;
    @BindView(R.id.rl_cotent)
    RelativeLayout mRlCotent;
    @BindView(R.id.rl_answer_submit)
    RelativeLayout mRlAnswerSubmit;
    CustomizedLiveActivity mActivity;
    List<ChooseButton> buttonlist = new ArrayList<>();
    private int result = -1;

    public ChooseAnswerDialog(@NonNull CustomizedLiveActivity context) {
        super(context, R.style.inputdialog);
        mActivity = context;
        setContentView(R.layout.answer_dialog_choose);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    private void initView() {
        mIbAnswerTitle.setText("选择题");
        Drawable drawable = mActivity.getResources().getDrawable(R.drawable.answer_choose);
/// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mIbAnswerTitle.setCompoundDrawables(drawable, null, null, null);
        mRlAnswerSubmit.setBackgroundResource(R.drawable.shap_answer_judget_button_normal);
        mRlAnswerSubmit.setClickable(false);
        buttonlist.clear();
        mRlAnswerList.removeAllViews();
    }

    public void initListener() {
        mIbAnswerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ChooseAnswerDialog.this.isShowing()) {
                    ChooseAnswerDialog.this.dismiss();
                }
            }
        });
        mRlAnswerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交答题
                if (mActivity != null) {
                    if (result == -1) {
                        Toast.makeText(mActivity, "你提交的答案有问题", Toast.LENGTH_SHORT).show();
                    }
                    mActivity.vote(result);
                    ChooseAnswerDialog.this.dismiss();
                }
            }
        });

    }


    @Override
    public void show() {
        super.show();
        initView();
    }

    /**
     * @param type 题型
     *             10  单选 AB两个选项
     *             11  单选 ABC三个选项
     *             12  单选 ABCD四个选项
     *             13  单选 ABCDE五个选项
     *             30  判断 √或×
     */
    public void drawAnswerDialog(int type) {
        buttonlist.clear();
        if (type == 10) {
            drawDialog(2, new String[]{"A", "B"});
        } else if (type == 11) {
            drawDialog(3, new String[]{"A", "B", "C"});

        } else if (type == 12) {
            drawDialog(4, new String[]{"A", "B", "C", "D"});

        } else if (type == 13) {
            drawDialog(5, new String[]{"A", "B", "C", "D", "E"});

        }
    }

    private void drawDialog(int number, String[] text) {
        for (int i = 0; i < number; i++) {
            ChooseButton chooseButton = new ChooseButton(mActivity.getApplicationContext());
            chooseButton.setOptinsText(text[i]);
            mRlAnswerList.addView(chooseButton);
            buttonlist.add(chooseButton);
            setOptionClick(chooseButton, i);
        }
        mRlAnswerList.post(new Runnable() {
            @Override
            public void run() {
                int measuredHeight = mRlAnswerList.getMeasuredHeight() - 40;
                ViewGroup.LayoutParams layoutParams = mTvDisp.getLayoutParams();
                layoutParams.height = measuredHeight;
                mTvDisp.setLayoutParams(layoutParams);
            }
        });

    }

    /**
     * 选项按钮的点击
     *
     * @param button
     * @param num
     */
    public void setOptionClick(ChooseButton button, final int num) {
        if (mRlAnswerList == null) {
            return;
        }
        if (mRlAnswerList.getChildCount() <= 0) {
            return;
        }
        final Button optionButton = button.getOptionButton();
        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionButton.setBackgroundResource(R.drawable.shape_choose_check);
                mRlAnswerSubmit.setBackgroundResource(R.drawable.shap_answer_judget_button_submit);
                mRlAnswerSubmit.setClickable(true);
                result = num;
                for (int i = 0; i < buttonlist.size(); i++) {
                    if (i != num) {
                        buttonlist.get(i).getOptionButton().setBackgroundResource(R.drawable.shape_no_check);
                    }

                }
            }
        });


    }

}
