package vmediacn.com.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.utils.KJLoger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.bill.ActWSBillItemDetail;
import vmediacn.com.activity.weijie.ActWSBillPay;
import vmediacn.com.activity.weijie.ActWeiJieShopDetail;
import vmediacn.com.allBean.bill.BillResponse;
import vmediacn.com.allBean.bill.BillResultBody;
import vmediacn.com.ui.pulltorefrush.MyXListView;
import vmediacn.com.util.MyDialog;
import vmediacn.com.util.StringCallBack;

/**
 * A simple {@link Fragment} subclass.
 * 订单
 *
 * 说明：state=1 是待付款；state=2 是等待商家接单,state=3 是商家已接单;state=4 是已收货
 *///String jsn ="单数据成功{\"resultState\":true,\"message\":\"获取成功\",\"resultBody\":[{\"Id\":10,\"state\":1,\"Name\":\"炸鸡等2件商品\",\"Money\":308.00,\"ShopLogo\":\"http://192.168.0.68:8048/Logo/mdl.jpg\",\"ShopName\":\"mdl\",\"time\":\"\\/Date(1460951900268)\\/\"},{\"Id\":11,\"state\":1,\"Name\":\"炸鸡等1件商品\",\"Money\":158.00,\"ShopLogo\":\"http://192.168.0.68:8048/Logo/mdl.jpg\",\"ShopName\":\"mdl\",\"time\":\"\\/Date(1460951931343)\\/\"},{\"Id\":12,\"state\":1,\"Name\":\"炸鸡等2件商品\",\"Money\":308.00,\"ShopLogo\":\"http://192.168.0.68:8048/Logo/mdl.jpg\",\"ShopName\":\"mdl\",\"time\":\"\\/Date(1460963482286)\\/\"}]}\n";

public class FgtBill extends BaseFragment implements MyXListView.IXListViewListener{

    private String TAG = "vmediacn.com.fragment.FgtBill--";

    private MyXListView listView;
    private MyAdapter adapter;
    private List<BillResultBody> list;

