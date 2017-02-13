package vmediacn.com.activity.mine;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kymjs.kjframe.ui.BindView;

import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
/*
* 我的余额
*
* */
public class ActMyBalance extends BaseActivity {
    @BindView(id = R.id.act_yue_chargeId, click = true)
    private TextView chargeTv;

    @BindView(id = R.id.act_yue_setPasswordLayId, click = true)
    private LinearLayout setPasswordLay;
    @Override
    public void setRootView() {

        setContentView(R.layout.act_my_yu_e);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeText("我的余额");

    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.act_yue_chargeId:
                intent.setClass(ActMyBalance.this, ActChooseBank.class);//充值
                intent.putExtra("hasChoose", false);
                break;

            case R.id.act_yue_setPasswordLayId://设置支付密码
                intent.setClass(ActMyBalance.this, ActSetPayPwdSet.class);
                break;

        }
        startActivity(intent);
    }
}
