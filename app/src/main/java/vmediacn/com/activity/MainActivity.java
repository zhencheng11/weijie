package vmediacn.com.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.mapapi.model.LatLng;

import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.KJFragment;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.PreferenceHelper;

import cn.jpush.android.api.JPushInterface;
import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.load.LoginActivity;
import vmediacn.com.allBean.weijie.MyLatLongResult;
import vmediacn.com.fragment.FgtBill;
import vmediacn.com.fragment.FgtFind;
import vmediacn.com.fragment.FgtMine;
import vmediacn.com.fragment.FgtWeiJie;
import vmediacn.com.ui.KeyBoardLayout;
import vmediacn.com.util.MyUtil;

/*
*主界面
* */
public class MainActivity extends BaseActivity implements MyLatLongResult {

    @BindView(id = R.id.act_main_rb_weijieId, click = true)
    private RadioButton weijieBt;

    @BindView(id = R.id.act_main_rb_findId, click = true)
    private RadioButton findBt;

    @BindView(id = R.id.act_main_rb_orderId, click = true)
    private RadioButton orderBt;

    @BindView(id = R.id.act_main_rb_mineId, click = true)
    private RadioButton mineBt;
    public static boolean isForeground = false;
    private String TAG = "vmediacn.com.activity.MainActivity";
    private KJFragment fragment;
    private FgtWeiJie fgtWeiJie;
    private FgtFind fgtFind;
    private FgtBill fgtBill;
    private FgtMine fgtMine;
    private KeyBoardLayout mRootView;
    private RadioGroup radioGroup;
    private LatLng latLng1;
//定位接口

    @Override
    public void latLongResult(LatLng latLng) {
        KJLoger.log(TAG, "--自定义接口-latLng-" + latLng);

    }
    @Override
    public void setRootView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initData() {
        super.initData();

        String currentLocation = PreferenceHelper.readString(MainActivity.this, "UserInfo", "currentLocation");//当前位置
        String Account = PreferenceHelper.readString(MainActivity.this, "UserInfo", "Account");//手机号
        String name = PreferenceHelper.readString(MainActivity.this, "UserInfo", "Name", Account);//名字
        String niCheng = PreferenceHelper.readString(MainActivity.this, "UserInfo", "niCheng", "");//昵称
        PreferenceHelper.write(MainActivity.this,"UserInfo","isPaypwd",true);
        String isSetPayWord = PreferenceHelper.readString(MainActivity.this, "UserInfo", "isSetPaypwd", "0");//1已设置支付密码//0没设置
        boolean isLogin0 = PreferenceHelper.readBoolean(MainActivity.this, "UserInfo", "isLogin");
        //if (isLogin0) {
            Common.isLogin = true;
        //} else {
          //  Common.isLogin = false;
        //}

        Common.Usr_PhoneNumber = "13717899174";
        Common.isPaypwd = 1 + "";
        Common.currentLocation = currentLocation;
        KJLoger.log(TAG,"--Usr_PhoneNumber--"+Account);
        KJLoger.log(TAG,"--niCheng--"+niCheng);
        KJLoger.log(TAG,"--isSetPayWord--"+isSetPayWord);
        KJLoger.log(TAG,"--currentLocation--"+currentLocation);
    }

   @Override
    public void initWidget() {
        super.initWidget();

       fgtWeiJie = new FgtWeiJie();
       fgtFind = new FgtFind();
       fgtBill = new FgtBill();
       fgtMine = new FgtMine();
       initInputDate();



    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    KJLoger.log(TAG,"--显示输入法--");
                    radioGroup.setVisibility(View.GONE);
                    break;
                case 2:
                    KJLoger.log(TAG,"--隐藏输入法--");
                    radioGroup.setVisibility(View.VISIBLE);
                    break;
            }

        }
    };
    private void initInputDate() {
        mRootView = (KeyBoardLayout) findViewById(R.id.main_root);
        radioGroup = (RadioGroup) findViewById(R.id.act_main_goupId);
        mRootView.setOnSizeChangedListener(new KeyBoardLayout.onSizeChangedListener() {
            @Override
            public void onChanged(boolean showKeyboard) {
                if (showKeyboard) {

                    handler.obtainMessage(1).sendToTarget();
                }else {

                    handler.obtainMessage(2).sendToTarget();
                }
            }
        });

        changeFragment(R.id.act_main_frameLayoutId, fgtWeiJie);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!MyUtil.isNetworkConnected(MainActivity.this)) {
                    note(MainActivity.this, "提示", "请链接网络", "确定");
                    return;
                }
                switch (checkedId) {
                    case R.id.act_main_rb_weijieId:
                        changeFragment(R.id.act_main_frameLayoutId, fgtWeiJie);
                        break;

                    case R.id.act_main_rb_findId:
                        changeFragment(R.id.act_main_frameLayoutId, fgtFind);
                        break;

                    case R.id.act_main_rb_orderId:
                        if (!Common.isLogin) {
                            new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage("请登录")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                            startActivityForResult(intent, 100);
                                        }
                                    }).setIcon(R.mipmap.ic_note).show();
                        } else {
                            changeFragment(R.id.act_main_frameLayoutId, fgtBill);
                        }
                        break;
                    case R.id.act_main_rb_mineId:
                        KJLoger.log(TAG,"--我的" +"点击了--");
                        changeFragment(R.id.act_main_frameLayoutId, fgtMine);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            changeFragment(R.id.act_main_frameLayoutId,new FgtBill());
        }
    }

    public int getBottomViewHeight() {

        int height = radioGroup.getBottom();
        KJLoger.log(TAG, "--getBottomViewHeight--" + height);
        return height;
    }

    public void hideBottomView() {
        radioGroup.setVisibility(View.GONE);
    }

    public void showBottomView() {
        radioGroup.setVisibility(View.VISIBLE);
    }

    //接收自定义的激光推送的消息
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }
    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!TextUtils.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                setCostomMsg(showMsg.toString());
            }
        }
    }

    private void setCostomMsg(String msg){
        /*if (null != msgText) {
            msgText.setText(msg);
            msgText.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(MainActivity.this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        JPushInterface.onResume(MainActivity.this);
    }
}
