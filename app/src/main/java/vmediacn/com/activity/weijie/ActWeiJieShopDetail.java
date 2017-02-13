package vmediacn.com.activity.weijie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.KJFragment;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.List;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.activity.load.LoginActivity;
import vmediacn.com.allBean.weijie.WSShopDetailRespose;
import vmediacn.com.allBean.weijie.WSShopDetilResultBody;
import vmediacn.com.fragment.find.FindFgtShopDetailJudge;
import vmediacn.com.fragment.find.FindFgtShopDetailProduct;
import vmediacn.com.fragment.find.FindFgtShopDetailXiangQ;
/*
* 店铺详情
* */
public class ActWeiJieShopDetail extends BaseActivity {
    private String TAG = "vmediacn.com.activity.weijie.ActWeiJieProductDetail";
    private String id;
    private TabLayout tableLayout;
    private KJFragment fragment ;

    @BindView(id = R.id.act_shop_detail_backImagId, click = true)
    private ImageView backImg;
    //商店名字
    @BindView(id = R.id.act_shop_detail_shopTitleId, click = true)
    private TextView shopTitleTv;
    //起送价
    @BindView(id = R.id.act_shop_detail_shopQiSongJiaId, click = true)
    private TextView qiSongJiaTv;

    //配送费
    @BindView(id = R.id.act_shop_detail_shopFrightId, click = true)
    private TextView peiSongFeiTv;

    //配送费
    @BindView(id = R.id.act_shop_detail_shopPruductCountId, click = true)
    private TextView productCountTv;

    //月售
    @BindView(id = R.id.act_shop_detail_shopYueShouId, click = true)
    private TextView yueShouTv;

     //月售
    @BindView(id = R.id.act_shop_detail_timeId, click = true)
    private TextView timeTv;

    //shopLogo
    @BindView(id = R.id.act_shop_detail_shopPhotoId, click = true)
    private ImageView shopLogoIv;

    @BindView(id = R.id.act_shop_detail_collectId, click = true)//收藏
    private ImageView collectIv;

    @BindView(id = R.id.act_shop_detail_frameLayoutId, click = true)//收藏
    private FrameLayout frameLayout;

    @BindView(id = R.id.topLayId, click = true)//收藏
    private LinearLayout topLinearLayout;


    private String location;
    private String shopId;


    @Override
    public void setRootView() {

        setContentView(R.layout.act_shop_detail);

    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.act_shop_detail_backImagId://f返回按钮
                onBackPressed();
                break;

            case R.id.act_shop_detail_collectId://收藏
                if (!Common.isLogin) {
                    new AlertDialog.Builder(ActWeiJieShopDetail.this).setTitle("提示").setMessage("请登录")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ActWeiJieShopDetail.this, LoginActivity.class);
                                    startActivityForResult(intent, 100);
                                }
                            }).setIcon(R.mipmap.ic_note).show();
                } else {
                    requstCollect();
                }


                break;
            default:
                break;
        }
    }

    private void requstCollect() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", shopId);
            jsonObject.put("Tel", Common.Usr_PhoneNumber);
            jsonObject.put("state", 1);
            KJHttp http = new KJHttp();
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            http.jsonPost(Common.MSUri + "Collection", params, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    //{"resultState":true,"message":"收藏成功"}
                    KJLoger.log(TAG, "--收藏返回数据--" + t);
                    JSONTokener tokener = new JSONTokener(t);
                    try {
                        JSONObject object = (JSONObject) tokener.nextValue();
                        String message = object.getString("message");
                        if (object.getString("resultState").equals("true")) {
                            ShowToast(ActWeiJieShopDetail.this, message);

                        } else {
                            ShowToast(ActWeiJieShopDetail.this, message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "--收藏请求失败--" + strMsg);

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("Id");
            KJLoger.log(TAG,"--id--");
        }
        getProductDate();
        tableLayout = (TabLayout) findViewById(R.id.act_shop_detail_tabLayoutId);
        TabLayout.Tab tab1 = tableLayout.newTab();


        TabLayout.Tab tab2 = tableLayout.newTab();
        TabLayout.Tab tab3 = tableLayout.newTab();
        tab1.setText("商品");
        tab2.setText("评价");
        tab3.setText("详情");
        tableLayout.addTab(tab1, true);
        tableLayout.addTab(tab2);
        tableLayout.addTab(tab3);
    }

    @Override
    public void initWidget() {
        super.initWidget();


        tableLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                KJLoger.log(TAG, "tab_position--" + tab.getPosition());
                fragment = null;
                switch (tab.getPosition()) {
                    case 0://商品
                        fragment = new FindFgtShopDetailProduct();
                        Bundle bundle = new Bundle();
                        bundle.putString("shopId", id);
                        bundle.putInt("top", frameLayout.getTop());
                        KJLoger.log(TAG, "frameLayout.getTop()--" + frameLayout.getTop());
                        fragment.setArguments(bundle);
                        break;

                    case 1://评价
                        fragment = new FindFgtShopDetailJudge();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("shopId", id);
                        fragment.setArguments(bundle1);
                        break;

                    case 2://详情
                        fragment = new FindFgtShopDetailXiangQ();
                        break;
                }
                if (fragment != null) {

                    changeFragment(R.id.act_shop_detail_frameLayoutId, fragment);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void getProductDate() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id",id);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Shopsdetails", params, false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "--店铺详情请求成功" + t);
                    setJson(t);
                    Gson gson = new Gson();
                    WSShopDetailRespose respose = gson.fromJson(t, WSShopDetailRespose.class);
                    if (respose.getResultState().equals("true")) {
                        List<WSShopDetilResultBody> resultBodyList = respose.getResultBody();
                        if (resultBodyList != null && resultBodyList.size() > 0) {
                            WSShopDetilResultBody resultBody = resultBodyList.get(0);
                            String shopId = resultBody.getId();
                            setShopId(shopId);
                            String qisong = resultBody.getQisong();
                            String fright = resultBody.getFright();
                            String grade = resultBody.getGrade();
                            location = resultBody.getLocation();
                            String logo = resultBody.getLogo();
                            String time = resultBody.getTime();
                            String sales = resultBody.getSales();
                            String number = resultBody.getNumber();
                            String name = resultBody.getName();
                            shopTitleTv.setText(name);
                            productCountTv.setText(number+"件商品");
                            yueShouTv.setText("月售"+sales+"件");
                            timeTv.setText("约"+time+"分钟");
                            KJBitmap bitmap = new KJBitmap();
                            bitmap.display(shopLogoIv, logo);
                            peiSongFeiTv.setText("配送费" + fright + "元");
                            qiSongJiaTv.setText("起送"+qisong+"元");

                            Bundle bundle = new Bundle();
                            bundle.putString("shopId", id);
                            bundle.putInt("top", frameLayout.getTop());
                            KJLoger.log(TAG, "frameLayout.getTop()0--" + frameLayout.getTop());

                            fragment = new FindFgtShopDetailProduct();
                            fragment.setArguments(bundle);
                            changeFragment(R.id.act_shop_detail_frameLayoutId, fragment);
                        }
                    }

                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "--店铺详情请求失败" + strMsg);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setJson(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    private String json;

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopId() {
        return id;
    }

}
