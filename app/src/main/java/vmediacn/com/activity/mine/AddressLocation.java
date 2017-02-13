package vmediacn.com.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.util.MyDialog;

/*
*
* 收货地址定位
*
* */
public class AddressLocation extends BaseActivity implements OnGetGeoCoderResultListener,OnGetSuggestionResultListener{
    private String TAG = "[-AddressLocation-]--";
    private GeoCoder geoCoder;
    private LocationClient locationClient;
    private LatLng latLng;

    //建议搜索
    private SuggestionSearch suggestionSearch = null;

    @BindView(id = R.id.address_location_searcNocontentId)//周边检索的ListView
    private TextView geoNocotentTv;

    @BindView(id = R.id.address_location_listViewId)//周边检索的ListView
    private ListView geolistView;

    @BindView(id = R.id.address_location_searchlistViewId)//搜索结果的ListView
    private ListView searchListView;

    @BindView(id = R.id.address_location_searchEtId,click = true)//搜索框
    private EditText searchEt;

    @BindView(id = R.id.address_location_deleteId,click = true)//删除内容
    private ImageButton deleBtn;

    @BindView(id = R.id.address_location_backId,click = true)//删除内容
    private ImageView backImag;

    private List<PoiInfo> poiList;
    private MyAdapter adapter ;
    private SearchPoiAdapter searchPoiAdapter;
    private String city;

    private static List<SuggestionResult.SuggestionInfo> searchPoiList;
    private Context context;
    private String province;
    private String district;


    @Override
    public void setRootView() {

        setContentView(R.layout.act_address_location);
    }

    @Override
    public void initData() {
        super.initData();
        context = this;
        searchEt.setEnabled(false);
        MyDialog.showDialog(context);

        poiList = new ArrayList();
        searchPoiList = new ArrayList<>();



        adapter = new MyAdapter();
        searchPoiAdapter= new SearchPoiAdapter();

        geolistView.setAdapter(adapter);
        searchListView.setAdapter(searchPoiAdapter);

        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);

        suggestionSearch = SuggestionSearch.newInstance();
        suggestionSearch.setOnGetSuggestionResultListener(this);

        locationClient = new LocationClient(AddressLocation.this);
        locationClient.registerLocationListener(new MyLocationClient());

        initOptionAndStart();

        initEvent();

    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.address_location_deleteId:
                searchEt.setText("");
                break;

