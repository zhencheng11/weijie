package vmediacn.com.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
/*
* 输入支付密码
*
* */
public class ActChargeInputPassword extends BaseActivity {

    @BindView(id = R.id.act_wsbill_pay_ensurePayMyBalanceLayId)
    private LinearLayout banlanceLay;

    @BindView(id = R.id.act_wsbill_pay_ensurePay_type_nameId)
    private TextView typeNameTv;

    @BindView(id = R.id.act_wsbill_pay_moneyId)
    private TextView payMoneyTv;

    private TextView verification;
    private TableLayout table;
    private ProgressBar bar;
    private StringBuffer buffer = new StringBuffer();
    private TextView[] texts ;
    private Button[] btns;
    public Context context;

    private String TAG = "--ActChargeInputPassword--";
    @Override
    public void setRootView() {
        setContentView(R.layout.act_charge_input_password);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        context = ActChargeInputPassword.this;
        changeText("确认充值");
        banlanceLay.setVisibility(View.GONE);
        typeNameTv.setText("充值金额");
        Context context = ActChargeInputPassword.this;
        Intent intent = getIntent();
        String chageMoney = null;
        if (intent != null) {

            chageMoney = intent.getStringExtra("chageMoney");
        }
        payMoneyTv.setText(chageMoney);
        initPop(context);
    }
    private void initPop(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_safely_keyboard, null);
        verification = (TextView) findViewById(R.id.pop_safely_keyboard_verification);

        table = (TableLayout) findViewById(R.id.pop_safely_keyboard);
        bar = (ProgressBar) findViewById(R.id.pop_safely_keyboard_bar);
        texts = new TextView[]{
                (TextView) findViewById(R.id.pop_safely_keyboard_tv1),
                (TextView) findViewById(R.id.pop_safely_keyboard_tv2),
                (TextView) findViewById(R.id.pop_safely_keyboard_tv3),
                (TextView) findViewById(R.id.pop_safely_keyboard_tv4),
                (TextView) findViewById(R.id.pop_safely_keyboard_tv5),
                (TextView) findViewById(R.id.pop_safely_keyboard_tv6),
        };
        btns = new Button[]{
                (Button) findViewById(R.id.pop_safely_keyboard_btn0),
                (Button) findViewById(R.id.pop_safely_keyboard_btn1),
                (Button) findViewById(R.id.pop_safely_keyboard_btn2),
                (Button) findViewById(R.id.pop_safely_keyboard_btn3),
                (Button) findViewById(R.id.pop_safely_keyboard_btn4),
                (Button) findViewById(R.id.pop_safely_keyboard_btn5),
                (Button) findViewById(R.id.pop_safely_keyboard_btn6),
                (Button) findViewById(R.id.pop_safely_keyboard_btn7),
                (Button) findViewById(R.id.pop_safely_keyboard_btn8),
                (Button) findViewById(R.id.pop_safely_keyboard_btn9),
                (Button) findViewById(R.id.pop_safely_keyboard_back),
        };

        for (int i = 0; i < btns.length; i++) {
            btns[i].setOnClickListener(new MyOnClickListener(i));

        }
    }
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            KJLoger.log(TAG, "---click index--" + index);
            if (index == 10) {
                if (!isNull()) {
                    buffer.deleteCharAt(buffer.length() - 1);
                    texts[buffer.length()].setText("");
                }
            } else {
                buffer.append(String.valueOf(index));
                texts[buffer.length() - 1].setText("•");
                if (buffer.length() == 6) {
                    //myPayPasswordListener.Success(buffer.toString());
                    Toast.makeText(context, buffer.toString(), Toast.LENGTH_SHORT).show();

                    pay(buffer.toString());
                    table.setVisibility(View.GONE);
                    verification.setVisibility(View.VISIBLE);
                    bar.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void pay(String s) {

    }

    private boolean isNull(){
        if (buffer.toString().equals("")){
            return true;
        }else {
            return false;
        }
    }
}
