package vmediacn.com.activity.weijie;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.allBean.find.CartInfo;
import vmediacn.com.allBean.weijie.ProductDetailResultBody;
import vmediacn.com.allBean.weijie.WSProductDetailRespose;
import vmediacn.com.util.CartDBManager;
import vmediacn.com.util.StringCallBack;

/*
*商品详情
* */
public class ActWeiJieProductDetail extends BaseActivity {

    private String TAG = "vmediacn.com.activity.weijie.ActWeiJieProductDetail";
    private String goodId;
    private String shopId;

    @BindView(id = R.id.wschoose_merchant_item_addBtnId, click = true)//购物车商品加
    private ImageButton addBtn;

    @BindView(id = R.id.wschoose_merchant_item_deleteBtnId, click = true)//购物车商品减
    private ImageButton deleBtn;

    @BindView(id = R.id.wschoose_merchant_item_bgId, click = true)//购物车商品减
    private ImageView bgTv;

    @BindView(id = R.id.wschoose_merchant_item_productcountId)//商品数量
    private TextView countTv;

    @BindView(id = R.id.fgt_find_shop_detail_product_cartallMoneyId)//购物总商品总数
    private TextView cart_AllMoneyTV;

    @BindView(id = R.id.fgt_find_shop_detail_product_cartcountId)//购物车商品总数量
    private TextView cart_CountTV;

    @BindView(id = R.id.fgt_find_shop_detail_product_payId,click = true)//立即支付
    private Button payBtn;

    @BindView(id = R.id.wschoose_merchant_item_backImagId,click = true)//返回
    private ImageView backImg;

    @BindView(id = R.id.wschoose_merchant_item_colectionId,click = true)//收藏
    private ImageView colectionImg;

    @BindView(id = R.id.wschoose_merchant_item_shop_imageViewId,click = true)//店铺的背景
    private ImageView shopbackImg;

    @BindView(id = R.id.act_weijie_product_detail_productNameId,click = true)//商品名称
    private TextView productNameTv;

    @BindView(id = R.id.act_weijie_product_detail_productYueShouId,click = true)//月售
    private TextView yueShouTv;

    @BindView(id = R.id.act_weijie_product_detail_jiageId,click = true)//商品价格
    private TextView productPriceTv;

    @BindView(id = R.id.act_weijie_product_detail_describleId,click = true)//商品描述
    private TextView productdescribleTv;

    @BindView(id = R.id.wschoose_merchant_item_nameTvId,click = true)//店铺名称
    private TextView shopNameTv;

    @BindView(id = R.id.wschoose_merchant_item_distanceId,click = true)//店铺距离
    private TextView shopDistanceTv;

    @BindView(id = R.id.wschoose_merchant_item_all_productId,click = true)//店铺商品总数
    private TextView shopProductCountTv;

    @BindView(id = R.id.wschoose_merchant_item_yueShouTvId,click = true)//店铺月售
    private TextView shopYueShouTv;



    private static final int NUM_ADD = 100;
    private static final int NUM_DEL = 101;
    private static final int ADDTO_CART = 102;
    private int cart_num=1;
    private Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADDTO_CART://加入购物车
                    CartInfo cartInfo = new CartInfo(shopId,
                            resultBody.getId(),resultBody.getName(),Double.parseDouble(resultBody.getPrice()),1);
                    boolean succsess = dbManager.addToCart(cartInfo);
                    KJLoger.log(TAG, "--succsess--" + succsess);
                    if (succsess) {
                        countTv.setVisibility(View.VISIBLE);
                        countTv.setText("1");
                        deleBtn.setVisibility(View.VISIBLE);
                        getShoppingCartDate(shopId);
                    }
                    break;
                case NUM_ADD://数量加
                    int num = Integer.parseInt(countTv.getText().toString());
                    KJLoger.log(TAG, "--num--" + num);
                    dbManager.updateCartNum(shopId, goodId, num + 1);
                    num = num + 1;
                    num = num > 99 ? 99 : num;
                    countTv.setText(num + "");
                    getShoppingCartDate(shopId);
                    break;

