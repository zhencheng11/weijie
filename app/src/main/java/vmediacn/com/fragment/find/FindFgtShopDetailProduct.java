package vmediacn.com.fragment.find;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.load.LoginActivity;
import vmediacn.com.activity.weijie.ActWSBill;
import vmediacn.com.activity.weijie.ActWeiJieProductDetail;
import vmediacn.com.allBean.find.CartInfo;
import vmediacn.com.allBean.find.ProductDetailItemResponse;
import vmediacn.com.allBean.find.ProductDetailItemResultBody;
import vmediacn.com.fragment.BaseFragment;
import vmediacn.com.util.CartDBManager;
import vmediacn.com.util.StringCallBack;
//{"resultState":true,"message":"获取成功","resultBody":[{"Id":4,"Name":"方便面","Price":20.00,"Pic":"http://192.168.0.68:8048/Logo/zj.jpg","type":"炸鸡是很多快餐店的招牌食品之一。金黄香脆的外皮，鲜嫩多汁的鸡肉，还有香辣咸麻的味道，掀起了一股男女老幼吃炸鸡的热浪。主料三黄鸡1000g辅料油适量 盐适量 五香粉适量 生姜...","brand":"方便面","ShopsId":10,"Creat_time":"\/Date(1460364661000)\/","State":1,"sales":564656,"rx":null}]}

/**
 * A simple {@link Fragment} subclass.
 * 店铺的产品详情
 */
public class FindFgtShopDetailProduct extends BaseFragment {


    @BindView(id = R.id.fgt_find_shop_detail_product_payId,click = true)
    private Button paytBtn;

    @BindView(id = R.id.fgt_find_shop_detail_product_cartImagId,click = true)
    private ImageView cartImage;

    @BindView(id = R.id.fgt_find_shop_detail_product_cartcountId,click = true)
    private TextView cart_countTv;
    //清空购物车
    @BindView(id = R.id.cart_clearCartId,click = true)
    private TextView clear_cartTv;

    @BindView(id = R.id.fgt_find_shop_detail_product_cartallMoneyId)
    private TextView cart_allMoneyTv;

    @BindView(id = R.id.fgt_find_shop_detail_product_titleId)
    private TextView titleTv;

    @BindView(id = R.id.fgt_find_shop_detail_product_type_listViewId)
    private ListView typeListView;

    @BindView(id = R.id.fgt_find_shop_detail_product_gridViewId)
    private ListView detailListView;


    private MyTypeAdapter typeAdapter= new MyTypeAdapter();
    private DetailAdapter detailAdapter= new DetailAdapter();

    private List<ProductDetailItemResultBody> resultBodyList = new ArrayList<>();
    private List<String> cartIdList = new ArrayList<>();
    private CartAdapter cartAdapter = new CartAdapter();
    private List<CartInfo> cartBodyList = new ArrayList<CartInfo>();

