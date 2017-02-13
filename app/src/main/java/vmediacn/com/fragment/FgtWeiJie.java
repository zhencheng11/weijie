package vmediacn.com.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.MainActivity;
import vmediacn.com.activity.load.LoginActivity;
import vmediacn.com.activity.weijie.ActChooseMerchant;
import vmediacn.com.activity.weijie.ActWeiJieShopDetail;
import vmediacn.com.allBean.weijie.MyLatLongResult;
import vmediacn.com.allBean.weijie.ResultBody;
import vmediacn.com.allBean.weijie.WSNearProductBody;
import vmediacn.com.allBean.weijie.WeiJieRespose;
import vmediacn.com.util.MyPoiOverlay;
import vmediacn.com.util.MyUtil;
import vmediacn.com.zbar.CaptureActivity;

/**
 * A simple {@link Fragment} subclass.
 * 微街
 */
public class FgtWeiJie extends BaseFragment {
    private String json = "{\"resultState\":true,\"message\":\"成功\",\n" + " \"resultBody\":[{\"Id\":1,\"Name\":\"东村跨境体验店\",\"number\":0,\"sales\":null,\n" + "\"detail\":\"汉堡包，以及薯条、炸鸡、汽水、冰品、沙拉、水果等快餐食品\",\n" + "\"C__Specific_location\":\"双桥地铁站附近\",\"Logo\":\"http://192.168.0.68:8048/MS/Logo/麦当劳.jpg\",\n" + "\"Longitude\":\"116.57543\",\"Latitude\":\"39.877001\",\"Distances\":\"在900米以内\",\"type\":\"快餐\",\"grade\":5}]}";

    @BindView(id = R.id.fgt_weijie_xiadanId, click = true)//搜索
    private Button serchBtn;

    @BindView(id = R.id.head_title_view_backIgId)//head_title_view_menuId
    private ImageView arrImg;

    @BindView(id = R.id.head_title_view_menuId, click = true) //扫码
    private TextView scanTv ;

    @BindView(id = R.id.head_title_view_textId)
    private TextView titlTV;

    @BindView(id = R.id.fgt_weijie_relatlayoutId)
    private RelativeLayout bottoViewLayout;

    @BindView(id = R.id.fgt_weijie_locationImagId,click = true)//定位
    private ImageView locationImg;

    private boolean firstLocation;

    private PoiSearch poiSeach;
    private String TAG = "vmediacn.com.fragment.FgtWeiJie--";
    private LocationClient locationClient;
    private BaiduMap baiduMap;
    private Context context;
    private EditText editText;
    private boolean isOpen;
    private LatLng latLng;
    private MyLocationListener locationListener;
    private GeoCoder geoCoder;
    private MapView mapView;
    private MainActivity mainActivity;
    private PopupWindow popupWindow;

