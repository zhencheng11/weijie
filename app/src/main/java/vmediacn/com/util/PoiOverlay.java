package vmediacn.com.util;

/**
 * Created by Administrator on 2016/4/6.
 */

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
import com.baidu.mapapi.search.poi.PoiResult;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.R;

/**
 * 用于显示poi的overly
 */
public class PoiOverlay extends OverlayManager {

    private List<ImageView> listImage = new ArrayList<>();

    private Context context;
    private static final int MAX_POI_SIZE = 10;

    private PoiResult mPoiResult = null;

    /**
     * 构造函数
     *
     * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
     */
    public PoiOverlay(BaiduMap baiduMap, Context context) {
        super(baiduMap);
        this.context = context;
    }

    /**
     * 设置POI数据
     *
     * @param poiResult 设置POI数据
     */
    public void setData(PoiResult poiResult) {
        this.mPoiResult = poiResult;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {
        if (mPoiResult == null || mPoiResult.getAllPoi() == null) {
            return null;
        }
        List<OverlayOptions> markerList = new ArrayList<OverlayOptions>();
        int markerSize = 0;
        for (int i = 0; i < mPoiResult.getAllPoi().size()
                && markerSize < MAX_POI_SIZE; i++) {
            if (mPoiResult.getAllPoi().get(i).location == null) {
                continue;
            }
            markerSize++;
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            //2016.3.21改
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.mipmap.shop_loaction);
            listImage.add(imageView);
           /* markerList.add(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_mark"
                            + markerSize + ".png")).extraInfo(bundle)
                    .position(mPoiResult.getAllPoi().get(i).location).title(mPoiResult.getAllPoi().get(i).name));*/
            markerList.add(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromView(imageView)).extraInfo(bundle)
                    .position(mPoiResult.getAllPoi().get(i).location).title(mPoiResult.getAllPoi().get(i).name));



        }
        return markerList;
    }

    /**
     * 获取该 PoiOverlay 的 poi数据
     *
     * @return
     */
    public PoiResult getPoiResult() {
        return mPoiResult;
    }

    /**
     * 覆写此方法以改变默认点击行为
     *
     * @param i 被点击的poi在
     *          {@link PoiResult#getAllPoi()} 中的索引
     * @return
     */
    public boolean onPoiClick(Marker marker, int i, View view) {
//        if (mPoiResult.getAllPoi() != null
//                && mPoiResult.getAllPoi().get(i) != null) {
//            Toast.makeText(BMapManager.getInstance().getContext(),
//                    mPoiResult.getAllPoi().get(i).name, Toast.LENGTH_LONG)
//                    .show();
//        }
        return false;
    }

    @Override
    public final boolean onMarkerClick(Marker marker) {
        if (!mOverlayList.contains(marker)) {
            BitmapDescriptor icon = marker.getIcon();

            return false;
        }
        if (marker.getExtraInfo() != null) {
            return onPoiClick(marker, marker.getExtraInfo().getInt("index"), listImage.get(marker.getExtraInfo().getInt("index")));
        }
        return false;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        // TODO Auto-generated method stub
        return false;
    }
}