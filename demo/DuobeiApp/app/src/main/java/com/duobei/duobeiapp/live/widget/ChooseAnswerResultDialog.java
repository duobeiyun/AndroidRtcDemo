package com.duobei.duobeiapp.live.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.common.ChooseButton;
import com.duobei.duobeiapp.common.ProgressResultBar;
import com.duobei.duobeiapp.live.CustomizedLiveActivity;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *** Created by  com.duobei.duobeiapp.live.widget by yangge on 2017/10/26 
 *** mail:ge123.yang@gmail.com
 * 显示选择题答题结果的dialog
 **/
public class ChooseAnswerResultDialog extends Dialog {

    CustomizedLiveActivity mActivity;
    @BindView(R.id.ib_answer_title)
    TextView mIbAnswerTitle;
    @BindView(R.id.ib_answer_close)
    ImageButton mIbAnswerClose;
    @BindView(R.id.rl_answer_list)
    LinearLayout mRlAnswerList;
    @BindView(R.id.ll_answer_progress_list)
    LinearLayout mLlAnswerProgressList;
    @BindView(R.id.rl_cotent)
    RelativeLayout mRlCotent;
    @BindView(R.id.tv_refresh_answer_number)
    TextView mTvRefreshAnswerNumber;
    @BindView(R.id.ib_answer_refresh)
    ImageButton mIbAnswerRefresh;
    @BindView(R.id.rl_answer_refresh)
    RelativeLayout mRlAnswerRefresh;
    @BindView(R.id.tv_answer_end)
    TextView mTvAnswerEnd;
    @BindView(R.id.rl_answer_refresh_result)
    RelativeLayout mRlAnswerRefreshResult;
    private String mResult;

    public ChooseAnswerResultDialog(@NonNull CustomizedLiveActivity context) {
        super(context, R.style.inputdialog);
        mActivity = context;
        setContentView(R.layout.answer_dialog_choose_result);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mIbAnswerTitle.setText("选择题");
        Drawable drawable = mActivity.getResources().getDrawable(R.drawable.answer_choose);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mIbAnswerTitle.setCompoundDrawables(drawable, null, null, null);
        mResult = mActivity.getString(R.string.answer_current_number);
        mTvRefreshAnswerNumber.setText(String.format(mResult, 0));
        mRlAnswerRefreshResult.setVisibility(View.GONE);
        mRlAnswerRefresh.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.ib_answer_close})
    public void onlClick(View view) {
        switch (view.getId()) {
            case R.id.ib_answer_close:
                ChooseAnswerResultDialog.this.dismiss();
                break;
        }
    }

    /**
     * @param json              返回的答题结果
     *                          {"A":0,"B":1,"C":0,"D":0,"E":0}
     * @param type              题型
     *                          10  单选 AB两个选项
     *                          11  单选 ABC三个选项
     *                          12  单选 ABCD四个选项
     *                          13  单选 ABCDE五个选项
     *                          30  判断 √或×
     * @param yourChooseOPtions 你所选择的选项
     *                          相关的方法可以优化，时间原因，可供参考<^_^>
     */
    public void getAnswerResult(JSONObject json, int type, int yourChooseOPtions) {
        if (type == 10) {
            drawDialog(2, new String[]{"A", "B"}, new int[]{json.optInt("A"),
                    json.optInt("B")}, yourChooseOPtions);
        } else if (type == 11) {
            drawDialog(3, new String[]{"A", "B", "C"}, new int[]{json.optInt("A"),
                            json.optInt("B"), json.optInt("C")}
                    , yourChooseOPtions);

        } else if (type == 12) {
            drawDialog(4, new String[]{"A", "B", "C", "D"}, new int[]{json.optInt("A"),
                    json.optInt("B"), json.optInt("C")
                    , json.optInt("D")}, yourChooseOPtions);

        } else if (type == 13) {
            drawDialog(5, new String[]{"A", "B", "C", "D", "E"}, new int[]{json.optInt("A"),
                    json.optInt("B"), json.optInt("C")
                    , json.optInt("D"), json.optInt("E")}, yourChooseOPtions);

        }
    }

    private void drawDialog(int num, String[] texts, int[] answers, int yourChooseOPtions) {
        mRlAnswerList.removeAllViews();
        mLlAnswerProgressList.removeAllViews();
        float sum = getSumAnswerResult(answers);
        for (int i = 0; i < num; i++) {
            ChooseButton chooseButton = new ChooseButton(mActivity.getApplicationContext());
            chooseButton.setOptinsText(texts[i]);
            chooseButton.setEnabled(false);
            chooseButton.setOptintsColor(R.color.color_answer_result_show);
            chooseButton.setOptinsBackground(R.drawable.shape_choose_button_result);
            if (i == yourChooseOPtions) {
                chooseButton.getYourChooseButton().setVisibility(View.VISIBLE);
            } else {
                chooseButton.getYourChooseButton().setVisibility(View.GONE);
            }
            mRlAnswerList.addView(chooseButton);
            ProgressResultBar progressResultBar = new ProgressResultBar(mActivity.getApplicationContext());
            progressResultBar.setanswerNumberText("" + answers[i]);
            progressResultBar.setanswerPercentText("" + (int) ((answers[i] / sum) * 100) + "%");
            progressResultBar.setProgressNumber((int) ((answers[i] / sum) * 100));
            mLlAnswerProgressList.addView(progressResultBar);
            resizeProgressLayout(i, progressResultBar);
        }
        mTvRefreshAnswerNumber.setText(String.format(mResult, (int) sum));

    }

    private float getSumAnswerResult(int[] answers) {
        int sum = 0;
        for (int i = 0; i < answers.length; i++) {
            sum = sum + answers[i];
        }
        return sum;

    }

    private void resizeProgressLayout(int num, final ProgressResultBar progressResultBar) {
        if (mLlAnswerProgressList == null) {
            return;
        }
        if (mLlAnswerProgressList.getChildCount() <= 0) {
            return;
        }
        if (num == 0) {
            progressResultBar.post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) progressResultBar.getLayoutParams();
                    layoutParams.setMargins(0, dip2px(mActivity, 6), 0, 0);
                }
            });
        } else {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) progressResultBar.getLayoutParams();
            layoutParams.setMargins(0, dip2px(mActivity, 42), 0, 0);
        }

    }

    public static int dip2px(Context context, float pxValue) {

        /*final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (pxValue / scale +0.5f);*/
        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (pxValue * scale + 0.5f);


    }

    public void stopAnswer(boolean isStop) {
        if (isStop) {
            mRlAnswerRefreshResult.setVisibility(View.VISIBLE);
            mRlAnswerRefresh.setVisibility(View.GONE);
        } else {
            mRlAnswerRefreshResult.setVisibility(View.GONE);
            mRlAnswerRefresh.setVisibility(View.VISIBLE);
        }

    }

}
