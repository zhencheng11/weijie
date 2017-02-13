package vmediacn.com.activity.bill;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.List;

import so.orion.slidebar.GBSlideBar;
import so.orion.slidebar.GBSlideBarAdapter;
import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.activity.weijie.ActWSBillPay;
import vmediacn.com.allBean.bill.BillDetailItemRespose;
import vmediacn.com.allBean.bill.BillGoodsBody;
import vmediacn.com.allBean.bill.BillShopsBody;
import vmediacn.com.util.StringCallBack;
/*订单详情页
* state=1是待付款；state=2是等待商家接单,state=3是商家已接单;state=4是已收货
* */
public class ActWSBillItemDetail extends BaseActivity {

    @BindView(id = R.id.act_wsbill_detil_lay_cancel_billId,click = true)
    private TextView cancleTv;

    @BindView(id = R.id.act_wsbill_detil_lay_pay_billId,click = true)
    private TextView payTv;

    @BindView(id = R.id.bill_detail_stateId)//订单状态
    private TextView billStateTv;

    @BindView(id = R.id.bill_detail_timeId)//订单时间
    private TextView billTimeTv;

    @BindView(id = R.id.bill_detail_allMoney1Id)//订单总价1
    private TextView billAllMoney1Tv;

    @BindView(id = R.id.bill_detail_allMoney2Id)//订单总价1
    private TextView billAllMoney2Tv;

    @BindView(id = R.id.bill_detail_shengYuTime1Id)//订单剩余时间
    private TextView billShengYuTimeTv;

    @BindView(id = R.id.bill_detail_shopNameId)//商店名字
    private TextView shopNameTv;

    @BindView(id = R.id.bill_detail_frightId)//配送费
    private TextView frightTv;

    @BindView(id = R.id.bill_detail_addressId)//收货地址
    private TextView addressTv;

    @BindView(id = R.id.bill_detail_telePhoneId)//手机号：收货人
    private TextView telephoneTv;

    @BindView(id = R.id.bill_detail_shouHuoNameId)//收货人姓名
    private TextView shouHuoNameTv;

    @BindView(id = R.id.bill_detail_bill_codeId)//订单号
    private TextView billCodeTv;

    @BindView(id = R.id.bill_detail_bill_timeId)//订单时间
    private TextView detail_billTimeTv;

    @BindView(id = R.id.bill_detail_goodsListViewId)//订单listView
    private ListView listView;
    private CartAdapter adapter = new CartAdapter();

    private List<BillGoodsBody> goodsList = new ArrayList<BillGoodsBody>();
    private String TAG = "vmediacn.com.activity.bill.ActWSBillItemDetail--";
    private GBSlideBar gbSlideBar;
    private SlideAdapter mAdapter;
    private String id;

    @Override
    public void setRootView() {
        setContentView(R.layout.act_wsbill_detail);

    }

