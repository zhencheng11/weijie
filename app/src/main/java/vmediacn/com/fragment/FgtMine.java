package vmediacn.com.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.widget.RoundImageView;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.load.LoginActivity;
import vmediacn.com.activity.mine.ActConsigneeAddress;
import vmediacn.com.activity.mine.ActMyBalance;
import vmediacn.com.activity.mine.ActMyCollection;
import vmediacn.com.activity.mine.ActMyYouHuiQuan;
import vmediacn.com.activity.mine.ActSugestionFeedback;
import vmediacn.com.activity.mine.ActUserInfo;

/**
 * A simple {@link Fragment} subclass.
 * 我的
 */
public class FgtMine extends BaseFragment {
    //
    @BindView(id =R.id.fgt_mine_user_lineaLayId )
    private LinearLayout linearLayout ;

    @BindView(id =R.id.fgt_mine_user_phoneNumberId )
    private TextView phoneNumberTv ;

    @BindView(id =R.id.fgt_mine_user_nameId )
    private TextView nameTv;

    @BindView(id = R.id.fgt_mine_longinOrRegistId, click = true)
    private TextView loginOrRegist ;

    @BindView(id = R.id.fgt_mine_usrPhotoId,click = true)
    private RoundImageView imageView ;

    private String TAG = "--vmediacn.com.fragment.FgtMine--";

    @BindView(id = R.id.fgt_mine_consignee_addressId, click = true)
    private LinearLayout consignee_address;

    @BindView(id = R.id.fgt_mine_user_relayId, click = true)
    private RelativeLayout userLay;

    @BindView(id = R.id.head_title_view_menuId, click = true)
    private TextView menuLayId;

    @BindView(id = R.id.fgt_mine_collectionLayId, click = true)
    private LinearLayout collectionLayId;

    @BindView(id = R.id.fgt_mine_youHuiQuanId, click = true)
    private LinearLayout youHuiQuanLay;

    @BindView(id = R.id.fgt_mine_consignee_yiJianFanKuiId, click = true)//意见反馈
    private LinearLayout yiJianFanKuiLay;

    @BindView(id = R.id.fgt_mine_myYuEId, click = true)
    private LinearLayout yuELay;

    private KJBitmap kjBitmap ;

    private Context context;
    private boolean isLogin;


    public FgtMine() {

        // Required empty public constructor
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fgt_mine, viewGroup, false);
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle arguments = getArguments();
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        KJLoger.log(TAG,"----widgetClick----");
        Intent intent = new Intent();

        switch (v.getId()) {

            case R.id.fgt_mine_consignee_addressId://收货人地址

                KJLoger.log(TAG, "收货人地址点击了");
                intent.setClass(getActivity(), ActConsigneeAddress.class);

                break;

            case R.id.fgt_mine_longinOrRegistId://跳转登录或者注册页面

                intent.setClass(getActivity(), LoginActivity.class);
                break;

            case R.id.fgt_mine_usrPhotoId://点击头像

                //if (isLogin) {
                    intent.setClass(getActivity(), ActUserInfo.class);
                    intent.putExtra("niCheng", nameTv.getText().toString());
                    intent.putExtra("account", phoneNumberTv.getText().toString());
                startActivity(intent);
                //}

                break;
            case R.id.fgt_mine_user_relayId:

                if (isLogin) {
                    intent.setClass(getActivity(), ActUserInfo.class);
                    intent.putExtra("niCheng", nameTv.getText().toString());
                    intent.putExtra("account", phoneNumberTv.getText().toString());
                    startActivity(intent);
                }
                break;

            case R.id.fgt_mine_collectionLayId://跳转收藏界面


                intent.setClass(getActivity(), ActMyCollection.class);
                break;

            case R.id.fgt_mine_youHuiQuanId://我的优惠券

                intent.setClass(getActivity(), ActMyYouHuiQuan.class);

                break;

            case R.id.fgt_mine_myYuEId://我的余额

                intent.setClass(getActivity(), ActMyBalance.class);

                break;

            case R.id.fgt_mine_consignee_yiJianFanKuiId://意见反馈

                intent.setClass(getActivity(), ActSugestionFeedback.class);
                break;

        }
        if (Common.isLogin) {
            startActivity(intent);
        }else {
            new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("请登录")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivityForResult(intent, 100);
                        }
                    }).setIcon(R.mipmap.ic_note).show();
        }


    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        KJLoger.log(TAG, "----initWidget----");
        changeText("我的");
        kjBitmap = new KJBitmap();
        context = getActivity();

        initViwe(parentView);
    }

    private void initViwe(View parentView) {
        isLogin = PreferenceHelper.readBoolean(context, "UserInfo", "isLogin", false);
        menuLayId.setVisibility(View.VISIBLE);
        menuLayId.setText("");
        menuLayId.setBackgroundResource(R.mipmap.message);
        if (!isLogin) {//第一次进入程序
            imageView.setImageResource(R.mipmap.user);
            linearLayout.setVisibility(View.GONE);
            loginOrRegist.setVisibility(View.VISIBLE);
        }else {
            linearLayout.setVisibility(View.VISIBLE);
            loginOrRegist.setVisibility(View.GONE);
            getUserInfo();
            String account = PreferenceHelper.readString(context, "UserInfo", "Account", Common.Usr_PhoneNumber);
            phoneNumberTv.setText(account);

                nameTv.setText("真诚");
            }

    }
    public void getUserInfo() {
        JSONObject object = new JSONObject();
        try {
            object.put("Tel", Common.Usr_PhoneNumber);
            final HttpParams params = new HttpParams();
            params.putJsonParams(object.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "GetPhoto", params,false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);//--头像回调成功--{"resultState":true,"message":"上传成功",
                    // "Photo":"http://182.92.188.3:8048/Image/2016-05-04/20160504110849450072.jpg",
                    // "nickName":null}
                    KJLoger.log(TAG, "--用户信息回调成功--" + t);
                    JSONTokener jsonTokener = new JSONTokener(t);
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        String resultState = jsonObject.getString("resultState");
                        if (resultState.equals("true")) {


                            String photo = jsonObject.getString("Photo");
                            String nickName = jsonObject.getString("nickName");
                            if (photo != null) {
                                //handler.obtainMessage(1,photo).sendToTarget();
                                kjBitmap.display(imageView, photo);
                            }

//                            String account = PreferenceHelper.readString(context, "UserInfo", "Account", Common.Usr_PhoneNumber);
//                            phoneNumberTv.setText(account);
//                            if (nickName.equals("") || nickName == null) {
//                                nameTv.setText(account);
//                            } else {
//                                nameTv.setText(nickName);
//                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KJLoger.log(TAG, "----onActivityResult---resultCode-" + resultCode);
        if (resultCode == 100) {
            String account = PreferenceHelper.readString(context, "UserInfo", "Account", Common.Usr_PhoneNumber);
            String name = PreferenceHelper.readString(context, "UserInfo", "Name", account);
            String niCheng = PreferenceHelper.readString(context, "UserInfo", "niCheng", "");
            if (niCheng.equals("")) {
                nameTv.setText(name);
            } else {
                nameTv.setText(niCheng);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        KJLoger.log(TAG, "--fgtMine--onResume---");
        View parentView = getActivity().getLayoutInflater().inflate(R.layout.fgt_mine, null);
        initWidget(parentView);
        getUserInfo();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String photoUri = (String) msg.obj;
                //kjBitmap.display(imageView, photoUri);
            }
        }
    };
}
