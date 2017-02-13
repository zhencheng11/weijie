package vmediacn.com.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
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
import vmediacn.com.activity.find.ActSearch;
import vmediacn.com.activity.weijie.ActWeiJieShopDetail;
import vmediacn.com.allBean.weijie.ResultBody;
import vmediacn.com.allBean.weijie.WeiJieRespose;
import vmediacn.com.ui.pulltorefrush.XListView;
import vmediacn.com.util.MyDialog;
import vmediacn.com.util.StringCallBack;

/**
 * A simple {@link Fragment} subclass.
 * 发现
 */
public class FgtFind extends BaseFragment implements XListView.IXListViewListener{
    //分类
    private List<ResultBody> fenLeiList = new ArrayList<>();

    //排序range
    private List<ResultBody> rangeList = new ArrayList<>();
    //筛选
    private List<ResultBody> shaiXuanList = new ArrayList<>();

    List<ResultBody> resultBodyList = new ArrayList<>();
    private XListView listView;
    private String TAG = "vmediacn.com.fragment.FgtFind--";
    private MyAdapter adapter;
    private KJBitmap bitmap = new KJBitmap();

    @BindView(id = R.id.fgt_find_bar_fenLeiId,click = true)
    private LinearLayout fenLeiLay;

    @BindView(id = R.id.fgt_find_bar_rangeId,click = true)
    private LinearLayout rangeLay;

    @BindView(id = R.id.fgt_find_bar_shaiXuanId,click = true)
    private LinearLayout shaiXuanLay;
    //搜索
    @BindView(id = R.id.head_title_view_menuId,click = true)
    private TextView searchTv;

    private boolean isEnd = false;
    private int pageIndex = 1;
    private Activity activity;


