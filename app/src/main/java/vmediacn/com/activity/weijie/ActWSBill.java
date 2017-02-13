package vmediacn.com.activity.weijie;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.activity.mine.ActConsigneeAddress;
import vmediacn.com.activity.mine.AddAddress;
import vmediacn.com.allBean.find.CartInfo;
import vmediacn.com.allBean.mine.MineAddrResponse;
import vmediacn.com.allBean.mine.MineAddrResultBody;
import vmediacn.com.allBean.weijie.WSShopDetailRespose;
import vmediacn.com.allBean.weijie.WSShopDetilResultBody;
import vmediacn.com.util.CartDBManager;
/*付款订单
* */
//{"resultState":true,"message":"成功","resultBody":[{"id":97,
// "goodid":1,"name":"炸鸡","pic":"http://192.168.0.68:8048/Logo/炸鸡.jpg","number":10,"price":15.00,
// "Attribute_value":"家用"},
// {"id":98,"goodid":2,"name":"汉堡","pic":"http://192.168.0.68:8048/Logo/炸鸡.jpg",
// "number":1,"price":20.00,"Attribute_value":""},{"id":99,"goodid":1,"name":"炸鸡","pic":"http://192.168.0.68:8048/Logo/炸鸡.jpg","number":10,"price":15.00,"Attribute_value":"家用"},{"id":125,"goodid":1,"name":"炸鸡","pic":"http://192.168.0.68:8048/Logo/炸鸡.jpg","number":10,"price":15.00,"Attribute_value":"家用"},{"id":126,"goodid":1,"name":"炸鸡","pic":"http://192.168.0.68:8048/Logo/炸鸡.jpg","number":10,"price":15.00,"Attribute_value":"家用"},{"id":127,"goodid":1,"name":"炸鸡","pic":"http://192.168.0.68:8048/Logo/炸鸡.jpg","number":10,"price":15.00,"Attribute_value":"家用"},{"id":177,"goodid":1,"name":"炸鸡","pic":"http://192.168.0.68:8048/Logo/炸鸡.jpg","number":10,"price":15.00,"Attribute_value":"家用"},{"id":178,"goodid":1,"name":"炸鸡","pic":"http://192.168.0.68:8048/Logo/炸鸡.jpg","number":10,"price":15.00,"Attribute_value":"家用"},{"id":179,"goodid":1,"name":"炸鸡","pic":"http://192.168.0.68:8048/Logo/炸鸡.jpg","number":10,"price":15.00,"Attribute_value":"家用"},{"id":180,"goodid":1,"name":"炸鸡","pic":"http://192.168.0.68:8048/Logo/炸鸡.jpg","number":10,"price":15.00,"Attribute_value":"家用"}]}

public class ActWSBill extends BaseActivity {
    private String TAG = "vmediacn.com.activity.weijie.ActWSBill--";
    @BindView(id = R.id.act_wsbill_hasnot_shouhuodizhiLayoutId, click = true)//无收获地址
    private LinearLayout linearLayout;

    @BindView(id = R.id.act_wsbill_has_shouhuodizhiLayoutId1,click = true)//有收获地址
    private LinearLayout linearLayout1;

    @BindView(id = R.id.act_wsbill_paybtnId, click = true)//支付
    private Button payBtn;

    @BindView(id = R.id.act_add_address_item_nameId)//姓名
    private TextView nameTv;

    @BindView(id = R.id.act_add_address_item_phoneId)//手机号
    private TextView phoneNumberTv;

    @BindView(id = R.id.act_add_address_item_addressId, click = true)//地址
    private TextView addressTv;

    @BindView(id = R.id.wsbill_shopNameId)//店铺名称
    private TextView shopTitleTv;

    @BindView(id = R.id.act_wsbill_moneyId)//allMoney
    private TextView allMoneyTv;

    @BindView(id = R.id.wsbill_peiSongFeiId, click = true)//配送费
    private TextView peiSongTv;

    private CartAdapter cartAdapter = new CartAdapter();
    @BindView(id = R.id.wsbill_listViewId)//ListView
    private ListView billListView;

    private ArrayList<String> goodIdList = new ArrayList<>();
    private ArrayList<String> numList = new ArrayList<>();

    private List<CartInfo> cartBodyList = new ArrayList<CartInfo>();
    private String addressId;
    private String shopId;
    private CartDBManager dbManager;
    private String fright;