    private int pageIndex = 1;
    private boolean isEnd = false;
    public FgtBill() {
        // Required empty public constructor
    }
    private SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
            openItem.setWidth(dp2px(90));
            openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
            openItem.setTitleColor(Color.WHITE);
            openItem.setIcon(R.mipmap.ic_delete);
            menu.addMenuItem(openItem);
        }
    };
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fgt_bill, viewGroup, false);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        changeText("订单");
        MyDialog.showDialog(getActivity());
        list = new ArrayList<>();
        listView = (MyXListView) parentView.findViewById(R.id.fgt_bill_listViewId);
        TextView emptyView = (TextView) parentView.findViewById(R.id.bill_noContentId);
        View headView = getActivity().getLayoutInflater().inflate(R.layout.headview, null);
        listView.addHeaderView(headView);

        listView.setMenuCreator(creator);
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(true);
        listView.setAutoLoadEnable(true);
        listView.setXListViewListener(this);

        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        listView.setEmptyView(emptyView);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int menu_position) {
                switch (menu_position) {
                    case 0://删除订单
                        KJLoger.log(TAG, " --删除的Item--position--" + i);
                        list.remove(i);
                        adapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        //getBillDate(true);
        gettestDate();
        getBillDetail();

    }

    public void getBillDate(final boolean isClear) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", Common.Usr_PhoneNumber);
            jsonObject.put("pageSize", 3);
            jsonObject.put("pageIndex", pageIndex);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Orderlist", params, false, new HttpCallBack() {
                @Override
                public void onSuccess(String json) {
                    super.onSuccess(json);
                    KJLoger.log(TAG, "所有订单数据成功" + json);

                    MyDialog.closeDialog();
                    listView.stopRefresh();
                    listView.stopLoadMore();

                    long currentTime = System.currentTimeMillis();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date = new Date(currentTime);
                    String s = formatter.format(date);
                    KJLoger.log(TAG, " 当前时间是：" + s);
                    listView.setRefreshTime(s);

                    Gson gson = new Gson();
                    BillResponse response = gson.fromJson(json, BillResponse.class);
                    if (response.getResultState().equals("true")) {
                        List<BillResultBody> resultBody = response.getResultBody();

                        if (resultBody == null || resultBody.size() == 0) {
                            isEnd = true;
                            listView.setPullLoadEnable(false);
                        } else {
                            listView.setPullLoadEnable(true);
                            isEnd = false;
                        }
                        if (isClear) {
                            list.clear();
                        }
                        list.addAll(resultBody);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "请求所有订单数据失败" + strMsg);
                    gettestDate();
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        }

    private void gettestDate() {
        KJLoger.log(TAG, "测试数据执行了");
        BillResultBody resultBody = new BillResultBody();
        resultBody.setId("1");
        resultBody.setState("1");
        resultBody.setName("香烟");
        resultBody.setShopName("BHG生活超市");


        BillResultBody resultBody1 = new BillResultBody();
        resultBody.setState("2");
        resultBody1.setId("2");
        resultBody1.setName("啤酒");
        resultBody1.setShopName("汇丰超市");


        BillResultBody resultBody2 = new BillResultBody();
        resultBody1.setId("3");
        resultBody.setState("3");
        resultBody1.setName("饮料");
        resultBody.setShopName("联华超市");

        list.clear();
        list.add(resultBody);
        list.add(resultBody1);
        list.add(resultBody);
        list.add(resultBody2);
        list.add(resultBody1);
        adapter.notifyDataSetChanged();


    }

    public void getBillDetail() {

        JSONObject object = new JSONObject();
        try {
            object.put("id", 10);
            HttpParams params = new HttpParams();
            params.putJsonParams(object.toString());

            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Orderdetails", params, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "订单详情请求" + t);
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
    public void onRefresh() {
        pageIndex = 1;
        //getBillDate(true);
        KJLoger.log(TAG, "刷新 的 pageIndex==" + pageIndex);
    }

    @Override
    public void onLoadMore() {
        if (!isEnd) {
            ++pageIndex;
            //getBillDate(false);
            KJLoger.log(TAG, "加载更多 的 pageIndex" + pageIndex);
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fgt_bill_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.shopNameTv = (TextView) convertView.findViewById(R.id.fgt_bill_listView_item_shopNameId);
                viewHolder.billStateTv = (TextView) convertView.findViewById(R.id.fgt_bill_listView_item_bill_stateId);
                viewHolder.shopPhotoIv = (ImageView) convertView.findViewById(R.id.fgt_bill_listView_item_shopPhotoId);
                viewHolder.shopProductNameTv = (TextView) convertView.findViewById(R.id.fgt_bill_listView_item_shopProductNameId);
                viewHolder.billTimeTv = (TextView) convertView.findViewById(R.id.fgt_bill_listView_item_bill_timeId);
                viewHolder.billMoney = (TextView) convertView.findViewById(R.id.fgt_bill_listView_item_bill_moneyId);
                viewHolder.bill_handle_left = (TextView) convertView.findViewById(R.id.fgt_bill_listView_item_bill_handle_leftId);
                viewHolder.bill_handle_right = (TextView) convertView.findViewById(R.id.fgt_bill_listView_item_bill_handle_rightId);
                viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.fgt_bill_listView_item_bill_handle_shopLay);
                viewHolder.shopLay = (LinearLayout) convertView.findViewById(R.id.fgt_bill_listView_item_shopLayId);
                convertView.setTag(viewHolder);

            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final BillResultBody resultBody = list.get(position);
            final String id = resultBody.getId();
            String name = resultBody.getName();
            final String price = resultBody.getMoney();

            viewHolder.shopProductNameTv.setText(name);
            viewHolder.billMoney.setText(price);
            KJBitmap bitmap = new KJBitmap();
            //bitmap.display(viewHolder.shopPhotoIv, resultBody.getShopLogo());
            viewHolder.shopNameTv.setText(resultBody.getShopName());
            KJLoger.log(TAG, "--state--" + resultBody.getState());
            String strState = resultBody.getState();
            if (strState != null && !strState.isEmpty()) {
                int  state = Integer.parseInt(strState);
                switch (state) {
                    //*说明：state=1是待付款；state=2是等待商家接单,state=3是商家已接单;state=4是已收货*//*
                    case 1://待付款
                        viewHolder.billStateTv.setText("待付款");
                        break;

                    case 2://等待接单
                        viewHolder.billStateTv.setText("等待接单");
                        break;

                    case 3://已接单
                        viewHolder.billStateTv.setText("已接单");
                        break;

                    case 4://已收货
                        viewHolder.billStateTv.setText("已收货");
                        break;

                }
            }



            viewHolder.bill_handle_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KJLoger.log(TAG, "立即支付");
                    //跳转支付详情页面
                    Intent intent = new Intent(getActivity(), ActWSBillPay.class);
                    intent.putExtra("allMoney", price);
                    intent.putExtra("billId", id);
                    startActivity(intent);
                }
            });

            viewHolder.bill_handle_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KJLoger.log(TAG, "取消订单");
                    //执行取消订单
                    requstCancelBill(id,position);

                }
            });

            viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ActWSBillItemDetail.class);
                    intent.putExtra("Id", id);
                    startActivity(intent);
                }
            });
            viewHolder.shopLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ActWeiJieShopDetail.class);
                    intent.putExtra("Id",id);
                    startActivity(intent);
                }
            });
            return convertView;
        }
        private class ViewHolder{

            private TextView shopNameTv,billStateTv;
            private ImageView shopPhotoIv;
            private RelativeLayout relativeLayout;
            private LinearLayout shopLay;
            private TextView shopProductNameTv,billTimeTv,billMoney,bill_handle_left,bill_handle_right;
        }
    }

    private void requstCancelBill(String id, final int position) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", id);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Edit_Order", params, new StringCallBack(getActivity()) {
                @Override
                public void onSuccess(String json) {
                    KJLoger.log(TAG, "--取消订单请求数据---" + json);

                    JSONTokener jsonTokener = new JSONTokener(json);
                    try {
                        JSONObject jsonObject1 = (JSONObject) jsonTokener.nextValue();
                        String message = jsonObject1.getString("message");
                        if (jsonObject1.getString("resultState").equals("true")) {

                            list.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        showToast(getActivity(), message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