    public FgtFind() {
        // Required empty public constructor
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fgt_find, viewGroup, false);
    }

    @Override
    protected void initData() {
        super.initData();
        activity = getActivity();
        KJLoger.log(TAG, "--common.latlon--" + Common.latLng);
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.fgt_find_bar_fenLeiId:
                getPopupWindow1(v);
            break;

            case R.id.fgt_find_bar_rangeId:
                getPopupWindow2(v);
                break;

            case R.id.fgt_find_bar_shaiXuanId:
                getPopupWindow3(v);
                break;

            case R.id.head_title_view_menuId:
                Intent intent = new Intent(activity, ActSearch.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        changeText("发现");
        MyDialog.showDialog(getActivity());
        listView = (XListView) parentView.findViewById(R.id.fgt_find_listViewId);

        View headView = getActivity().getLayoutInflater().inflate(R.layout.headview, null);
        listView.addHeaderView(headView);

        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(true);
        listView.setAutoLoadEnable(true);
        listView.setXListViewListener(this);
        listView.setAutoLoadEnable(true);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        getNearShopDate(true);
        //搜索
        searchTv.setVisibility(View.VISIBLE);
        searchTv.setText("");
        searchTv.setBackgroundResource(R.drawable.btn_search);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KJLoger.log(TAG," item_position--"+position);
                String id1 = resultBodyList.get(position-1).getId();
                Intent intent = new Intent(getActivity(), ActWeiJieShopDetail.class);
                intent.putExtra("Id", id1);
                startActivity(intent);

            }
        });



    }
    public void getPopupWindow1(View parentView) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fgt_find_item1, null);
        WindowManager manager = getActivity().getWindowManager();
        Display defaultDisplay = manager.getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        PopupWindow popupWindow = new PopupWindow(view, width, 699,true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        TextView BLCSTv = (TextView) view.findViewById(R.id.find_item1_bianlichaoshiId);
        String banlitype = BLCSTv.getText().toString();
        onPopupWindowItemClick(BLCSTv, banlitype, popupWindow);

        TextView CYMSTv = (TextView) view.findViewById(R.id.find_item1_canyinmeishiId);
        onPopupWindowItemClick(CYMSTv, CYMSTv.getText().toString(), popupWindow);

        TextView SHFUTv = (TextView) view.findViewById(R.id.find_item1_shenghuofuwuId);
        onPopupWindowItemClick(SHFUTv,SHFUTv.getText().toString(),popupWindow);

        popupWindow.showAsDropDown(parentView);


    }

    private void onPopupWindowItemClick( View view, final String type ,final PopupWindow popupWindow) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    /*
                    * 分类
                    * */
                    case R.id.find_item1_canyinmeishiId://餐饮美食点
                        KJLoger.log(TAG, "--餐饮美食点————");
                        requstFenLeiDate(type, popupWindow);
                        break;

                    case R.id.find_item1_bianlichaoshiId://便利超市
                        KJLoger.log(TAG, "--便利超市————");
                        requstFenLeiDate(type, popupWindow);
                        break;

                    case R.id.find_item1_shenghuofuwuId://生活服务
                        KJLoger.log(TAG, "--生活服务————");
                        requstFenLeiDate(type, popupWindow);
                        break;
                    /*
                    *排序
                    * */
                    case R.id.find_item3_liwozuijinId://离我最近
                        KJLoger.log(TAG, "--离我最近————");
                        requstRangeDate(type, popupWindow);
                        break;

                    case R.id.find_item3_renqizuigaoId://人气最好
                        KJLoger.log(TAG, "--人气最好————");
                        requstRangeDate(type, popupWindow);
                        break;

                    case R.id.find_item3_pinjiazuihaoId://评价最好
                        KJLoger.log(TAG, "--评价最好————");
                        requstRangeDate(type, popupWindow);
                        break;
                    /*
                    * 筛选
                    * */
                    case R.id.find_item2_tejiayouhuiId://特价优惠
                        KJLoger.log(TAG, "--特价优惠————");
                        requstRangeDate(type, popupWindow);
                        break;

                    case R.id.find_item2_xiadanlijianId://下单立减
                        KJLoger.log(TAG, "--下单立减————");
                        requstRangeDate(type, popupWindow);
                        break;

                    case R.id.find_item2_zhengpingyouhuiId://赠品优惠
                        KJLoger.log(TAG, "--赠品优惠————");
                        requstRangeDate(type, popupWindow);
                        break;

                    case R.id.find_item2_mianfeipeisongId://免费配送
                        KJLoger.log(TAG, "--免费配送————");
                        requstRangeDate(type, popupWindow);
                        break;

                }
                popupWindow.dismiss();
            }
        });

    }

    private void requstRangeDate(final String type, PopupWindow popupWindow) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Type", type);
            jsonObject.put("name", "");
            jsonObject.put("pageSize", 6);
            jsonObject.put("pageIndex", 1);
            jsonObject.put("longitude", Common.latLng.longitude);//经度116.5691441688
            jsonObject.put("latitude", Common.latLng.latitude);//纬度

            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "GetNearShops", params, new StringCallBack(activity) {
                @Override
                public void onSuccess(String json) {
                    KJLoger.log(TAG, "--"+type+"数据请求成功" + json);
                    Gson gson = new Gson();
                    WeiJieRespose weiJieRespose = gson.fromJson(json, WeiJieRespose.class);
                    if (weiJieRespose.getResultState().equals("true")) {
                        List<ResultBody> resultBody = weiJieRespose.getResultBody();
                        if (resultBody != null && resultBody.size() > 0) {
                            resultBodyList.clear();
                            resultBodyList.addAll(resultBody);
                            adapter.notifyDataSetChanged();
                        } else {
                            getTest(type);
                        }
                    }
                    else {
                        getTest(type);
                    }

                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    getTest(type);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requstFenLeiDate(final String type,PopupWindow popupWindow) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Type", type);
            jsonObject.put("longitude", Common.latLng.longitude);
            jsonObject.put("latitude", Common.latLng.latitude);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "GetLXNearShops", params, new StringCallBack(activity) {
                @Override
                public void onSuccess(String json) {
                    KJLoger.log(TAG, "--便利超市数据请求成功" + json);
                    Gson gson = new Gson();
                    WeiJieRespose weiJieRespose = gson.fromJson(json, WeiJieRespose.class);
                    if (weiJieRespose.getResultState().equals("true")) {
                        List<ResultBody> resultBody = weiJieRespose.getResultBody();
                        if (resultBody != null && resultBody.size() > 0) {
                            resultBodyList.clear();
                            resultBodyList.addAll(resultBody);
                            adapter.notifyDataSetChanged();
                        } else {
                            getTest(type);
                        }
                    }
                    else {
                        getTest(type);
                    }

                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    getTest(type);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPopupWindow2(View parentView) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fgt_find_item3, null);
        WindowManager manager = getActivity().getWindowManager();
        Display defaultDisplay = manager.getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        PopupWindow popupWindow = new PopupWindow(view, width, 699, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        TextView LWZJTv = (TextView) view.findViewById(R.id.find_item3_liwozuijinId);
        onPopupWindowItemClick(LWZJTv, "JL", popupWindow);//距离

        TextView RQZGTv = (TextView) view.findViewById(R.id.find_item3_renqizuigaoId);
        onPopupWindowItemClick(RQZGTv,"RQ",popupWindow);//人气

        TextView PJZHTv = (TextView) view.findViewById(R.id.find_item3_pinjiazuihaoId);
        onPopupWindowItemClick(PJZHTv,"JG",popupWindow);//评价

        popupWindow.showAsDropDown(parentView);
    }
    public void getPopupWindow3(View parentView) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fgt_find_item2, null);
        WindowManager manager = getActivity().getWindowManager();
        Display defaultDisplay = manager.getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        PopupWindow popupWindow = new PopupWindow(view, width, 699, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        TextView TJYIHTv = (TextView) view.findViewById(R.id.find_item2_tejiayouhuiId);//特价优惠
        onPopupWindowItemClick(TJYIHTv,TJYIHTv.getText().toString(),popupWindow);

        TextView XDLJTv = (TextView) view.findViewById(R.id.find_item2_xiadanlijianId);//下单立减
        onPopupWindowItemClick(XDLJTv,XDLJTv.getText().toString(),popupWindow);

        TextView ZPYHTv = (TextView) view.findViewById(R.id.find_item2_zhengpingyouhuiId);//赠品优惠
        onPopupWindowItemClick(ZPYHTv,ZPYHTv.getText().toString(),popupWindow);

        TextView MFPSTv = (TextView) view.findViewById(R.id.find_item2_mianfeipeisongId);//免费配送
        onPopupWindowItemClick(MFPSTv,MFPSTv.getText().toString(),popupWindow);



        popupWindow.showAsDropDown(parentView);
    }

    @Override
    public void onRefresh() {
        pageIndex = 1;
        getNearShopDate(true);
        KJLoger.log(TAG, "刷新 的 pageIndex" + pageIndex);
    }

    @Override
    public void onLoadMore() {
        if (!isEnd) {
            ++pageIndex;
            getNearShopDate(false);
            KJLoger.log(TAG, "加载更多 的 pageIndex" + pageIndex);
        }
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fgt_find_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.nameTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_nameTvId);
                viewHolder.startLay = (LinearLayout) convertView.findViewById(R.id.wschoose_merchant_item_starLayId);
                viewHolder.distanceTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_distanceId);
                viewHolder.productCountTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_all_productId);
                viewHolder.yueShouTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_yueShouTvId);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.wschoose_merchant_item_shop_imageViewId);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ResultBody resultBody = resultBodyList.get(position);
            String distances = resultBody.getDistances();
            String number = resultBody.getNumber();
            int grade = Integer.parseInt(resultBody.getGrade());
            String logo = resultBody.getLogo();
            String sales = resultBody.getSales();
            String name = resultBody.getName();
            KJLoger.log(TAG, "imageView==" + viewHolder.imageView);
            bitmap.display(viewHolder.imageView, logo);
            viewHolder.nameTv.setText(name);
            viewHolder.distanceTv.setText(distances);
            viewHolder.productCountTv.setText(number + "件商品");
            int childCount = viewHolder.startLay.getChildCount();
            if (childCount - grade != 0) {
                for (int i = grade; i < childCount; i++) {
                    ImageView imageView = (ImageView) viewHolder.startLay.getChildAt(i);
                    imageView.setImageResource(R.mipmap.star_def);
                }
            }

            viewHolder.yueShouTv.setText(sales+"");
            return convertView;
        }
        private class ViewHolder {
            TextView nameTv,distanceTv,productCountTv,yueShouTv;
            ImageView imageView;
            LinearLayout startLay;
        }
    }

    /*private String longitude = "116.5691441688";
    private String latitude = "39.8712016955";*/
    public void getNearShopDate(final boolean isClear) {
        //显示附近的店铺
        getTest();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("longitude", Common.latLng.longitude);//经度116.5691441688
            jsonObject.put("latitude", Common.latLng.latitude);//纬度
            jsonObject.put("Type", "JL");//纬度
            jsonObject.put("name", "");//纬度
            jsonObject.put("pageSize", 6);
            jsonObject.put("pageIndex", pageIndex);

            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "GetNearShops", params, false, new HttpCallBack() {

                @Override
                public void onSuccess(String json) {
                    super.onSuccess(json);
                    /*KJLoger.log(TAG, "--请求获取附近的店铺-t-" + json);

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
                    WeiJieRespose weiJieRespose = gson.fromJson(json, WeiJieRespose.class);
                    if (weiJieRespose.getResultState().equals("true")) {
                        List<ResultBody> resultBody = weiJieRespose.getResultBody();
                        if (resultBody == null || resultBody.size()== 0) {
                            isEnd = true;
                            listView.setPullLoadEnable(false);
                        } else {
                            listView.setPullLoadEnable(true);
                            isEnd = false;
                        } if (isClear) {
                            resultBodyList.clear();
                        }
                        resultBodyList.addAll(resultBody);
                        adapter.notifyDataSetChanged();
                    }*/
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, " 请求获取附近的店铺失败" + strMsg);
                    getTest();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getTest(String type) {

        ResultBody productBody = new ResultBody();
        productBody.setC__Specific_location("东村国际创意基地");
        productBody.setDistances("2.1km");
        productBody.setGrade("5");
        productBody.setNumber("110");
        productBody.setId("1");
        productBody.setLogo("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1460514985&di=d03a53c2532e571ce034f158cccbf1a3&src=http://pic8.nipic.com/20100629/466602_091034083070_2.jpg");
        productBody.setName(type);
        productBody.setSales("11万");

        ResultBody productBody1 = new ResultBody();
        productBody1.setC__Specific_location("东村国际创意基地");
        productBody1.setDistances("2.5km");
        productBody1.setId("2");
        productBody1.setNumber("222");
        productBody1.setGrade("5");
        productBody1.setLogo("http://m2.quanjing.com/2m/top014/top-664930.jpg");
        productBody1.setName(type);
        productBody1.setSales("15万");
        resultBodyList.clear();
        resultBodyList.add(productBody);
        resultBodyList.add(productBody);
        resultBodyList.add(productBody1);
        resultBodyList.add(productBody1);
        adapter.notifyDataSetChanged();
    }

    private void getTest() {

        ResultBody productBody = new ResultBody();
        productBody.setC__Specific_location("东村国际创意基地");
        productBody.setDistances("2.1km");
        productBody.setGrade("5");
        productBody.setNumber("110");
        productBody.setId("1");
        productBody.setLogo("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1460514985&di=d03a53c2532e571ce034f158cccbf1a3&src=http://pic8.nipic.com/20100629/466602_091034083070_2.jpg");
        productBody.setName("BHG生活超市");
        productBody.setSales("11万");

        ResultBody productBody1 = new ResultBody();
        productBody1.setC__Specific_location("东村国际创意基地");
        productBody1.setDistances("2.5km");
        productBody1.setId("2");
        productBody1.setNumber("222");
        productBody1.setGrade("5");
        productBody1.setLogo("http://m2.quanjing.com/2m/top014/top-664930.jpg");
        productBody1.setName("BHG生活超市");
        productBody1.setSales("15万");
        resultBodyList.clear();
        resultBodyList.add(productBody);
        resultBodyList.add(productBody);
        resultBodyList.add(productBody1);
        resultBodyList.add(productBody1);
        adapter.notifyDataSetChanged();
    }
}
