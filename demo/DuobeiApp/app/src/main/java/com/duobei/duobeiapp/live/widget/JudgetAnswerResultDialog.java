package com.duobei.duobeiapp.live.widget;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duobei.duobeiapp.R;
import com.duobei.duobeiapp.common.AnswerType;
import com.duobei.duobeiapp.common.ProgressResultBar;
import com.duobei.duobeiapp.live.CustomizedLiveActivity;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *** Created by  com.duobei.duobeiapp.live.widget by yangge on 2017/10/26 
 *** mail:ge123.yang@gmail.com
 * 显示判断题答题结果的dialog
 **/
public class JudgetAnswerResultDialog extends Dialog {
    CustomizedLiveActivity mActivity;
    @BindView(R.id.ib_answer_title)
    TextView mIbAnswerTitle;
    @BindView(R.id.ib_answer_close)
    ImageButton mIbAnswerClose;
    @BindView(R.id.ib_answer_correct)
    ImageButton mIbAnswerCorrect;
    @BindView(R.id.iv_answer_correct_choosed)
    ImageView mIvAnswerCorrectChoosed;
    @BindView(R.id.rl_answer_correct)
    RelativeLayout mRlAnswerCorrect;
    @BindView(R.id.ib_answer_error)
    ImageButton mIbAnswerError;
    @BindView(R.id.iv_answer_error_choosed)
    ImageView mIvAnswerErrorChoosed;
    @BindView(R.id.rl_answer_list)
    RelativeLayout mRlAnswerList;
    @BindView(R.id.prb_result_currect)
    ProgressResultBar mPrbResultCurrect;
    @BindView(R.id.prb_result_error)
    ProgressResultBar mPrbResultError;
    @BindView(R.id.rl_cotent)
    RelativeLayout mRlCotent;
    @BindView(R.id.tv_answer_number)
    TextView mTvAnswerNumber;
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

    public JudgetAnswerResultDialog(@NonNull CustomizedLiveActivity context) {
        super(context, R.style.inputdialog);
        setContentView(R.layout.answer_dialog_judget_result);
        this.mActivity = context;
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {
        mIvAnswerCorrectChoosed.setVisibility(View.GONE);
        mIvAnswerErrorChoosed.setVisibility(View.GONE);
        mResult = mActivity.getString(R.string.answer_current_number);
        mTvRefreshAnswerNumber.setText(String.format(mResult, 0));
    }


    @OnClick({R.id.ib_answer_close, R.id.ib_answer_refresh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_answer_close:
                JudgetAnswerResultDialog.this.dismiss();
                break;
            case R.id.ib_answer_refresh:
                //刷新
                refreshdata();
                break;
        }

    }

    private String refreshdata() {
        mIbAnswerRefresh.setImageResource(R.drawable.answer_result_refreshing);
        mTvRefreshAnswerNumber.setText("加载中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIbAnswerRefresh.setImageResource(R.drawable.answer_refresh);
                        mTvRefreshAnswerNumber.setText(String.format(mResult, 10));
                    }
                });
            }
        }).start();
        return "";

    }

    /**
     * {"A":0,"B":1,"C":0,"D":0,"E":0}
     *
     * @param json
     */
    public void getResultMessge(JSONObject json, int youchooseOptions) {
        int currectNumber = json.optInt("A");
        int errorNumber = json.optInt("B");
        refreshView(currectNumber, errorNumber);
        updaeChoose(youchooseOptions);
    }

    /**
     * 刷新view
     */
    private void refreshView(int currectNumber, int errorNumber) {
        float sum = currectNumber + errorNumber;
        mPrbResultCurrect.setanswerNumberText("" + currectNumber);
        mPrbResultCurrect.setanswerPercentText("" + (int) ((currectNumber / sum) * 100) + "%");
        mPrbResultCurrect.setProgressNumber((int) ((currectNumber / sum) * 100));

        mPrbResultError.setanswerNumberText("" + errorNumber);
        mPrbResultError.setanswerPercentText("" + (int) ((errorNumber / sum) * 100) + "%");
        mPrbResultError.setProgressNumber((int) ((errorNumber / sum) * 100));
        mTvRefreshAnswerNumber.setText(String.format(mResult, (int) sum));
    }

    public void updaeChoose(int yourChooseOptions) {
        if (AnswerType.RESULTTRUE == yourChooseOptions) {
            mIvAnswerCorrectChoosed.setVisibility(View.VISIBLE);
            mIvAnswerErrorChoosed.setVisibility(View.GONE);
        } else {
            mIvAnswerCorrectChoosed.setVisibility(View.GONE);
            mIvAnswerErrorChoosed.setVisibility(View.VISIBLE);
        }
    }
}
