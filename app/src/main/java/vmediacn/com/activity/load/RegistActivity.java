package vmediacn.com.activity.load;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.SystemTool;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.util.MyUtil;
/*
* 注册
* */
public class RegistActivity extends BaseActivity {

    private String TAG = "vmediacn.com.activity.load.RegistActivity";
    private EditText phoneNumberEt;
    private EditText passWordEt;
    private EditText verificationEt;
    private Button getCodeBT;
    private int time=60;
    private EditText second_passwordET;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_regist);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.act_regist_btn:
                //注册
                pass_verification();
                //getRegistData();
            break;
            case R.id.act_regist_code_btn:
                //获取验证码
                getCode();
                break;

        }
    }

    private void pass_verification() {

        String phone = phoneNumberEt.getText().toString();
        String password = passWordEt.getText().toString();

        String second_password = second_passwordET.getText().toString();
        String verificationCode = verificationEt.getText().toString();
        //
        if (phone.equals("")) {
            phoneNumberEt.requestFocus();
            note(RegistActivity.this, "提示：", "请输入您的账号", "确定");
            return;
        }
        if (!MyUtil.isMobileNumber(phone)) {
            phoneNumberEt.requestFocus();
            note(RegistActivity.this, "提示：", "请输入正确的手机号", "确定");
            return;
        }
        if (password.equals("")) {
            passWordEt.requestFocus();
            note(RegistActivity.this, "提示：", "请输入您的密码", "确定");
            return;
        }
        if (password.length() < 6) {
            passWordEt.requestFocus();
            note(RegistActivity.this, "提示", "密码位数少于6位", "确定");
            return;
        }

        if (password.length() > 12) {
            passWordEt.requestFocus();
            note(RegistActivity.this, "提示", "密码位数多于12位", "确定");
            return;
        }

        if (second_password.equals("")) {
            second_passwordET.requestFocus();
            note(RegistActivity.this, "提示：", "请确认您的密码", "确定");
            return;
        }

        if (!password.equals(second_password)) {
            passWordEt.setText("");
            second_passwordET.setText("");
            passWordEt.requestFocus();
            note(RegistActivity.this, "提示：", "两次输入的密码不一致", "确定");
            return;
        }

        /*if (TextUtils.isEmpty(verificationCode)) {
            note(RegistActivity.this, "提示：", "请确认验证码", "确定");
            return;
        }*/
        if(!SystemTool.checkNet(getApplicationContext())){
            Toast.makeText(getApplicationContext(),"无网络，请检查网络",Toast.LENGTH_LONG).show();
        }
        //改
        getRegistData();//去掉验证码

      //SMSSDK.submitVerificationCode("86", phone, verificationCode);
    }

    private void getCode() {
        String phone = phoneNumberEt.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ShowToast(RegistActivity.this,"请输入手机号");
            return;
        }
        if (!MyUtil.isMobileNumber(phone)) {
            phoneNumberEt.requestFocus();
            ShowToast(RegistActivity.this, "请输入正确的手机号");
            return;
        }

        EventHandler eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message message = Message.obtain();
                message.arg1 = event;
                message.arg2 = result;
                message.obj = data;
                handler.sendMessage(message);
            }
        };

        SMSSDK.unregisterAllEventHandler();
        SMSSDK.registerEventHandler(eventHandler);
        SMSSDK.getSupportedCountries();
        SMSSDK.getVerificationCode("86", phone);
        KJLoger.log(TAG,"--getCode--执行了");
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                // 短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                    getRegistData();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "验证码已经发送，请查收。", Toast.LENGTH_SHORT).show();
                    getCodeBT.setEnabled(false);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getCodeBT.setEnabled(true);
                            getCodeBT.setText("获取");
                            time = 60;
                        }
                    }, 1000 * 62);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int temp = --time;
                            getCodeBT.setText("(" + temp + ")秒");
                            getCodeBT.setEnabled(false);
                            if (time < 1) {
                                getCodeBT.setText("获取");
                            } else {
                                handler.postDelayed(this, 1000);
                            }
                        }
                    }, 1000);
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {// 返回支持发送验证码的国家列表
                } else {
                    Toast.makeText(getApplicationContext(), "验证码不正确", Toast.LENGTH_SHORT).show();
                }
            } else {
                ((Throwable) data).printStackTrace();
                Toast.makeText(getApplicationContext(), "验证码不正确", Toast.LENGTH_SHORT).show();
            }
        }
    };
    protected void note(Context context,String title, String content, String button) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(content)
                .setPositiveButton(button, null).setIcon(R.mipmap.ic_note)
                .show();
    }
    @Override
    public void initData() {
        super.initData();
        TextView title = (TextView) findViewById(R.id.head_title_view_textId);
        title.setText("注册");
        phoneNumberEt = (EditText) findViewById(R.id.act_login_phoneNumber);
        passWordEt = (EditText) findViewById(R.id.act_login_passWordId);
        second_passwordET = (EditText) findViewById(R.id.act_login_secod_passWordId);
        verificationEt = (EditText) findViewById(R.id.act_login_verification_code);

        getCodeBT = (Button) findViewById(R.id.act_regist_code_btn);
        getCodeBT.setOnClickListener(this);
        Button registBT = (Button) findViewById(R.id.act_regist_btn);
        registBT.setOnClickListener(this);

    }

    public void getRegistData() {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", phoneNumberEt.getText().toString());
            jsonObject.put("Pawd", passWordEt.getText().toString());
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri+"Register", params, false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "注册请求成功" + t);
                    JSONTokener jsonTokener = new JSONTokener(t);
                    try {
                        JSONObject jsonObject1 = (JSONObject) jsonTokener.nextValue();
                        String resultState = jsonObject1.getString("resultState");
                        String message = jsonObject1.getString("message");
                        if (resultState.equals("true")) {
                            ShowToast(RegistActivity.this, message);
                            String usePhone = phoneNumberEt.getText().toString();
                            Common.Usr_PhoneNumber = usePhone;
                            onBackPressed();
                        }else {
                            ShowToast(RegistActivity.this, message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "注册失败" + strMsg);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