            case R.id.address_location_backId://返回上层页面
                onBackPressed();
                break;
        }
    }

    private void initOptionAndStart() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(0);
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        locationClient.setLocOption(option);
        locationClient.start();
    }

    private void initEvent() {
        searchEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchEt.getText().toString().trim().length() > 0) {
                    suggestionSearch.requestSuggestion(new SuggestionSearchOption().
                            keyword(searchEt.getText().toString().trim()).city(city));
                }
            }
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() > 0) {
                    suggestionSearch.requestSuggestion(new SuggestionSearchOption().keyword(s.toString().trim()).city(city));
                } else {
                    if (searchPoiList != null) {
                        searchPoiList.clear();
                    }
                    showGeoOrSearch(GEO);
                    hideSoftinput(context);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        geolistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiInfo poiInfo = poiList.get(position);
                String name = poiInfo.name;
                Intent intent = new Intent();
                intent.putExtra("provice", province);
                intent.putExtra("city", city);
                intent.putExtra("district", district);
                intent.putExtra("name", name);
                setResult(100, intent);
                onBackPressed();
            }
        });
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SuggestionResult.SuggestionInfo suggestionInfo = searchPoiList.get(position);
                String district = suggestionInfo.district;
                String name = suggestionInfo.key;
                String city = suggestionInfo.city;
                Intent intent = new Intent();
                intent.putExtra("provice", province);
                intent.putExtra("city", city);
                intent.putExtra("district", district);
                intent.putExtra("name", name);
                setResult(100, intent);
                if (searchListView.getVisibility() == View.VISIBLE) {
                    searchListView.setVisibility(View.GONE);
                    onBackPressed();
                }
                onBackPressed();
            }
        });
    }
    /**
     * 隐藏软键盘
     *
     *
     */
    private void hideSoftinput(Context mContext) {
        InputMethodManager manager = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager.isActive()) {
            manager.hideSoftInputFromWindow(searchEt.getWindowToken(), 0);
        }
    }
    @Override
    public void onBackPressed() {
        if (searchListView.getVisibility() == View.VISIBLE) {
            showGeoOrSearch(GEO);
        } else {
            this.finish();
        }
    };
    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        locationClient.stop();
        geoCoder.destroy();
        suggestionSearch.destroy();
        super.onDestroy();
    }
    private void showGeoOrSearch(int state) {
        switch (state) {
            case 0://Search
                searchListView.setVisibility(View.VISIBLE);
                geolistView.setVisibility(View.GONE);
                break;
            case 1://geo
                searchListView.setVisibility(View.GONE);
                geolistView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private int SEARCH = 0;
    private int GEO = 1;
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        KJLoger.log(TAG, "--onGetGeoCodeResult执行了--");
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        KJLoger.log(TAG, "--onGetReverseGeoCodeResult执行了--");
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(AddressLocation.this, "抱歉，未能找到结果", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.getPoiList() != null) {

            KJLoger.log(TAG, "----result.getAddress();----" + result.getAddress());
            List<PoiInfo> poiList0 = result.getPoiList();
            MyDialog.closeDialog();
            geolistView.setEmptyView(geoNocotentTv);

            poiList.clear();
            poiList.addAll(poiList0);
            adapter.notifyDataSetChanged();
            for (int i = 0; i < poiList0.size(); i++) {
                PoiInfo poiInfo = poiList0.get(i);
            }
        } else {
            Toast.makeText(AddressLocation.this, "没有周边热点", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        KJLoger.log(TAG, "--onGetSuggestionResult--执行了--");
        //建议搜索
        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
            return;
        }
        if (searchEt.getText().toString().trim().length() > 0) {
            if (searchPoiList == null) {
                searchPoiList = new ArrayList<>();
            }
            searchPoiList.clear();
            List<SuggestionResult.SuggestionInfo> allSuggestions = suggestionResult.getAllSuggestions();
            int size = allSuggestions.size();
            KJLoger.log(TAG, "--size----"+size);
            searchPoiList.addAll(allSuggestions);
            updateCityPoiListAdapter();
        }
    }
    // 刷新当前城市兴趣地点列表界面的adapter
    private void updateCityPoiListAdapter() {
        if (searchPoiAdapter == null) {
            searchPoiAdapter = new SearchPoiAdapter();
            searchListView.setAdapter(searchPoiAdapter);
        } else {
            searchPoiAdapter.notifyDataSetChanged();
        }
        showGeoOrSearch(SEARCH);
    }

    private class MyLocationClient implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            KJLoger.log(TAG,"bdLocation.getCity()="+bdLocation.getCity());
            latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            city = bdLocation.getCity();
            province = bdLocation.getProvince();
            district = bdLocation.getDistrict();
            String addrStr = bdLocation.getAddrStr();
            KJLoger.log(TAG,"--province--"+ province);
            KJLoger.log(TAG,"--district--"+ district);
            KJLoger.log(TAG,"--addrStr--"+addrStr);
            searchEt.setEnabled(true);
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return poiList.size();
        }

        @Override
        public Object getItem(int position) {
            return poiList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodler viewHodler = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.address_location_item, null);
                viewHodler = new ViewHodler();
                viewHodler.imageView = (ImageView) convertView.findViewById(R.id.address_location_item_imageId);
                viewHodler.nameTv = (TextView) convertView.findViewById(R.id.address_location_item_nameId);
                viewHodler.detailTv = (TextView) convertView.findViewById(R.id.address_location_item_detailId);
                viewHodler.curentTv = (TextView) convertView.findViewById(R.id.address_location_item_currentId);
                convertView.setTag(viewHodler);

            } else {
                viewHodler = (ViewHodler) convertView.getTag();
            }
            if (position == 0) {
                viewHodler.curentTv.setVisibility(View.VISIBLE);
                viewHodler.imageView.setImageResource(R.mipmap.place);
            } else {
                viewHodler.curentTv.setVisibility(View.GONE);
                viewHodler.imageView.setImageResource(R.mipmap.place_dark_grey);
            }
            PoiInfo poiInfo = poiList.get(position);
            String name = poiInfo.name;
            String address = poiInfo.address;
            String city = poiInfo.city;
            viewHodler.nameTv.setText(name);

            viewHodler.detailTv.setText(address);
            return convertView;
        }

        class ViewHodler {
            ImageView imageView;
            TextView nameTv;
            TextView detailTv;
            TextView curentTv;
        }

    }
    public class SearchPoiAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (searchPoiList != null) {
                return searchPoiList.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            if (searchPoiList != null) {
                return searchPoiList.get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CityPoiHolder holder = null;
            if (convertView == null) {
                holder = new CityPoiHolder();
                convertView = getLayoutInflater().inflate(R.layout.mapview_location_poi_lv_item, null);
                holder.tvMLIPoiName = (TextView) convertView.findViewById(R.id.tvMLIPoiName);
                holder.tvMLIPoiAddress = (TextView) convertView.findViewById(R.id.tvMLIPoiAddress);
                convertView.setTag(holder);
            } else {
                holder = (CityPoiHolder) convertView.getTag();
            }
            SuggestionResult.SuggestionInfo suggestionInfo = searchPoiList.get(position);

            holder.tvMLIPoiName.setText(suggestionInfo.key);
            holder.tvMLIPoiAddress.setText(suggestionInfo.city + suggestionInfo.district);
            return convertView;
        }
        private class CityPoiHolder {
             TextView tvMLIPoiName, tvMLIPoiAddress;
        }

    }

}
