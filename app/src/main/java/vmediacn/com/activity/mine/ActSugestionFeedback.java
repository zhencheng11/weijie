package vmediacn.com.activity.mine;

import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
/*
* 建议反馈
*
* */
public class ActSugestionFeedback extends BaseActivity {

    @Override
    public void setRootView() {
        setContentView(R.layout.act_sugestion_feedback);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeText("意见反馈");
    }
}