    private MyLatLongResult latLongResult;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 110) {
                KJLoger.log(TAG, "--handMessage-收到消息-");
                PopupWindow popupWindow= (PopupWindow) msg.obj;
                Intent intent = new Intent(getActivity(), ActChooseMerchant.class);
                startActivity(intent);
                popupWindow.dismiss();
                mainActivity.showBottomView();
                bottoViewLayout.setVisibility(View.VISIBLE);

            }
        }
    };

    public FgtWeiJie() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        latLongResult = (MyLatLongResult) activity;
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {

        return layoutInflater.inflate(R.layout.fgt_wei_jie, viewGroup, false);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        context = getActivity();
        if (!MyUtil.isNetworkConnected(getActivity())) {
            note(getActivity(), "提示", "请链接网络", "确定");
        }


        //初始化数据
        initView(parentView);

        initMainData();
        //点击地图让pop消失
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                baiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }
    private void test() {
        Gson gson = new Gson();
        WeiJieRespose weiJieRespose = gson.fromJson(json, WeiJieRespose.class);
        List<ResultBody> resultBody1 = weiJieRespose.getResultBody();
        MyPoiOverlay overLay = new MyPoiOverLay(baiduMap, context);
        baiduMap.setOnMarkerClickListener(overLay);
        overLay.setData(resultBody1);
        overLay.addToMap();
        overLay.zoomToSpan();
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.fgt_weijie_xiadanId:
                KJLoger.log(TAG, "下单点击了");
                String seachKey = editText.getText().toString();

                if (seachKey.isEmpty()) {
                    Toast.makeText(getActivity(),"收索内容不能为空",Toast.LENGTH_LONG).show();
                }else if (!Common.isLogin) {
                    new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("请登录")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivityForResult(intent, 100);
                                }
                            }).setIcon(R.mipmap.ic_note).show();
                }else if (!seachKey.isEmpty()&&Common.isLogin){
                    requstSearchData(seachKey,latLng,v);
                }

                break;
            case R.id.head_title_view_menuId:
                //扫码
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.fgt_weijie_locationImagId://定位
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
                break;
        }
    }

    private void requstSearchData(String seachKey, LatLng latLng,View view) {
        List<WSNearProductBody> resultBodyList = new ArrayList<>();
        WSNearProductBody productBody = new WSNearProductBody();
        productBody.setC__Specific_location("东村国际创意基地");
        productBody.setDistances("2.1km");
        productBody.setFright("免费");
        productBody.setId("1");
        productBody.setLogo("");
        productBody.setPrice("5");
        productBody.setName("红烧牛肉面");
        productBody.setSales("11万");
        resultBodyList.add(productBody);

        WSNearProductBody productBody1 = new WSNearProductBody();
        productBody1.setC__Specific_location("东村国际创意基地");
        productBody1.setDistances("2.5km");
        productBody1.setFright("免费");
        productBody1.setId("2");
        productBody1.setLogo("");
        productBody1.setPrice("5.5");
        productBody1.setName("红烧牛肉面");
        productBody1.setSales("15万");
        resultBodyList.add(productBody);
        resultBodyList.add(productBody1);
        resultBodyList.add(productBody1);
        int size = resultBodyList.size();
        //KJLoger.log(TAG, "--商家的size--" + size);

        showShopWaitePop(size, resultBodyList, view);
        //
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", seachKey);
            jsonObject.put("Type", "JL");
            KJLoger.log(TAG, "--搜索关键字--" + seachKey);
            jsonObject.put("longitude", latLng.longitude);
            jsonObject.put("latitude", latLng.latitude);
            jsonObject.put("pageSize", 6);
            jsonObject.put("pageIndex", 1);
            KJLoger.log(TAG, "--经度：longitude--" + latLng.longitude);
            KJLoger.log(TAG, "--纬度：latitude--" + latLng.latitude);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "GetNearGoods", params, false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "--搜索商品请求数据成功" + t);
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "--搜索商品请求数据失败--" + strMsg);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showShopWaitePop(int size, final List<WSNearProductBody> resultBodyList,View parentView) {
        //隐藏键盘
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);
        //显示pop
        LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.pop_notification_merchant, null);
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout topView = (LinearLayout) getActivity().findViewById(R.id.fgt_weijie_titleLayId);
        int top = topView.getBottom();

        int bottom = mainActivity.getBottomViewHeight();
        view.setLayoutParams(params);
        TextView sizeTV = (TextView) view.findViewById(R.id.pop_notification_size);
        sizeTV.setText(size+"");
        TextView cancelTv = (TextView) view.findViewById(R.id.head_title_view_cancelId);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                bottoViewLayout.setVisibility(View.VISIBLE);
            }
        });

        //topView.setVisibility(View.GONE);//隐藏topView
        //KJLoger.log(TAG,"--topView-visible-"+topView.getVisibility());
        bottoViewLayout.setVisibility(View.GONE);//隐藏底部的View
        mainActivity.hideBottomView();//隐藏Mainactivity 底部
        KJLoger.log(TAG,"--bottom-"+bottom);
        //height-400
        popupWindow = new PopupWindow(view,width,height);