    @Override
    public void setRootView() {

        setContentView(R.layout.activity_wsbill);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeText("订单");
        dbManager = new CartDBManager(ActWSBill.this);
        Intent intent = getIntent();
        if (intent != null) {
            shopId = intent.getStringExtra("shopId");
            KJLoger.log(TAG, "--shopId--" + shopId);
        }

        billListView.setAdapter(cartAdapter);

        getGoodAndNumList();
        getShopDetailDate();

        judgeAddress();



    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.act_wsbill_paybtnId:
                KJLoger.log(TAG, "--支付点击了--");
                if (addressId==null||addressId.equals("")) {
                    ShowToast(ActWSBill.this, "请添加收货地址");
                } else {
                    submitBill(addressId);
                }

                break;
            case R.id.act_wsbill_has_shouhuodizhiLayoutId1://有收货地址
                if (linearLayout1.getVisibility() == View.VISIBLE) {
                    Intent intent1 = new Intent(ActWSBill.this, ActConsigneeAddress.class);
                    intent1.putExtra("isChooseAddress", true);
                    startActivityForResult(intent1, 100);
                }
                break;

            case R.id.act_wsbill_hasnot_shouhuodizhiLayoutId://无收货地址
                if (linearLayout.getVisibility() == View.VISIBLE) {//跳转添加地址页面
                    Intent intent2 = new Intent(ActWSBill.this, AddAddress.class);
                    intent2.putExtra("type", "添加收货地址");
                    startActivity(intent2);
                }
                break;

            default:
                break;
        }
    }
    /*
    @params
    * goodId:商品Id
    * num：数量
    * addressId：地址Id
    * */
    private void submitBill(String addressId) {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", toJson(goodIdList, numList));
            jsonObject.put("addid", addressId);
            KJLoger.log(TAG, "--addId--" + addressId);
            jsonObject.put("Tel", Common.Usr_PhoneNumber);
            KJLoger.log(TAG, "--Usr_PhoneNumber--" + Common.Usr_PhoneNumber);

            HttpParams params = new HttpParams();
            KJLoger.log(TAG, "--json--" + jsonObject.toString());
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "immediately", params, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "生成订单请求成功了" + t);
                    JSONTokener tokener = new JSONTokener(t);
                    try {
                        JSONObject jsonObject1 = (JSONObject) tokener.nextValue();
                        String resultState = jsonObject1.getString("resultState");
                        if (resultState.equals("true")) {
                            //跳转支付页面
                            String billId = jsonObject1.getString("resultBody");
                            Intent intent = new Intent(ActWSBill.this, ActWSBillPay.class);
                            intent.putExtra("billId", billId);
                            intent.putExtra("allMoney", allMoneyTv.getText().toString());
                            startActivity(intent);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "生成订单请求失败" + strMsg);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private String toJson(List<String> goodIdList,List<String> numlList) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < goodIdList.size(); i++) {
            JSONObject object = new JSONObject();
            try {
                object.put("id", goodIdList.get(i));
                object.put("count", numlList.get(i));
                array.put(i, object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        KJLoger.log(TAG, "--toJson--" + array.toString());
        return array.toString();
    }

    //获取商品和数量的集合
    private void getGoodAndNumList() {
        List<CartInfo> cartInfos = dbManager.query(shopId);
        goodIdList.clear();
        numList.clear();
        for (int i = 0; i < cartInfos.size(); i++) {
            CartInfo cartInfo = cartInfos.get(i);
            goodIdList.add(cartInfo.goodId);
            numList.add(cartInfo.num + "");
            KJLoger.log(TAG, "--cartInfo.goodId--"+cartInfo.goodId+"--cartInfo.num--"+cartInfo.num);
        }
    }
    public void getShopDetailDate() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", shopId);
            KJLoger.log(TAG, "--shopId--"+shopId);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Shopsdetails", params, false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "--店铺详情请求成功" + t);
                    Gson gson = new Gson();
                    WSShopDetailRespose respose = gson.fromJson(t, WSShopDetailRespose.class);
                    if (respose.getResultState().equals("true")) {
                        List<WSShopDetilResultBody> resultBodyList = respose.getResultBody();
                        if (resultBodyList != null && resultBodyList.size() > 0) {
                            WSShopDetilResultBody resultBody = resultBodyList.get(0);
                            //String shopId = resultBody.getId();
                            fright = resultBody.getFright();
                            String name = resultBody.getName();
                            shopTitleTv.setText(name);
                            peiSongTv.setText(fright + "元");
                            getShoppingCartDate(shopId);
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
    //购物车商品回调
    private void getShoppingCartDate(String shopId) {
        List<CartInfo> cartInfos = dbManager.query(shopId);
        if (cartInfos != null && cartInfos.size() > 0) {
            KJLoger.log(TAG, "--购物车数量--" + cartInfos.size());
            cartBodyList.clear();
            cartBodyList.addAll(cartInfos);
            setAllMoney(cartBodyList);
        } else {
            KJLoger.log(TAG, "--购物车没有商品--");
            cartBodyList.clear();
        }
        cartAdapter.notifyDataSetChanged();
    }
    private void judgeAddress() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name",Common.Usr_PhoneNumber);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "address", params, false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "--请求收货地址数据成功"+t);
                    JSONTokener tokener = new JSONTokener(t);
                    try {
                        JSONObject jsonObject1 = (JSONObject) tokener.nextValue();
                        String resultState = jsonObject1.getString("resultState");
                        String message = jsonObject1.getString("message");
                        if (resultState.equals("true")) {
                            KJLoger.log(TAG,"resultState=="+resultState);
                            ShowToast(ActWSBill.this, message);
                            MineAddrResponse response = new Gson().fromJson(t, MineAddrResponse.class);
                            List<MineAddrResultBody> resultBodyList = response.getResultBody();

                            if (resultBodyList !=null&&resultBodyList.size()>0) {
                                KJLoger.log(TAG, "resultBodyList.size==" + resultBodyList.size());

                                linearLayout1.setVisibility(View.VISIBLE);//地址详情
                                linearLayout.setVisibility(View.GONE);

                                MineAddrResultBody resultBody = resultBodyList.get(0);
                                addressId = resultBody.getId();
                                nameTv.setText(resultBody.getName());
                                phoneNumberTv.setText(resultBody.getMobile());
                                addressTv.setText(resultBody.getAddress1());

                            } else {
                                linearLayout1.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                        }else {
                            linearLayout1.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "--请求数据失敗");
                    linearLayout1.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void setAllMoney(List<CartInfo> list){
        double allMoney =0.0;
        for (int i = 0; i < list.size(); i++) {
            CartInfo cartBody = list.get(i);
            double price= cartBody.price;
            int number = cartBody.num;
            allMoney = allMoney +price*number;
        }
        allMoney = allMoney+Double.parseDouble(fright);
        allMoneyTv.setText(allMoney + "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {//接收地址回传信息

            case 100:

                String name = data.getStringExtra("name");
                String phone = data.getStringExtra("phone");
                String address = data.getStringExtra("address");
                addressId = data.getStringExtra("addressId");
                nameTv.setText(name);
                phoneNumberTv.setText(phone);
                addressTv.setText(address);

                break;

        }
    }
    private class CartAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return cartBodyList.size();
        }

        @Override
        public Object getItem(int position) {
            return cartBodyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CartHolder cartHolder = null;
            if (convertView == null) {
                cartHolder = new CartHolder();
                convertView = getLayoutInflater().inflate(R.layout.wsbill_item_lay, null);
                cartHolder.countTv = (TextView) convertView.findViewById(R.id.wsbill_item_lay_countId);
                cartHolder.nameTv = (TextView) convertView.findViewById(R.id.wsbill_item_lay_nameId);
                cartHolder.moneyTv = (TextView) convertView.findViewById(R.id.wsbill_item_lay_allMoneyId);
                convertView.setTag(cartHolder);
            } else {
                cartHolder = (CartHolder) convertView.getTag();
            }
            CartInfo cartInfo = cartBodyList.get(position);
            String name = cartInfo.goodName;
            int number = cartInfo.num;
            double price = cartInfo.price;
            cartHolder.nameTv.setText(name);
            cartHolder.countTv.setText("x"+number);
            double allMoney = price * number;
            cartHolder.moneyTv.setText(allMoney+"");
            return convertView;
        }
        private class CartHolder{
            private TextView nameTv,countTv,moneyTv;
        }
    }
}
