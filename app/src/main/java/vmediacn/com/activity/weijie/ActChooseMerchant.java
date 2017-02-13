package vmediacn.com.activity.weijie;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.allBean.find.CartInfo;
import vmediacn.com.allBean.weijie.WSNearProductBody;
import vmediacn.com.util.CartDBManager;

/*选择商家
* */
public class ActChooseMerchant extends BaseActivity {

    private  List<WSNearProductBody> resultBodyList = new ArrayList<>();
    private ListView listview;
    private MyAdapter adapter;
    private static final int NUM_ADD = 100;
    private static final int NUM_DEL = 101;
    private static final int ADDTO_CART = 102;
    private String TAG = "vmediacn.com.activity.weijie.ActChooseMerchant--";
    private boolean isture =true;
    private Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            View view = listview.getChildAt(msg.arg1-listview.getFirstVisiblePosition());
            WSNearProductBody bean = null;
            TextView num_Tv = (TextView) view.findViewById(R.id.wschoose_merchant_item_productcountId);
            ImageButton deleBtn = (ImageButton) view.findViewById(R.id.wschoose_merchant_item_deleteBtnId);

            switch (msg.what) {
                case ADDTO_CART://加入购物车
                    bean = resultBodyList.get(msg.arg1);
                    CartInfo cartInfo = new CartInfo(bean.getId(),
                            bean.getId(),bean.getName(),Double.parseDouble(bean.getPrice()),1);
                    boolean succsess = dbManager.addToCart(cartInfo);
                    KJLoger.log(TAG, "--succsess--" + succsess);
                    if (succsess) {
                        num_Tv.setVisibility(View.VISIBLE);
                        num_Tv.setText("1");
                        deleBtn.setVisibility(View.VISIBLE);
                    }
                    break;

                case NUM_ADD://数量加
                    bean = resultBodyList.get(msg.arg1);
                    int num = Integer.parseInt(num_Tv.getText().toString());
                    KJLoger.log(TAG, "--num--" + num);
                    dbManager.updateCartNum(bean.getId(), bean.getId(), num + 1);
                    num = num + 1;
                    num = num > 99 ? 99 : num;
                    num_Tv.setText(num + "");
                    break;

                case NUM_DEL://数量减
                    bean = resultBodyList.get(msg.arg1);
                    int num1 = Integer.parseInt(num_Tv.getText().toString());
                    KJLoger.log(TAG, "--num1--" + num1);
                    if (num1 > 1) {
                        dbManager.updateCartNum(bean.getId(),bean.getId(),num1-1);
                        num1 = num1 - 1;
                        num1 = num1 > 1 ? num1 : 1;
                        num_Tv.setText(num1 + "");
                    }
                    break;
            }
        }
    };
    private CartDBManager dbManager;

    @Override
    public void setRootView() {
        setContentView(R.layout.wschoose_merchant);
    }
    @Override
    public void initWidget() {
        super.initWidget();
        changeText("选择商家");
        dbManager = new CartDBManager(ActChooseMerchant.this);
    }

    @Override
    public void initData() {
        super.initData();
        listview = (ListView) findViewById(R.id.wschoose_merchant_ListViewId);
        View headView = getLayoutInflater().inflate(R.layout.headview, null);
        //listview.addHeaderView(headView);

        adapter = new MyAdapter();
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KJLoger.log(TAG, " item click " + position);
                Intent intent = new Intent(ActChooseMerchant.this, ActWeiJieProductDetail.class);
                intent.putExtra("goodId", resultBodyList.get(position).getId());
                intent.putExtra("shopId", resultBodyList.get(position).getId());
                startActivity(intent);
            }
        });
        getData();

    }

    public void getData() {

        WSNearProductBody productBody = new WSNearProductBody();
        productBody.setC__Specific_location("东村国际创意基地");
        productBody.setDistances("2.1km");
        productBody.setFright("免费");
        productBody.setId("1");
        productBody.setLogo("");
        productBody.setPrice("4");
        productBody.setName("红烧牛肉面");
        productBody.setSales("11万");

        WSNearProductBody productBody1 = new WSNearProductBody();
        productBody1.setC__Specific_location("东村国际创意基地");
        productBody1.setDistances("7.5km");
        productBody1.setFright("免费");
        productBody1.setId("2");
        productBody1.setLogo("");
        productBody1.setPrice("5.5");
        productBody1.setName("红烧牛肉面");
        productBody1.setSales("15万");

        WSNearProductBody productBody2 = new WSNearProductBody();
        productBody2.setC__Specific_location("东村国际创意基地");
        productBody2.setDistances("2.5km");
        productBody2.setFright("免费");
        productBody2.setId("3");
        productBody2.setLogo("");
        productBody2.setPrice("7.5");
        productBody2.setName("红烧牛肉面");
        productBody2.setSales("15万");


        resultBodyList.add(productBody);
        resultBodyList.add(productBody1);
        resultBodyList.add(productBody2);
        adapter.notifyDataSetChanged();


    }
