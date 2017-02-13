package vmediacn.com.activity.weijie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.activity.mine.ActMyBalance;
import vmediacn.com.activity.mine.ActSetPayPwdSet;
/*
* 付款页面
* */
public class ActWSBillPay extends BaseActivity {

    private String TAG = "vmediacn.com.activity.weijie.ActWSBillPay--";
    @BindView(id = R.id.act_wsbill_pay_ensurePayId, click = true)
    private Button payBtn;

    @BindView(id = R.id.act_wsbill_pay_moneyId, click = true)
    private TextView payMoneyTv;

    @BindView(id = R.id.keyboard_parentLayId)
    private LinearLayout keyboarParentLay;
    //我的余额
    @BindView(id = R.id.act_wsbill_pay_ensurePayMyBalanceId,click = true)
    private LinearLayout myBalanceLay;

    private View parentView;
    private String billId;
    private String allMoney;

    private TextView verification;
    private TableLayout table;
    private ProgressBar bar;
    private StringBuffer buffer = new StringBuffer();
    private TextView[] texts ;
    private Button[] btns;
    public Context context;
    private Button btn_diss;



    @Override
    public void setRootView() {

        setContentView(R.layout.ac_wsbill_pay);

    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeText("付款");
        Intent intent = getIntent();
        if (intent != null) {
            billId = intent.getStringExtra("billId");
            allMoney = intent.getStringExtra("allMoney");
        }
        payMoneyTv.setText(allMoney);
    }

    @Override
    public void initData() {
        super.initData();
        context = ActWSBillPay.this;
        parentView = findViewById(R.id.act_wsbill_pay_parentViewId);
        getBill();

        //初始化密码键盘
        if (Common.isPaypwd.equals("1")){
            initPop(context);
        }else {
            keyboarParentLay.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("设置新密码");
            builder.setMessage("您还未设置支付密码，请先设置支付密码再进行支付操作");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context, ActSetPayPwdSet.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            builder.setCancelable(false);//弹出框不可以换返回键取消
            AlertDialog dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//将弹出框设置为全局
            dialog.setCanceledOnTouchOutside(false);//失去焦点不会消失
            dialog.show();
        }
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.act_wsbill_pay_ensurePayMyBalanceId://跳转余额界面
                Intent intent = new Intent(ActWSBillPay.this, ActMyBalance.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    private void initPop(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_safely_keyboard, null);
        verification = (TextView) findViewById(R.id.pop_safely_keyboard_verification);

        table = (TableLayout) findViewById(R.id.pop_safely_keyboard);
        bar = (ProgressBar) findViewById(R.id.pop_safely_keyboard_bar);
        btn_diss = (Button) findViewById(R.id.pop_safely_keyboard_diss);
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
                    //Toast.makeText(context, buffer.toString(), Toast.LENGTH_SHORT).show();
                    pay(buffer.toString());
                    table.setVisibility(View.GONE);
                    verification.setVisibility(View.VISIBLE);
                    bar.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    private boolean isNull(){
        if (buffer.toString().equals("")){
            return true;
        }else {
            return false;
        }
    }

    private void pay(String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",billId);
            jsonObject.put("telphone",Common.Usr_PhoneNumber );
            jsonObject.put("ZhiFuPwd", password);
            KJLoger.log(TAG, "--billId--" + billId + "--telephone--" + Common.Usr_PhoneNumber + "--ZhiFuPwd--" + password);
            HttpParams params = new HttpParams();
            KJHttp http = new KJHttp();
            KJLoger.log(TAG, "--json--"+jsonObject.toString());

            http.jsonPost(Common.MSUri + "Payment", params, false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "付款请求成功0" + t);
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "付款请求失败0" + strMsg);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void getBill() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", Common.Usr_PhoneNumber);
            jsonObject.put("pageSize", 6);
            jsonObject.put("pageIndex", 1);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Orderlist", params, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "获取订单请求成功了" + t);
                    JSONTokener tokener = new JSONTokener(t);
                    try {
                        JSONObject jsonObject1 = (JSONObject) tokener.nextValue();
                        String resultState = jsonObject1.getString("resultState");
                        if (resultState.equals("true")) {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "请求订单失败" + strMsg);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            if (Common.isPaypwd.equals("1")) {
                keyboarParentLay.setVisibility(View.VISIBLE);
                initPop(context);
            }
        }
    }
}