    @Override
    public void initData() {
        super.initData();

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("Id");
            getBillDetail(id);

        }
        initGBSBar();
    }

    private void initGBSBar() {

        gbSlideBar = (GBSlideBar) findViewById(R.id.gbslidebar);
        Resources resources = getResources();
        mAdapter = new SlideAdapter(resources, new int[]{
                R.drawable.btn_tag_selector,
                R.drawable.btn_tag_selector,
                R.drawable.btn_tag_selector,
                R.drawable.btn_tag_selector});

        mAdapter.setTextColor(new int[]{
                Color.GREEN,
                Color.BLUE,
                Color.RED,
                Color.BLACK
        });

        KJLoger.log(TAG, mAdapter.getCount() + "");


        gbSlideBar.setAdapter(mAdapter);

        gbSlideBar.setPosition(0);
        gbSlideBar.setClickable(false);
        gbSlideBar.setHorizontalScrollBarEnabled(false);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lingLay);
        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeText("订单详情");
        listView.setAdapter(adapter);

    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.act_wsbill_detil_lay_cancel_billId://取消订单
                break;

            case R.id.act_wsbill_detil_lay_pay_billId://立即支付

                Intent intent = new Intent(ActWSBillItemDetail.this, ActWSBillPay.class);
                intent.putExtra("allMoney", billAllMoney1Tv.getText().toString());
                intent.putExtra("billId", id);

                startActivity(intent);
                break;

        }
    }
    public void getBillDetail(String id) {

        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            HttpParams params = new HttpParams();
            params.putJsonParams(object.toString());

            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Orderdetails", params, new StringCallBack(ActWSBillItemDetail.this) {
                @Override
                public void onSuccess(String json) {
                    KJLoger.log(TAG, "订单详情请求成功" + json);
                    Gson gson = new Gson();
                    BillDetailItemRespose respose = gson.fromJson(json, BillDetailItemRespose.class);
                    if (respose.getResultState().equals("true")) {
                        List<BillShopsBody> shops = respose.getShops();
                        double dfright = 0;
                        if (shops.size() > 0) {
                            BillShopsBody shopsBody = shops.get(0);

                            String address = shopsBody.getAddress();
                            String code = shopsBody.getCode();
                            String fright = shopsBody.getFright();
                            dfright = Double.parseDouble(fright);
                            String shopName = shopsBody.getShopName();
                            String shouHuoName = shopsBody.getName();
                            String tel = shopsBody.getTel();
                            String yh = shopsBody.getYh();
                            String time = shopsBody.getTime();

                            KJLoger.log(TAG, "--address--" + address + "--code--" + code + "\n"+"--fright--" + fright + "--shopName--" + shopName);
                            KJLoger.log(TAG, "--tel--" + tel + "--yh--" + yh + "\n"+"--time--" + time );

                            frightTv.setText(fright);
                            shopNameTv.setText(shopName);
                            addressTv.setText(address);
                            shouHuoNameTv.setText(shouHuoName);
                            telephoneTv.setText(tel);
                            billCodeTv.setText(code);
                            detail_billTimeTv.setText(time);

                        }

                        double allMoney = 0;
                        List<BillGoodsBody> goods = respose.getGoods();
                        for (int i = 0; i < goods.size(); i++) {
                            BillGoodsBody goodBody = goods.get(i);
                            String money = goodBody.getMoney();
                            double money0 = Double.parseDouble(money);
                            allMoney = allMoney+money0;

                        }
                        allMoney = allMoney +dfright;
                        KJLoger.log(TAG, "--allMoney--" + allMoney);
                        billAllMoney2Tv.setText("总计：" + allMoney + "元");
                        billAllMoney1Tv.setText(allMoney+"元");
                        goodsList.clear();
                        goodsList.addAll(goods);
                        adapter.notifyDataSetChanged();


                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private class CartAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return goodsList.size();
        }

        @Override
        public Object getItem(int position) {
            return goodsList.get(position);
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
            BillGoodsBody resultBody = goodsList.get(position);
            String name = resultBody.getName();
            String number = resultBody.getCount();
            String price = resultBody.getMoney();
            cartHolder.nameTv.setText(name);
            cartHolder.countTv.setText("x"+number);
            double allMoney = Double.parseDouble(price) * Integer.parseInt(number);
            cartHolder.moneyTv.setText(price+"");
            return convertView;
        }
        private class CartHolder{

            private TextView nameTv,countTv,moneyTv;
        }
    }
    public class SlideAdapter implements GBSlideBarAdapter {


        protected StateListDrawable[] mItems;
        protected String[] content = new String[]{"订单提交","商家接单","配送中","已完成"};
        protected int[] textColor;

        public SlideAdapter(Resources resources, int[] items) {
            int size = items.length;
            mItems = new StateListDrawable[size];
            Drawable drawable;
            for (int i = 0; i < size; i++) {
                drawable = resources.getDrawable(items[i]);
                if (drawable instanceof StateListDrawable) {
                    mItems[i] = (StateListDrawable) drawable;
                } else {
                    mItems[i] = new StateListDrawable();
                    mItems[i].addState(new int[] {}, drawable);
                }
            }
        }

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public String getText(int position) {
            return content[position];
        }

        @Override
        public StateListDrawable getItem(int position) {
            return mItems[position];
        }

        @Override
        public int getTextColor(int position) {
            return textColor[position];
        }

        public void setTextColor(int[] color){
            textColor = color;
        }
    }

}
