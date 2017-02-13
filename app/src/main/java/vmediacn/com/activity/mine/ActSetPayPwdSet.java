package vmediacn.com.activity.mine;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.PreferenceHelper;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
/*
* 设置支付密码
*
* */
public class ActSetPayPwdSet extends BaseActivity
{
    private String TAG = "vmediacn.com.activity.mine.ActSetPayPwdSet--";
    private TextView PayPassward ,SecondPayPassward;
    private String pay,second_pay;
    private Button put;
    @Override
    public void setRootView() {
        setContentView(R.layout.act_set_pay_pwd_set);

    } @Override
      public void initWidget() {
    super.initWidget();
    changeText("新建支付密码");
    PayPassward = (TextView) findViewById(R.id.act_set_reset_pwd_new);
    SecondPayPassward = (TextView) findViewById(R.id.act_set_reset_pwd_new_again);
    put = (Button) findViewById(R.id.act_set_reset_pwd_change_put);
    put.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isCheck()){
                putPayPassward(pay);
            }
        }
    });

}
    private void putPayPassward(String pay){
        KJHttp http = new KJHttp();
        JSONObject object = new JSONObject();
        try {
            object.put("Name", Common.Usr_PhoneNumber);
            object.put("ZFPawd", pay);
            HttpParams params =new HttpParams();
            params.putJsonParams(object.toString());
            http.jsonPost(Common.MSUri + "ZFpawd", params, new HttpCallBack() {
                @Override
                public void onSuccess(String json) {
                    JSONTokener tokener = new JSONTokener(json);
                    KJLoger.log(TAG, "设置支付密码请求成功" + json);
                    try {
                        JSONObject object1 = (JSONObject) tokener.nextValue();
                        String resultState = object1.getString("resultState");
                        String message = object1.getString("message");
                        if (resultState.equals("true")){
                            Common.isPaypwd="1";
                            PreferenceHelper.write(ActSetPayPwdSet.this, "UserInfo", "isSetPaypwd", "1");
                            ShowToast(getApplicationContext(), message);
                            setResult(100);
                            onBackPressed();
                        }else {
                            note(ActSetPayPwdSet.this,"提示：",message,"确定");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "设置支付密码请求失败" + strMsg);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    };
    private Boolean isCheck(){
        pay = PayPassward.getText().toString();
        second_pay = SecondPayPassward.getText().toString();
        if (pay.equals("")){
            PayPassward.requestFocus();
            note(ActSetPayPwdSet.this, "提示：", "请输入新支付密码", "确定");
            return false;

        }
        if (!pay.equals(second_pay)){
            PayPassward.requestFocus();
            note(ActSetPayPwdSet.this,"提示：","两次输入的密码不一致","确定");
            return false;

        }
        return true;
    }
}