    private List<String> typeList = new ArrayList();
    private static final int NUM_ADD = 100;
    private static final int NUM_DEL = 101;
    private static final int ADDTO_CART = 102;
    private static final int CHANGE_VIEW = 103;
    private static final int CART_ADD_NUM = 104;
    private static final int CART_DELE_NUM = 105;
    private int cart_num=1;
    private String TAG = "vmediacn.com.activity.weijie.FindFgtShopDetailProduct--";
    private boolean isture =true;
    private List<ProductDetailItemResultBody> addedList = new ArrayList<>();//存放加入购物车的bean
    private boolean flag;


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
    private double allMoney;
    private String shopId;
    private CartDBManager dbManager;
    private int topLayHeight;

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int all_cart_num=1;
    private Handler cartHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //购物车里的
            KJLoger.log(TAG, "--购物车msg.what--"+msg.what);
            View cart_view = cartListView1.getChildAt(msg.arg1-cartListView1.getFirstVisiblePosition());
            TextView cart_num_Tv = (TextView) cart_view.findViewById(R.id.cart_item_countId);
            CartInfo cart_bean = null;
            switch (msg.what) {
                case CART_ADD_NUM://购物车里的商品加
                    cart_bean = cartBodyList.get(msg.arg1);
                    int cart_num0 = Integer.parseInt(cart_num_Tv.getText().toString());
                    all_cart_num = Integer.parseInt(cart_countTv.getText().toString());
                    dbManager.updateCartNum(shopId, cart_bean.goodId, cart_num0 + 1);
                    cart_num0 = cart_num0 + 1;
                    cart_num0 = cart_num0> 99 ? 99 : cart_num0;
                    cart_bean.num=cart_num0;
                    cart_num_Tv.setText(cart_num0 + "");
                    getShoppingCartDate(shopId);
                    break;

                case CART_DELE_NUM://购物车里的商品减
                    cart_bean = cartBodyList.get(msg.arg1);
                    int cart_num1 = Integer.parseInt(cart_num_Tv.getText().toString());
                    if (cart_num1 > 1) {
                        dbManager.updateCartNum(shopId, cart_bean.goodId, cart_num1 - 1);
                        cart_num1 = cart_num1 - 1;
                        cart_num1 = cart_num1 > 1 ? cart_num1 : 1;
                        cart_num_Tv.setText(cart_num1+"");
                        cart_countTv.setText(all_cart_num+"");
                        getShoppingCartDate(shopId);
                    }
                    break;
            }
        }
    };
    private Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //商品详情里面的
            View view = detailListView.getChildAt(msg.arg1-detailListView.getFirstVisiblePosition());
            TextView num_Tv = (TextView) view.findViewById(R.id.wschoose_merchant_item_productcountId);
            ImageButton deleBtn = (ImageButton) view.findViewById(R.id.wschoose_merchant_item_deleteBtnId);
            ProductDetailItemResultBody bean = null;


            switch (msg.what) {

                case ADDTO_CART://加入购物车
                    bean = resultBodyList.get(msg.arg1);
                    CartInfo cartInfo = new CartInfo(shopId,
                            bean.getId(),bean.getName(),Double.parseDouble(bean.getPrice()),1);
                    boolean succsess = dbManager.addToCart(cartInfo);
                    KJLoger.log(TAG, "--succsess--" + succsess);
                    if (succsess) {
                        num_Tv.setVisibility(View.VISIBLE);
                        num_Tv.setText("1");
                        deleBtn.setVisibility(View.VISIBLE);
                        getShoppingCartDate(shopId);
                    }
                    break;

                case NUM_ADD://数量加
                    bean = resultBodyList.get(msg.arg1);
                    int num = Integer.parseInt(num_Tv.getText().toString());
                    KJLoger.log(TAG, "--num--" + num);
                    dbManager.updateCartNum(shopId, bean.getId(), num + 1);
                    num = num + 1;
                    num = num > 99 ? 99 : num;
                    num_Tv.setText(num + "");
                    getShoppingCartDate(shopId);
                    break;

                case NUM_DEL://数量减
                    bean = resultBodyList.get(msg.arg1);
                    int num1 = Integer.parseInt(num_Tv.getText().toString());
                    KJLoger.log(TAG, "--num1--" + num1);
                    if (num1 > 1) {
                        dbManager.updateCartNum(shopId,bean.getId(),num1-1);
                        num1 = num1 - 1;
                        num1 = num1 > 1 ? num1 : 1;
                        num_Tv.setText(num1 + "");
                        getShoppingCartDate(shopId);
                    }
                    break;
            }

        }
    };
    private LinearLayout cartView1;
    private SwipeMenuListView cartListView1;
    public FindFgtShopDetailProduct() {
        // Required empty public constructor
    }
    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fgt_find_shop_detail_product, viewGroup, false);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        dbManager = new CartDBManager(getActivity());
        Bundle arguments = getArguments();
        if (arguments != null) {
            shopId = arguments.getString("shopId");
            topLayHeight = arguments.getInt("top");

            KJLoger.log(TAG, "--shopId==" + shopId);

        }
        initView(parentView);
    }

    private void initView(View parentView) {

        /*cartView1 = (LinearLayout) parentView.findViewById(R.id.cart_relateLayId);
        cartListView1 = (SwipeMenuListView)parentView.findViewById(R.id.cart_listViewId);
        cartListView1.setMenuCreator(creator);

        cartListView1.setAdapter(cartAdapter);*/
        typeListView.setAdapter(typeAdapter);
        detailListView.setAdapter(detailAdapter);
        typeListView .setVerticalScrollBarEnabled(false);
        //getShopping();//测试数据
        getProductType();
        getShoppingCartDate(shopId);


        typeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KJLoger.log(TAG, "--type item click-- " + position);
                cleanitem();
                TextView text = (TextView) view.findViewById(R.id.wschoose_merchant_item_nameTvId);
                text.setTextColor(Color.parseColor("#01AAEF"));
                text.setBackgroundColor(Color.WHITE);
                titleTv.setText(typeList.get(position));
                String type = typeList.get(position);
                KJLoger.log(TAG, "--type -- " + type);
                requstDetailFromType(type);

            }
        });

        detailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ActWeiJieProductDetail.class);
                String goodId = resultBodyList.get(position).getId();
                KJLoger.log(TAG, "--detail item click-- " + position);
                KJLoger.log(TAG, "--detailListView--position--" + position);
                intent.putExtra("goodId", goodId);
                intent.putExtra("shopId", shopId);
                startActivity(intent);
            }
        });

    }



    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.fgt_find_shop_detail_product_payId:
                //确定
                if (!Common.isLogin) {
                    new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("请登录")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivityForResult(intent, 100);
                                }
                            }).setIcon(R.mipmap.ic_note).show();
                } else {
                    Intent intent = new Intent(getActivity(), ActWSBill.class);
                    intent.putExtra("shopId",shopId);
                    startActivity(intent);
                }


                break;
            case R.id.fgt_find_shop_detail_product_cartcountId:
            case R.id.fgt_find_shop_detail_product_cartImagId://显示购物车pop
                //showCartPop1(v);
                showCartPop(v);
                KJLoger.log(TAG, "--购物车被点击了-");
                break;

        }
    }


    private void setAllMoney(List<CartInfo> list){
        allMoney=0.0;
        for (int i = 0; i < list.size(); i++) {
            CartInfo cartBody = list.get(i);
            double price = cartBody.price;
            int num = cartBody.num;

            allMoney = allMoney +price*num;
        }
        cart_allMoneyTv.setText(allMoney + "");
    }
    //请求商品详情
    private void requstDetailFromType(String type) {

        KJLoger.log(TAG, "--shopId--" + shopId);
        KJLoger.log(TAG, "--type--" + type);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Type", type);
            jsonObject.put("ShopId", shopId);
            jsonObject.put("pageSize", 6);
            jsonObject.put("pageIndex", 1);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "ShopsGoods", params, new StringCallBack(getActivity()) {
                @Override
                public void onSuccess(String json) {
                    KJLoger.log(TAG, "--根据商品类型请求商品成功-t-" + json);
                    Gson gson = new Gson();
                    ProductDetailItemResponse respose = gson.fromJson(json, ProductDetailItemResponse.class);
                    String resultState = respose.getResultState();
                    if (resultState.equals("true")) {
                        List<ProductDetailItemResultBody> resultBody = respose.getResultBody();
                        if (resultBody != null && resultBody.size() > 0) {
                            resultBodyList.clear();
                            resultBodyList.addAll(resultBody);
                            detailAdapter.notifyDataSetChanged();
                        } else {
                            //getShopping();
                        }
                        detailAdapter.notifyDataSetChanged();
                    } else {
                        // getShopping();
                    }
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
            cart_countTv.setVisibility(View.VISIBLE);
            cart_countTv.setText(cartInfos.size()+ "");
            cartIdList.clear();
            cartBodyList.clear();
            cartBodyList.addAll(cartInfos);
            setAllMoney(cartBodyList);
            for (int i = 0; i < cartInfos.size(); i++) {
                CartInfo cartInfo = cartInfos.get(i);
                String id = cartInfo.goodId;
                cartIdList.add(id);
            }
            cartAdapter.notifyDataSetChanged();
        } else {
            KJLoger.log(TAG, "--购物车没有商品--");
            cart_countTv.setVisibility(View.GONE);
            cartBodyList.clear();
            cart_allMoneyTv.setText("0.0");
            cartAdapter.notifyDataSetChanged();
        }
    }

    private void showCartPop1(View v) {
            getShoppingCartDate(shopId);
        if (cartView1.getVisibility() == View.VISIBLE) {
            KJLoger.log(TAG,"--visible--"+"VISIBLE");
            cartView1.setVisibility(View.GONE);
        } else {
            KJLoger.log(TAG,"--visible--"+"GONE");
            cartView1.setVisibility(View.VISIBLE);
        }

    }

    private void showCartPop(View parentView) {
        View popView = getActivity().getLayoutInflater().inflate(R.layout.cart_view, null);
        ImageView bgImage = (ImageView) popView.findViewById(R.id.cart_bgId);
        cartListView1 = (SwipeMenuListView) popView.findViewById(R.id.cart_listViewId);
        TextView clearTv = (TextView) popView.findViewById(R.id.cart_clearCartId);

        LinearLayout cartLay = (LinearLayout) getActivity().findViewById(R.id.cartLayId);

        cartListView1.setAdapter(cartAdapter);
        getShoppingCartDate(shopId);

        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        int windowHeight = defaultDisplay.getHeight();

        int top = cartLay.getTop();
        KJLoger.log(TAG, "--topLayHeight=" + topLayHeight);
        final PopupWindow popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                top+topLayHeight,true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        KJLoger.log(TAG, "--cart_heght=" + cartLay.getHeight());
        int cartbgHeight = bgImage.getMeasuredHeight();
        KJLoger.log(TAG, "--cartbgHeight=" + cartbgHeight);

        popupWindow.showAtLocation(cartLay, Gravity.BOTTOM, 0, cartLay.getHeight());

        clearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KJLoger.log(TAG, "--清除购物车---");
                dbManager.clearCart(shopId);
                getShoppingCartDate(shopId);
            }
        });


        bgImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return false;
            }
        });
        cartListView1.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int menu_position) {
                switch (menu_position) {
                    case 0://删除地址
                        KJLoger.log(TAG, " --删除的Item--position--" + i);
                        String id = cartBodyList.get(i).goodId;
                        // requstDeleteCart(id,i);
                        dbManager.deleteItem(id);
                        getShoppingCartDate(shopId);
                        break;
                }
                return false;
            }
        });
    }
    //根据类型请求不到数据时的测试数据
    public void getShopping() {
        ProductDetailItemResultBody productBody = new ProductDetailItemResultBody();
        productBody.setName("方便面");
        productBody.setPrice("20.00");
        productBody.setSales("564656");
        productBody.setId("4");
        productBody.setPic("http://192.168.0.68:8048/Logo/zj.jpg");
        productBody.setType("炸鸡是很多快餐店的招牌食品之一。\n" +
                "    // 金黄香脆的外皮，鲜嫩多汁的鸡肉，还有香辣咸麻的味道，掀起了一股男女老幼吃炸鸡的热浪。\n" +
                "    // 主料三黄鸡1000g辅料油适量 盐适量 五香粉适量 生姜...");
        productBody.setBrand("方便面");
        productBody.setShopsId("10");
        productBody.setCreat_time("\\/Date(1460364661000)\\/");
        productBody.setState("1");
        productBody.setRx("");
        resultBodyList.clear();
        resultBodyList.add(productBody);
        detailAdapter.notifyDataSetChanged();



    }
    //类型listView的初始化
    private void cleanitem(){
        for (int i=0;i<typeList.size();i++){
            View view = typeListView.getChildAt(i);
            if (view!=null) {
                TextView text=(TextView) view.findViewById(R.id.wschoose_merchant_item_nameTvId);
                text.setTextColor(Color.parseColor("#000000"));
                text.setBackgroundColor(Color.parseColor("#e5e5e5"));
            }
        }

    }
    //获取商品类型
    public void getProductType() {

        KJLoger.log(TAG, "--shopId--" + shopId);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", shopId);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "ShopsGoodsLX", params, new HttpCallBack() {
                @Override
                public void onSuccess(String json) {
                    super.onSuccess(json);
                    try {
                        KJLoger.log(TAG, "--请求获取店铺商品类型-t-" + json);
                        JSONTokener jsonTokener = new JSONTokener(json);
                        JSONObject object0 = (JSONObject) jsonTokener.nextValue();
                        if (object0.getString("resultState").equals("true")) {
                            String resultBody = object0.getString("resultBody");
                            JSONArray array = new JSONArray(resultBody);
                            if (array != null && array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = (JSONObject) array.get(i);
                                    String typeName = object.getString("TypeName");
                                    typeList.add(typeName);

                                }
                                titleTv.setText(typeList.get(0));
                                typeAdapter.notifyDataSetChanged();
                                requstDetailFromType(typeList.get(0));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //没有商品类型时的测试数据
    private void test() {
        for (int i = 0; i < 10; i++) {
            typeList.add("商品類型" + i);
        }
    }

    private class MyTypeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return typeList.size();
        }

        @Override
        public Object getItem(int position) {
            return typeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHodler = null;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fgt_find_shop_detail_product_list_item_type, null);
                viewHodler = new ViewHolder();
                viewHodler.nameTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_nameTvId);
                convertView.setTag(viewHodler);

            }else {
                viewHodler = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                viewHodler.nameTv.setTextColor(Color.parseColor("#01AAEF"));
                viewHodler.nameTv.setBackgroundColor(Color.WHITE);
            }
            String type = (String) typeList.get(position);
            viewHodler.nameTv.setText(type);
            return convertView;
        }
        class ViewHolder{
            TextView nameTv ;
        }
    }


    private class DetailAdapter extends BaseAdapter{

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
            ViewHolder viewHodler = null;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fgt_find_shop_detail_product_list_item, null);
                viewHodler = new ViewHolder();
                viewHodler.imageView = (ImageView) convertView.findViewById(R.id.wschoose_merchant_item_imageViewId);
                viewHodler.nameTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_nameTvId);
                viewHodler.yueShouTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_yueShouTvId);
                viewHodler.jiaGeTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_jiageId);
                viewHodler.contuntTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_productcountId);
                viewHodler.addBtn = (ImageButton) convertView.findViewById(R.id.wschoose_merchant_item_addBtnId);
                viewHodler.deleBtn = (ImageButton) convertView.findViewById(R.id.wschoose_merchant_item_deleteBtnId);

                convertView.setTag(viewHodler);

            }else {
                viewHodler = (ViewHolder) convertView.getTag();
            }
            final ProductDetailItemResultBody productBody = resultBodyList.get(position);
            viewHodler.nameTv.setText(productBody.getName());
            viewHodler.yueShouTv.setText("月售"+productBody.getSales());
            viewHodler.jiaGeTv.setText("¥" + productBody.getPrice());
            KJBitmap bitmap = new KJBitmap();
            bitmap.display(viewHodler.imageView, productBody.getPic());
            boolean isAdded0 = dbManager.isAdded(shopId, productBody.getId());
            if (isAdded0) {
                viewHodler.deleBtn.setVisibility(View.VISIBLE);
                viewHodler.contuntTv.setVisibility(View.VISIBLE);
                viewHodler.contuntTv.setText(dbManager.getNum(shopId, productBody.getId())+"");
            } else {
                viewHodler.deleBtn.setVisibility(View.GONE);
                viewHodler.contuntTv.setVisibility(View.GONE);
            }
            //增加购物车
            viewHodler.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isAdded = dbManager.isAdded(shopId, productBody.getId());
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
        }
        class ViewHolder{
            ImageView imageView ;
            TextView nameTv ,yueShouTv,jiaGeTv,contuntTv;
            ImageButton addBtn ,deleBtn;
        }
    //购物车的adapter
    private class CartAdapter extends BaseAdapter{


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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.cart_item_view, null);
                cartHolder.cart_addBtn = (ImageButton) convertView.findViewById(R.id.cart_item_addId);
                cartHolder.cart_deleBtn = (ImageButton) convertView.findViewById(R.id.cart_item_deleId);
                cartHolder.countTv = (TextView) convertView.findViewById(R.id.cart_item_countId);
                cartHolder.nameTv = (TextView) convertView.findViewById(R.id.cart_item_nameId);
                cartHolder.moneyTv = (TextView) convertView.findViewById(R.id.cart_item_moneyId);
                convertView.setTag(cartHolder);
            } else {
                cartHolder = (CartHolder) convertView.getTag();
            }
            //KJLoger.log(TAG, "--cartAdapter--getView执行了--");
            CartInfo resultBody = cartBodyList.get(position);
            String name = resultBody.goodName;
            int number = resultBody.num;
            double price = resultBody.price;
            cartHolder.nameTv.setText(name);
            cartHolder.countTv.setText(number+"");
            cartHolder.moneyTv.setText(price+"");
            cartHolder.cart_addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = Message.obtain();
                    message.what=CART_ADD_NUM;
                    message.arg1= position;
                    cartHandler.sendMessage(message);
                    KJLoger.log(TAG, "--cart_购物车数量加--");
                }
            });

            cartHolder.cart_deleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = Message.obtain();
                    message.what=CART_DELE_NUM;
                    message.arg1= position;
                    cartHandler.sendMessage(message);
                    KJLoger.log(TAG, "--cart_购物车数量减--");
                }
            });
            return convertView;
        }
        private class CartHolder{
            private ImageButton cart_addBtn ,cart_deleBtn;
            private TextView nameTv,countTv,moneyTv;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getShoppingCartDate(shopId);

    }
}


