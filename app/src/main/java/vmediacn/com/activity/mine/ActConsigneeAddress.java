package vmediacn.com.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.allBean.mine.MineAddrResponse;
import vmediacn.com.allBean.mine.MineAddrResultBody;

/*
收货地址
* */
public class ActConsigneeAddress extends BaseActivity {

    private String TAG = "vmediacn.com.activity.mine.ActConsigneeAddress---";
    private SwipeMenuListView listView;
    private List<MineAddrResultBody> resultBodyList;
    private MyAdapter adapter;
    private SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {

            SwipeMenuItem openItem = new SwipeMenuItem(ActConsigneeAddress.this);

            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));

            openItem.setWidth(dp2px(90));

            openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));

            openItem.setTitleColor(Color.WHITE);

            openItem.setIcon(R.mipmap.ic_delete);

            menu.addMenuItem(openItem);
        }
    };
    private boolean isChooseAddress;

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
    @Override
    public void setRootView() {
        setContentView(R.layout.act_consignee_address);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeText("收货地址");
    }

    @Override
    public void initData() {
        super.initData();
        final Intent intent = getIntent();
        if (intent != null) {
            isChooseAddress = intent.getBooleanExtra("isChooseAddress",false);
        }
        listView = (SwipeMenuListView) findViewById(R.id.act_mine_consignee_address_listViewId);
        View footView = getLayoutInflater().inflate(R.layout.add_consignee_address_lay, null);

        listView.addFooterView(footView);
        resultBodyList = new ArrayList<>();
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setMenuCreator(creator);
        getData();

        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KJLoger.log(TAG, "--footView点击了--");
                Intent intent = new Intent(ActConsigneeAddress.this, AddAddress.class);
                intent.putExtra("type", "添加收货地址");
                startActivityForResult(intent, 100);
            }
        });
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int menu_position) {//侧滑菜单点击事件
                switch (menu_position) {
                    case 0://删除地址
                        String id = resultBodyList.get(i).getId();

                        deleAddress(id, i);


                        break;
                }
                return false;
            }

        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//item的点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isChooseAddress) {//是选择地址
                    MineAddrResultBody resultBody = resultBodyList.get(position);
                    Intent intent1 = new Intent();
                    intent1.putExtra("name", resultBody.getName());
                    intent1.putExtra("phone", resultBody.getMobile());
                    intent1.putExtra("address", resultBody.getAddress1());
                    intent1.putExtra("addressId", resultBody.getId());
                    setResult(100, intent1);
                    onBackPressed();
                }
            }
        });
    }

    private void deleAddress(String id, final int position) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            HttpParams httpParams = new HttpParams();
            httpParams.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Deladdress", httpParams, false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "--请求删除数据成功" + t);
                    JSONTokener tokener = new JSONTokener(t);
                    try {

                        JSONObject jsonObject1 = (JSONObject) tokener.nextValue();
                        String resultState = jsonObject1.getString("resultState");
                        String message = jsonObject1.getString("message");
                        if (resultState.equals("true")) {
                            resultBodyList.remove(position);
                            adapter.notifyDataSetChanged();
                        } else {
                            ShowToast(ActConsigneeAddress.this,message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "--请求删除数据失败" + strMsg);
                        ShowToast(ActConsigneeAddress.this,"删除数据失败");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getData() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", Common.Usr_PhoneNumber);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "address", params, false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "地址请求成功" + t);
                    JSONTokener jsonTokener = new JSONTokener(t);
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = (JSONObject) jsonTokener.nextValue();
                        String resultState = jsonObject1.getString("resultState");
                        String message = jsonObject1.getString("message");
                        if (resultState.equals("true")) {
                            MineAddrResponse response = new Gson().fromJson(t, MineAddrResponse.class);
                            List<MineAddrResultBody> resultBody2 = response.getResultBody();
                            resultBodyList.clear();
                            resultBodyList.addAll(resultBody2);
                            adapter.notifyDataSetChanged();
                        }else {
                            getTest();
                            ShowToast(ActConsigneeAddress.this, message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    getTest();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTest() {
        MineAddrResultBody resultBody = new MineAddrResultBody();
        resultBody.setAddress1("东村国际创意基地");
        resultBody.setName("詹姆斯");
        resultBody.setId("1");
        resultBody.setIs_default("0");
        resultBody.setStreet("北京维圣传媒");
        resultBody.setMobile("13717899174");

        MineAddrResultBody resultBody1 = new MineAddrResultBody();
        resultBody1.setAddress1("青青家园");
        resultBody1.setName("科比");
        resultBody1.setId("2");
        resultBody1.setIs_default("0");
        resultBody1.setStreet("北京维圣传媒");
        resultBody1.setMobile("13717899174");


        MineAddrResultBody resultBody3 = new MineAddrResultBody();
        resultBody3.setAddress1("青青家园");
        resultBody3.setName("韦德");
        resultBody3.setId("3");
        resultBody3.setIs_default("0");
        resultBody3.setStreet("北京维圣传媒");
        resultBody3.setMobile("13717899174");

        resultBodyList.add(resultBody);
        resultBodyList.add(resultBody1);
        resultBodyList.add(resultBody3);
        adapter.notifyDataSetChanged();
        //
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return resultBodyList.size();
        }

        @Override
        public Object getItem(int position) {
            return resultBodyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.add_consignee_address_item, null);
                viewHolder = new ViewHolder();
                viewHolder.addressTv = (TextView) convertView.findViewById(R.id.act_add_address_item_addressId);
                viewHolder.phoneTv = (TextView) convertView.findViewById(R.id.act_add_address_item_phoneId);
                viewHolder.nameTv = (TextView) convertView.findViewById(R.id.act_add_address_item_nameId);
                viewHolder.editLayout = (LinearLayout) convertView.findViewById(R.id.act_add_address_item_editaddressId);
                convertView.setTag(viewHolder);

            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final MineAddrResultBody resultBody = resultBodyList.get(position);

            viewHolder.addressTv.setText(resultBody.getStreet());
            viewHolder.nameTv.setText(resultBody.getName()+"");
            viewHolder.phoneTv.setText(resultBody.getMobile());
            viewHolder.editLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//编辑收货地址

                    String mobile = resultBody.getMobile();
                    String name = resultBody.getName();
                    String street = resultBody.getStreet();
                    String id = resultBody.getId();

                    Intent intent = new Intent(ActConsigneeAddress.this, AddAddress.class);
                    intent.putExtra("mobile", mobile);
                    intent.putExtra("name", name);
                    intent.putExtra("street", street);
                    intent.putExtra("type", "编辑收货地址");
                    intent.putExtra("id", id);

                    startActivityForResult(intent, 100);
                }
            });
            return convertView;
        }
        class ViewHolder{
            TextView nameTv;
            TextView phoneTv;
            TextView addressTv;
            LinearLayout editLayout;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            getData();
        }
    }
}
