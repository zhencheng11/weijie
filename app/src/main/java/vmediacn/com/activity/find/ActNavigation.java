package vmediacn.com.activity.find;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.OpenClientUtil;

import org.kymjs.kjframe.ui.BindView;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.ui.BikingRouteOverlay;
import vmediacn.com.ui.DrivingRouteOverlay;
import vmediacn.com.ui.TransitRouteOverlay;
import vmediacn.com.ui.WalkingRouteOverlay;
import vmediacn.com.util.OverlayManager;
/*
* 店铺导航
* */
public class ActNavigation extends BaseActivity implements BaiduMap.OnMapClickListener,
        OnGetRoutePlanResultListener ,OnGetGeoCoderResultListener{


    private PlanNode stNode;
    private PlanNode enNode;

    @Override
    public void setRootView() {
        setContentView(R.layout.act_navigation);
    }

    private LatLng endLatLng ;
    private GeoCoder geoCoder;
    // 处理搜索按钮响应
    @BindView(id = R.id.start,click = true)
    private EditText editSt;

    @BindView(id = R.id.end,click = true)
    private EditText editEn;
    // 浏览路线节点相关
    Button mBtnPre = null; // 上一个节点
    Button mBtnNext = null; // 下一个节点
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    private TextView popupText = null; // 泡泡view

    // 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    // 如果不处理touch事件，则无需继承，直接使用MapView即可
    MapView mMapView = null;    // 地图View
    BaiduMap mBaidumap = null;
    // 搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    @Override
    public void initWidget() {
        super.initWidget();
        changeText("导航路线");
        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);

        editSt.setText(Common.currentLocation);
        editEn.setText("豆各庄人民政府");
        geoCoder.geocode(new GeoCodeOption().city("北京").address(editEn.getText().toString()));

        // 设置起终点信息，对于tranist search 来说，城市名无意义
        stNode = PlanNode.withCityNameAndPlaceName("北京", Common.currentLocation);
        enNode = PlanNode.withCityNameAndPlaceName("北京", editEn.getText().toString().trim());
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode).to(enNode));
    }
    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */
    public void searchButtonProcess(View v) {
        // 重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();

        // 实际使用中请对起点终点城市进行正确的设定
        if (v.getId() == R.id.drive) {
            mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode).to(enNode));
        } else if (v.getId() == R.id.transit) {
            mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(stNode).city("北京").to(enNode));
        } else if (v.getId() == R.id.walk) {
            mSearch.walkingSearch((new WalkingRoutePlanOption())
                    .from(stNode).to(enNode));
        } else if (v.getId() == R.id.bike) {
            mSearch.bikingSearch((new BikingRoutePlanOption())
                    .from(stNode).to(enNode));
        }
    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        if (route == null || route.getAllStep() == null) {
            return;
        }
        if (nodeIndex == -1 && v.getId() == R.id.pre) {
            return;
        }
        // 设置节点索引
        if (v.getId() == R.id.next) {
            if (nodeIndex < route.getAllStep().size() - 1) {
                nodeIndex++;
            } else {
                return;
            }
        } else if (v.getId() == R.id.pre) {
            if (nodeIndex > 0) {
                nodeIndex--;
            } else {
                return;
            }
        }
        // 获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        } else if (step instanceof BikingRouteLine.BikingStep) {
            nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
            nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        // 移动节点至中心
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(ActNavigation.this);
        popupText.setBackgroundResource(R.drawable.popup1);
        popupText.setTextColor(0xFF000000);
        popupText.setGravity(Gravity.CENTER_VERTICAL);
        popupText.setPadding(5,5,5,5);
        popupText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

    }

    /**
     * 切换路线图标，刷新地图使其生效
     * 注意： 起终点图标使用中心对齐.
     */
    public void changeRouteIcon(View v) {
        if (routeOverlay == null) {
            return;
        }
        if (useDefaultIcon) {
            ((Button) v).setText("自定义起终点图标");
            Toast.makeText(this,
                    "将使用系统起终点图标",
                    Toast.LENGTH_SHORT).show();

        } else {
            ((Button) v).setText("系统起终点图标");
            Toast.makeText(this,
                    "将使用自定义起终点图标",
                    Toast.LENGTH_SHORT).show();

        }
        useDefaultIcon = !useDefaultIcon;
        routeOverlay.removeFromMap();
        routeOverlay.addToMap();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(ActNavigation.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();

            //显示popwindow
            LatLng location = result.getRouteLines().get(0).getTerminal().getLocation();
            BitmapDescriptor terminalMarker = overlay.getTerminalMarker();
            Bitmap bitmap = terminalMarker.getBitmap();
            int height = bitmap.getHeight();
            showBaiduMapInfoWindow(height, result, location);
        }

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(ActNavigation.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();

            //显示popwindow
            LatLng location = result.getRouteLines().get(0).getTerminal().getLocation();
            BitmapDescriptor terminalMarker = overlay.getTerminalMarker();
            Bitmap bitmap = terminalMarker.getBitmap();
            int height = bitmap.getHeight();
            showBaiduMapInfoWindow(height, result, location);
        }
    }

    @Override
    public void onGetDrivingRouteResult(final DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(ActNavigation.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            final DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
            routeOverlay = overlay;
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();

            LatLng location = result.getRouteLines().get(0).getTerminal().getLocation();
            BitmapDescriptor terminalMarker = overlay.getTerminalMarker();
            Bitmap bitmap = terminalMarker.getBitmap();
            int height = bitmap.getHeight();
            showBaiduMapInfoWindow(height,result,location);
        }
    }

    private void showBaiduMapInfoWindow(int height, final SearchResult result, final LatLng endLatLng) {
        View view =getLayoutInflater().inflate(R.layout.navigation_item, null);

        InfoWindow infoWindow = new InfoWindow(view, endLatLng, -height+10);
        ImageView imageView = (ImageView) view.findViewById(R.id.navigation_shopNavigationBtnId);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NaviParaOption paraOption = new NaviParaOption();
                paraOption.startName(editSt.getText().toString());
                paraOption.endName(editEn.getText().toString());

                paraOption.startPoint(Common.latLng);
                paraOption.endPoint(endLatLng);

                try {
                    if (result instanceof DrivingRouteResult) {

                        BaiduMapNavigation.openBaiduMapNavi(paraOption, ActNavigation.this);

                    } else if (result instanceof WalkingRouteResult) {

                        BaiduMapNavigation.openBaiduMapWalkNavi(paraOption, ActNavigation.this);

                    }else if (result instanceof TransitRouteResult) {

                        BaiduMapNavigation.openBaiduMapNavi(paraOption, ActNavigation.this);

                    }else if (result instanceof BikingRouteResult) {

                        BaiduMapNavigation.openBaiduMapBikeNavi(paraOption, ActNavigation.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showDialog();
                }


            }
        });
        mBaidumap.showInfoWindow(infoWindow);
    }
    /**
     * 提示未安装百度地图app或app版本过低
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(ActNavigation.this);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
        if (bikingRouteResult == null || bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(ActNavigation.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (bikingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = bikingRouteResult.getRouteLines().get(0);
            BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaidumap);
            routeOverlay = overlay;
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData(bikingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();

            //显示popwindow
            LatLng location = bikingRouteResult.getRouteLines().get(0).getTerminal().getLocation();
            BitmapDescriptor terminalMarker = overlay.getTerminalMarker();
            Bitmap bitmap = terminalMarker.getBitmap();
            int height = bitmap.getHeight();
            showBaiduMapInfoWindow(height, bikingRouteResult, location);
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(ActNavigation.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();

            return;
        }
       endLatLng = result.getLocation();

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }


    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.shop_loaction);
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.shop_loaction);
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.shop_loaction);
        }
    }

    private class MyBikingRouteOverlay extends BikingRouteOverlay {
        public  MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.shop_loaction);
        }


    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mSearch.destroy();
        mMapView.onDestroy();
        super.onDestroy();
    }

}
