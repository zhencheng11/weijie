package vmediacn.com.util;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.R;
import vmediacn.com.allBean.weijie.ResultBody;

/**
 * Created by Administrator on 2016/4/7.
 * 根据服务器显示店铺的overlay
 */
public class MyPoiOverlay extends OverlayManager {
    private List<ImageView> listImage = new ArrayList<>();

    private Context context;
    private static final int MAX_POI_SIZE = 10;

    private List<ResultBody> mPoiResult = null;
    private ImageView imageView;

    /**
     * 通过一个BaiduMap 对象构造
     *
     * @param baiduMap
     */
    public MyPoiOverlay(BaiduMap baiduMap,Context context) {
        super(baiduMap);
        this.context = context;

    }
    /**
     * 设置POI数据
     *
     * @param poiResult 设置POI数据
     */
    public void setData(List<ResultBody> poiResult) {
        this.mPoiResult = poiResult;
    }
    @Override
    public List<OverlayOptions> getOverlayOptions() {
        if (mPoiResult == null || mPoiResult.size() == 0) {
            return null;
        }
        List<OverlayOptions> markerList = new ArrayList<OverlayOptions>();
        int markerSize = 0;
        for (int i = 0; i < mPoiResult.size() && markerSize < MAX_POI_SIZE; i++) {
            if (mPoiResult.get(i).getLocation() == null) {
                continue;
            }
            markerSize++;
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            imageView = new ImageView(context);
            imageView.setImageResource(R.mipmap.shop_loaction);

            listImage.add(imageView);

            markerList.add(new MarkerOptions()
                      .icon(BitmapDescriptorFactory.fromView(imageView))
                      .extraInfo(bundle)
                      .position(mPoiResult.get(i).getLocation())
                      .title(mPoiResult.get(i).getName()));



        }
        return markerList;
    }
    /**
     * 获取该 PoiOverlay 的 poi数据
     *
     * @return
     */
    public List<ResultBody> getPoiResult() {
        return mPoiResult;
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!mOverlayList.contains(marker)) {
            BitmapDescriptor icon = marker.getIcon();

            return false;
        }
        if (marker.getExtraInfo() != null) {
            return onPoiClick(marker, marker.getExtraInfo().getInt("index"),
                    listImage.get(marker.getExtraInfo().getInt("index")));
        }
        return false;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        return false;
    }
    public boolean onPoiClick(Marker marker, int i, View view) {
        return false;
    }

    public int getMarkerHeight() {

        return imageView.getHeight();
    }
}