private class MyAdapter extends BaseAdapter{
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHodler viewHodler = null;
        if (convertView == null) {
            viewHodler = new ViewHodler();
            convertView = getLayoutInflater().inflate(R.layout.wschoose_merchant_listview_item, null);
            viewHodler.nameTV = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_nameTvId);
            viewHodler.yueShouTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_yueShouTvId);
            viewHodler.distanceTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_distanceTvId);
            viewHodler.locationTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_discribleTvId);
            viewHodler.priceTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_priceId);
            viewHodler.countTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_productcountId);
            viewHodler.chooseTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_chooseId);

            viewHodler.addBtn = (ImageButton) convertView.findViewById(R.id.wschoose_merchant_item_addBtnId);
            viewHodler.deleBtn = (ImageButton) convertView.findViewById(R.id.wschoose_merchant_item_deleteBtnId);

            viewHodler.logonImag = (ImageView) convertView.findViewById(R.id.wschoose_merchant_item_imageViewId);
            convertView.setTag(viewHodler);

        }else {
            viewHodler = (ViewHodler) convertView.getTag();
        }
        final WSNearProductBody productBody = resultBodyList.get(position);
        viewHodler.nameTV.setText(productBody.getName());
        viewHodler.yueShouTv.setText("月售"+productBody.getSales());
        viewHodler.priceTv.setText(productBody.getPrice()+"元");
        viewHodler.distanceTv.setText(productBody.getDistances());
        viewHodler.locationTv.setText(productBody.getC__Specific_location());
        //选择
        viewHodler.chooseTv.setTag(productBody);
        final ViewHodler finalViewHodler = viewHodler;
        viewHodler.chooseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加入购物车和跳转订单
                boolean isAdded = dbManager.isAdded(productBody.getId(), productBody.getId());
                KJLoger.log(TAG, "--isAdded-" + isAdded);
                if (isAdded) {
                    Intent intent = new Intent(ActChooseMerchant.this, ActWSBill.class);
                    intent.putExtra("shopId", productBody.getId());
                    startActivity(intent);
                } else {
                    boolean success = dbManager.addToCart(new CartInfo(productBody.getId(), productBody.getId(),
                            productBody.getName(), Double.parseDouble(productBody.getPrice()), 1));
                    if (success) {
                        Intent intent = new Intent(ActChooseMerchant.this, ActWSBill.class);
                        intent.putExtra("shopId", productBody.getId());
                        startActivity(intent);
                    }
                }

            }
        });

        boolean isAdded0 = dbManager.isAdded(productBody.getId(), productBody.getId());
        KJLoger.log(TAG, "--选择商家--" + isAdded0);
        if (isAdded0) {
            viewHodler.deleBtn.setVisibility(View.VISIBLE);
            viewHodler.countTv.setVisibility(View.VISIBLE);
            viewHodler.countTv.setText(dbManager.getNum(productBody.getId(), productBody.getId())+"");
        } else {
            viewHodler.deleBtn.setVisibility(View.GONE);
            viewHodler.countTv.setVisibility(View.GONE);
        }
        //增加购物车
        viewHodler.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAdded = dbManager.isAdded(productBody.getId(), productBody.getId());
                KJLoger.log(TAG, "--isAdded-" + isAdded);
                if (isAdded) {
                    KJLoger.log(TAG, "--增加购物车商品数量-position-" + position);//修改数据库商品的数量
                    Message message = Message.obtain();
                    message.what = NUM_ADD;
                    message.arg1 = position;
                    handler.sendMessage(message);
                } else {
                    KJLoger.log(TAG, "--加入购物车-position-" + position);
                    Message message = Message.obtain();
                    message.what = ADDTO_CART;
                    message.arg1 = position;
                    handler.sendMessage(message);
                }

            }
        });

        //减少购物车
        viewHodler.deleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KJLoger.log(TAG,"--减少购物车商品数量-position-"+position);
                Message message = Message.obtain();
                message.what = NUM_DEL;
                message.arg1 = position;
                handler.sendMessage(message);
            }
        });

        return convertView;
    }
    class ViewHodler{

        TextView nameTV,yueShouTv,distanceTv,locationTv,priceTv,countTv, chooseTv;
        ImageButton addBtn,deleBtn;
        ImageView logonImag;
    }
}

    private void addShoppingCart(String id,String count,String attribute) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Tel","13717899174");
            jsonObject.put("PId", id);
            jsonObject.put("count", count);
            jsonObject.put("attribute", attribute);

            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "GoodsInCar", params, false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "请求添加购物车成功" + t);
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "请求添加购物车失败" + strMsg);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}