//        final PopupWindow popupWindow = new PopupWindow(view, view.getWidth(), view.getHeight());
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        //模拟延时发送
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    handler.obtainMessage(110, popupWindow).sendToTarget();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(); ;
        mainActivity.hideBottomView();

    }

    private void initMainData() {
        KJLoger.log(TAG, "initMainData执行");
        arrImg.setVisibility(View.GONE);
        scanTv.setVisibility(View.VISIBLE);
        scanTv.setText("");
        scanTv.setBackgroundResource(R.drawable.btn_scan);
        locationClient = new LocationClient(getActivity());
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        // 定位初始化
        locationListener = new MyLocationListener();
        locationClient.registerLocationListener(locationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        //option.setScanSpan(1000);
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        locationClient.setLocOption(option);
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.
                LocationMode.NORMAL, true, null));
        locationClient.start();
    }

    private void initView(View parentView) {
        KJLoger.log(TAG, "initView执行");

        mainActivity = (MainActivity) getActivity();
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new MyGeoCodeListener());

        mapView = (MapView) parentView.findViewById(R.id.fgt_weijie_mapViewId);

        baiduMap = mapView.getMap();
        editText = (EditText) parentView.findViewById(R.id.fgt_weijie_seachId);
        //地图上比例尺
        mapView.showScaleControl(false);

        // 隐藏缩放控件
        mapView.showZoomControls(false);
    }



    private class MyGeoCodeListener  implements OnGetGeoCoderResultListener{

        private String title;

        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(getContext(), "抱歉，未能找到结果", Toast.LENGTH_SHORT).show();
                return;
            }
            KJLoger.log(TAG, "=-onGetReverseGeoCodeResult-执行了");
            List<PoiInfo> poiList = reverseGeoCodeResult.getPoiList();

            if (poiList != null && poiList.size() > 0) {
                PoiInfo poiInfo = poiList.get(0);

                KJLoger.log(TAG, "--address==" + poiInfo.name);
                title = poiInfo.name;
                PreferenceHelper.write(getActivity(), "UserInfo", "currentLocation", title);
                String currentLocation = PreferenceHelper.readString(getActivity(), "UserInfo", "currentLocation");//当前位置
                Common.currentLocation = currentLocation;
                KJLoger.log(TAG,"--currentLocation--"+currentLocation);

                titlTV.setText(title);

            }else {
                titlTV.setText("没有定位到当前位置");
            }

        }
}


    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            KJLoger.log(TAG,"--bdLocation执行了---");
            latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());

            latLongResult.latLongResult(latLng);

            if (latLng != null) {
                Common.latLng = latLng;
            }
            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            MyLocationData locData = new MyLocationData.Builder().accuracy(20).direction(100).latitude(bdLocation.getLatitude()).longitude(bdLocation.getLongitude()).build();
            baiduMap.setMyLocationData(locData);

            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(18.0f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            getNearShopDate();//显示附近的店铺
        }
    }
    public void getNearShopDate() {
        //显示附近的店铺
//        test();
            JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("longitude", latLng.longitude);//经度116.5691441688
            jsonObject.put("latitude", latLng.latitude);//纬度：39.8712016955
            jsonObject.put("Type", "JL");
            jsonObject.put("name", "");
            jsonObject.put("pageSize", 6);
            jsonObject.put("pageIndex", 1);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "GetNearShops", params, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "--请求获取附近的店铺-t-" + t);
                    Gson gson = new Gson();
                    WeiJieRespose weiJieRespose = gson.fromJson(t, WeiJieRespose.class);
                    test();
                    float a = 0;
                    float b = 0;
                    if (weiJieRespose.getResultState().equals("true")) {
                        List<ResultBody> resultBody = new ArrayList<ResultBody>();
                        for (int i = 0; i < 6; i++) {
                            ResultBody body = new ResultBody();
                            body.setC__Specific_location("双桥地铁站附近");
                            body.setId((10 + i) + "");
                            body.setName("张三" + i);
                            body.setNumber((1 + i) + "");
                            body.setSales("8888");
                            body.setYh("2.00");
                            body.setDetail("网络维修");
                            body.setLogo("http://192.168.0.68:8048/Logo/mdl.jpg");
                            /*if (i / 2 == 0) {
                                a = 1;
                            } else {
                                a = -1;
                            }*/
                            switch (i) {
                                case 0:
                                    a=0.05f;
                                    b=0.05f;
                                    break;
                                case 1:
                                    a=-0.15f;
                                    b=-0.05f;
                                    break;
                                case 2:
                                    a=0.09f;
                                    b=-0.02f;
                                    break;
                                case 3:
                                    a=0.15f;
                                    b=-0.15f;
                                    break;
                                case 4:
                                    a=0.25f;
                                    b=-0.55f;
                                    break;
                                case 5:
                                    a=0.35f;
                                    b=-0.05f;
                                    break;
                            }
                            body.setLongitude(Common.latLng.longitude + 0.001 *a);//设置经纬度
                            body.setLatitude(Common.latLng.latitude + 0.001  * b);
                            body.setDistances("300米以内");
                            body.setType("私人");
                            body.setGrade("5");
                            resultBody.add(body);

                        }

                        if (resultBody != null && resultBody.size() > 0) {
                            MyPoiOverlay overLay = new MyPoiOverLay(baiduMap, context);
                            baiduMap.setOnMarkerClickListener(overLay);
                            overLay.setData(resultBody);
                            overLay.addToMap();
                            overLay.zoomToSpan();
                        } else {
                            test();
                        }
                    } else {
                        test();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, " 获取附近的店铺信息 获取失败" + strMsg);
                    test();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPopWindow(String title,String content,LatLng latLng, final String Id,String logoUri,int markerHeight) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.popview, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();

        TextView titleTv = (TextView) view.findViewById(R.id.pop_title);
        TextView contentTv = (TextView) view.findViewById(R.id.pop_content);
        ImageView logoImg = (ImageView) view.findViewById(R.id.pop_image);
        KJBitmap kjBitmap = new KJBitmap();
        kjBitmap.display(logoImg, logoUri);
        kjBitmap.displayCacheOrDefult(logoImg,logoUri,R.drawable.tempview);
        contentTv.setMaxWidth((int) (width*0.6));
        titleTv.setText(title);
        contentTv.setText(content);
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromView(view);
        KJLoger.log(TAG,"--markerHeight--"+-markerHeight);
        final InfoWindow infoWindow = new InfoWindow(descriptor,latLng,-markerHeight+10,new InfoWindow.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick() {
                //跳转商品详情
                Intent intent = new Intent(getActivity(), ActWeiJieShopDetail.class);
                intent.putExtra("Id", Id);
                startActivity(intent);
                baiduMap.hideInfoWindow();
                KJLoger.log(TAG, "--infowindow点击了-Id=" + Id);
            }
        });
        baiduMap.showInfoWindow(infoWindow);

    }

    private class MyPoiOverLay extends MyPoiOverlay {
        /**
         * 构造函数
         *
         * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
         */
        public MyPoiOverLay(BaiduMap baiduMap,Context context) {
            super(baiduMap,context);
        }

        @Override
        public boolean onPoiClick(Marker marker,int i,View view) {
            BitmapDescriptor icon = marker.getIcon();
            Bitmap bitmap = icon.getBitmap();
            int height = bitmap.getHeight();
            ResultBody resultBody = getPoiResult().get(i);

            getPopWindow(resultBody.getName(),resultBody.getDetail(),resultBody.getLocation(),
                    resultBody.getId(),resultBody.getLogo(),height);

            return true;
        }

    }
    @Override
    public void onDestroy() {
        // 退出时销毁定位
        locationClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
        KJLoger.log(TAG, "--FgtWeijie--" + "onDestroy");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        KJLoger.log(TAG, "--FgtWeijie--" + "onSaveInstanceState");
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        KJLoger.log(TAG, "--FgtWeijie--" + "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        KJLoger.log(TAG, "--FgtWeijie--" + "onPause");
        mapView.onPause();
    }
}