                case NUM_DEL://数量减
                    int num1 = Integer.parseInt(countTv.getText().toString());
                    KJLoger.log(TAG, "--num1--" + num1);
                    if (num1 > 1) {
                        dbManager.updateCartNum(shopId,goodId,num1-1);
                        num1 = num1 - 1;
                        num1 = num1 > 1 ? num1 : 1;
                        countTv.setText(num1 + "");
                        getShoppingCartDate(shopId);
                    }
                    break;
            }

        }
    };
    private CartDBManager dbManager;
    private ProductDetailResultBody resultBody;

    @Override
    public void setRootView() {

        setContentView(R.layout.act_wei_jie_product_detail);

    }

    @Override
    public void initData() {

        super.initData();
        Intent intent = getIntent();
        if (intent != null) {
            goodId = intent.getStringExtra("goodId");
            shopId = intent.getStringExtra("shopId");
        }
        dbManager = new CartDBManager(ActWeiJieProductDetail.this);
        boolean added = dbManager.isAdded(shopId, goodId);
        if (added) {
            deleBtn.setVisibility(View.VISIBLE);
            countTv.setVisibility(View.VISIBLE);
            countTv.setText(dbManager.getNum(shopId, goodId) + "");
        } else {
            deleBtn.setVisibility(View.GONE);
            countTv.setVisibility(View.GONE);

        }
        getShoppingCartDate(shopId);

        getProductDate();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.wschoose_merchant_item_addBtnId://商品加
                if (dbManager.isAdded(shopId, goodId)) {
                    KJLoger.log(TAG, "--增加购物车商品数量--");
                    Message message = Message.obtain();
                    message.what = NUM_ADD;
                    handler.sendMessage(message);
                } else {
                    Message message = Message.obtain();
                    message.what = ADDTO_CART;
                    handler.sendMessage(message);
                }

                break;

            case R.id.wschoose_merchant_item_deleteBtnId://商品减
                KJLoger.log(TAG,"--减少购物车商品数量--");
                Message message1 = Message.obtain();
                message1.what = NUM_DEL;
                handler.sendMessage(message1);
                break;

            case R.id.fgt_find_shop_detail_product_payId://付款
                KJLoger.log(TAG, "--付款点击了");
                Intent intent = new Intent(ActWeiJieProductDetail.this, ActWSBill.class);
                intent.putExtra("shopId",shopId);
                startActivity(intent);
                break;
            case R.id.wschoose_merchant_item_backImagId://返回按钮
                onBackPressed();
                break;
            case R.id.wschoose_merchant_item_colectionId://收藏
                requstCollect();
                break;
            default:
                break;

        }

    }

    @Override
    public void initWidget() {
        super.initWidget();
    }

    public void getProductDate() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", goodId);
            jsonObject.put("Tel", Common.Usr_PhoneNumber);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Product_details", params, false, new StringCallBack(ActWeiJieProductDetail.this) {

                @Override
                public void onSuccess(String json) {
                    Gson gson = new Gson();
                    WSProductDetailRespose respose = gson.fromJson(json, WSProductDetailRespose.class);
                    KJLoger.log(TAG, "--商品详情请求成功" + json);
                    String resultState = respose.getResultState();
                    if (resultState.equals("true")) {
                        List<ProductDetailResultBody> resultBodyList = respose.getResultBody();
                        if (resultBodyList != null && resultBodyList.size() > 0) {
                            resultBody = resultBodyList.get(0);
                            String shopName = resultBody.getShopName();
                            String goodLogo = resultBody.getGoodLogo();
                            String goodSales = resultBody.getGoodSales();
                            String grade = resultBody.getGrade();
                            String goodName = resultBody.getName();
                            String shop_product_num = resultBody.getNumber();
                            String productPrice = resultBody.getPrice();
                            String product_yueShou = resultBody.getSales();
                            String shopLogo = resultBody.getShopLogo();
                            String shopSales= resultBody.getShopSales();
                            KJBitmap bitmap = new KJBitmap();
                            bitmap.display(bgTv, goodLogo);
                            bitmap.display(shopbackImg, shopLogo);
                            shopNameTv.setText(shopName);

                            yueShouTv.setText("月售：" + product_yueShou + "件");
                            productPriceTv.setText(productPrice);
                            productNameTv.setText(goodName);
                            if (goodSales == null || goodSales.equals("")) {
                                productdescribleTv.setText("暂无商品介绍");
                            } else {
                                productdescribleTv.setText(goodSales);
                            }

                            shopProductCountTv.setText("共" + shop_product_num + "件商品");
                            shopYueShouTv.setText("月售："+shopSales+"件");
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private List<CartInfo> cartBodyList = new ArrayList<CartInfo>();
    private void getShoppingCartDate(String shopId) {
        List<CartInfo> cartInfos = dbManager.query(shopId);
        if (cartInfos != null && cartInfos.size() > 0) {
            KJLoger.log(TAG, "--购物车数量--" + cartInfos.size());
            cart_CountTV.setVisibility(View.VISIBLE);
            cart_CountTV.setText(cartInfos.size() + "");
            cartBodyList.clear();
            cartBodyList.addAll(cartInfos);
            setAllMoney(cartBodyList);
        } else {
            KJLoger.log(TAG, "--购物车没有商品--");
            cart_CountTV.setVisibility(View.GONE);
            cartBodyList.clear();
            cart_AllMoneyTV.setText("0.0");
        }
    }

    private void setAllMoney(List<CartInfo> list){
        double allMoney=0.0;
        for (int i = 0; i < list.size(); i++) {
            CartInfo cartBody = list.get(i);
            double price = cartBody.price;
            int num = cartBody.num;

            allMoney = allMoney +price*num;
        }
        cart_AllMoneyTV.setText(allMoney + "");
    }
    private void requstCollect() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", goodId);
            jsonObject.put("Tel", Common.Usr_PhoneNumber);
            jsonObject.put("state", 2);
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
                            ShowToast(ActWeiJieProductDetail.this, message);

                        } else {
                            ShowToast(ActWeiJieProductDetail.this, message);
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


}
