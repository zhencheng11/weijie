package vmediacn.com.activity.load;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
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
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.SystemTool;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.activity.MainActivity;
import vmediacn.com.util.MyUtil;
/*
*登录
* */
public class LoginActivity extends BaseActivity {

    private String TAG = "vmediacn.com.activity.load.LoginActivity";
    private EditText phoneNumberEt;
    private EditText passWordEt;
    private EditText verificationEt;
    private EventHandler eh = new EventHandler() {

        @Override
        public void afterEvent(int event, int result, Object data) {

            if(result== SMSSDK.RESULT_ERROR){
                KJLoger.log(TAG, "==RESULT_ERROR==" + ((Throwable) data).getMessage());
            }
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            handler.sendMessage(msg);
        }

    };
    private int time=60;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            KJLoger.log(TAG, "--result==" + result);
            KJLoger.log(TAG, "--event==" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                // 短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                    getLoginDate();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
//                    isPost=true;
                    //MyBuilder.closeDialog();
                    Toast.makeText(getApplicationContext(), "验证码已经发送，请查收。", Toast.LENGTH_SHORT).show();
                    getCodeBT.setEnabled(false);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getCodeBT.setEnabled(true);
                            getCodeBT.setText("获取");
                            getCodeBT.setClickable(true);
                            time = 60;
                        }
                    }, 1000 * 61);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int temp = --time;
                            getCodeBT.setText("(" + temp + ")秒");
                            getCodeBT.setEnabled(false);
                            if (time < 1) {
                                getCodeBT.setText("获取");
                                getCodeBT.setEnabled(true);
                                getCodeBT.setClickable(true);
                            } else {
                                handler.postDelayed(this, 1000);
                            }
                        }
                    }, 1000);
                }  else if (event==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){}
                else {
                    Toast.makeText(getApplicationContext(), "验证码不正确!", Toast.LENGTH_SHORT).show();
                }
            } else {
                ((Throwable) data).printStackTrace();
                Toast.makeText(getApplicationContext(), "验证码不正确!", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private Button getCodeBT;


    @Override
    public void setRootView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initWidget() {
        super.initWidget();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.act_login_getverification_login_btn:
                //登录
                KJLoger.log(TAG, " 登录点击了1");
                //pass_verification();
                getLoginDate();
//
//                loginTest();
                break;

            case R.id.act_login_getverification_code_btn:
                getCode();
                break;

            case R.id.act_login_forget_password:
                break;

            case R.id.head_title_view_menuId:
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(intent);
                break;

        }
        super.onClick(v);
    }

    private void loginTest() {
        DisplayMetrics dm = new DisplayMetrics();
        Display dispaly = getWindowManager().getDefaultDisplay();
        dispaly.getMetrics(dm);
        String phoneWH = ("w" + dm.widthPixels + "h " + dm.heightPixels);
        //        设备ID
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //String deviceid = tm.getDeviceId();
        //        推送ID
        String Pushid = JPushInterface.getRegistrationID(this);
        //        获取 Imei号码
        int imei = tm.getSimState();
        //        获取手机类型
        String phoneType = Build.MODEL;
        //        手机系统
        String OSVison = Build.VERSION.RELEASE;
        //        手机标识
        String uuid = "Android";


        JSONObject json = new JSONObject();
        try {
            json.put("account", "13717899174");
            json.put("password", "131415");
            json.put("deviceId","");
            json.put("imei",imei);
            json.put("phoneType",phoneType);
            json.put("phoneWH",phoneWH);
            json.put("OSVsion",OSVison);
            json.put("uuid",uuid);
            json.put("PushId",Pushid);
            json.put("Version", "1.0");
            HttpParams params = new HttpParams();
            params.putJsonParams(json.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Longin", params, false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "测试longin数据--" + t);
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "测试longin数据获取失败--" + strMsg);

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void note(Context context,String title, String content, String button,int iconId) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(content)
                .setPositiveButton(button, null).setIcon(iconId)
                .show();
    }
    private void pass_verification() {

        String phone = phoneNumberEt.getText().toString();
        String password = passWordEt.getText().toString();
        String verificationCode = verificationEt.getText().toString();
        //
        if (phone.equals("")) {
            phoneNumberEt.requestFocus();
            note(LoginActivity.this, "提示：", "请输入您的账号", "确定",R.mipmap.ic_note);
            return;
        }
        if (!MyUtil.isMobileNumber(phone)) {
            phoneNumberEt.requestFocus();
            note(LoginActivity.this, "提示：", "请输入正确的手机号", "确定",R.mipmap.ic_note);
            return;
        }
        if (password.equals("")) {
            passWordEt.requestFocus();
            note(LoginActivity.this, "提示：", "请输入您的密码", "确定",R.mipmap.ic_note);
            return;
        }
        if (password.length() < 6) {
            passWordEt.requestFocus();
            note(LoginActivity.this, "提示", "密码位数少于6位", "确定",R.mipmap.ic_note);
            return;
        }

        if (password.length() > 12) {
            passWordEt.requestFocus();
            note(LoginActivity.this, "提示", "密码位数多于12位", "确定",R.mipmap.ic_note);
            return;
        }
        /*if (TextUtils.isEmpty(verificationCode)) {
            note(LoginActivity.this, "提示：", "请确认验证码", "确定",R.mipmap.ic_note);
            return;
        }*/
        if(!SystemTool.checkNet(getApplicationContext())){
            Toast.makeText(getApplicationContext(),"无网络，请检查网络",Toast.LENGTH_LONG).show();
            return;
        }
        getLoginDate();
        //SMSSDK.submitVerificationCode("86", phone, verificationCode);

    }

    @Override
    public void initData() {
        super.initData();

        phoneNumberEt = (EditText) findViewById(R.id.act_login_phoneNumber);
        passWordEt = (EditText) findViewById(R.id.act_login_passWordId);
        verificationEt = (EditText) findViewById(R.id.act_login_verification_code);

        getCodeBT = (Button) findViewById(R.id.act_login_getverification_code_btn);
        getCodeBT.setOnClickListener(this);

        Button loginBT = (Button) findViewById(R.id.act_login_getverification_login_btn);

        loginBT.setOnClickListener(this);

        TextView forgetTV = (TextView) findViewById(R.id.act_login_forget_password);

        TextView registTV = (TextView) findViewById(R.id.head_title_view_menuId);
        TextView title = (TextView) findViewById(R.id.head_title_view_textId);
        title.setText("登录");
        registTV.setOnClickListener(this);
        registTV.setVisibility(View.VISIBLE);
        registTV.setText("注册");


    }
    private void getCode() {
        String phone = phoneNumberEt.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            phoneNumberEt.requestFocus();
            note(LoginActivity.this, "提示", "请输入手机号", "确定", R.mipmap.ic_note);
            return;
        }
        if (!MyUtil.isMobileNumber(phone)) {
            phoneNumberEt.requestFocus();
            note(LoginActivity.this, "提示：", "请输入正确的手机号", "确定", R.mipmap.ic_note);
            return;
        }
        //SMSSDK.unregisterAllEventHandler();
        SMSSDK.getSupportedCountries();
        SMSSDK.getVerificationCode("86", phone);
        SMSSDK.registerEventHandler(eh);
        getCodeBT.setClickable(true);
        //MyBuilder.showDialog(this);


    }
    private void getLoginDate() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", phoneNumberEt.getText().toString());
            jsonObject.put("Pawd", passWordEt.getText().toString());
            KJLoger.log(TAG, "--Name--" + phoneNumberEt.getText().toString());
            KJLoger.log(TAG, "--Pawd--" + passWordEt.getText().toString());
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());

            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri+"Login", params, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "--登录请求成功" + t);
                    JSONTokener jsonTokener = new JSONTokener(t);
                    try {
                        JSONObject jsonObject1 = (JSONObject) jsonTokener.nextValue();
                        String resultState = jsonObject1.getString("resultState");
                        String message = jsonObject1.getString("message");
                        if (resultState.equals("true")) {
                            PreferenceHelper.write(LoginActivity.this,"UserInfo","Account",phoneNumberEt.getText().toString());
                            PreferenceHelper.write(LoginActivity.this,"UserInfo","PassWord",passWordEt.getText().toString());
                            PreferenceHelper.write(LoginActivity.this,"UserInfo","isFirst",false);
                            PreferenceHelper.write(LoginActivity.this,"UserInfo","isLogin",true);

                            Common.isLogin = true;
                            Common.Usr_PhoneNumber = PreferenceHelper.readString(LoginActivity.this,"UserInfo","Account");
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            setResult(1, intent);
                            //skipActivity(LoginActivity.this, MainActivity.class);
                            onBackPressed();

                        }else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "--登录失败" + strMsg);
                    KJLoger.log(TAG, "--errorNo" + errorNo);
                    Toast.makeText(LoginActivity.this,"登录失败" +strMsg, Toast.LENGTH_LONG).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
