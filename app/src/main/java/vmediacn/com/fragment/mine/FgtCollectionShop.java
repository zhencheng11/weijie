package vmediacn.com.fragment.mine;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.allBean.weijie.ResultBody;
import vmediacn.com.fragment.BaseFragment;
import vmediacn.com.util.StringCallBack;

/**
 * A simple {@link Fragment} subclass.
 * 收藏--店铺
 */
public class FgtCollectionShop extends BaseFragment{

    private List<ResultBody> resultBodyList = new ArrayList<>();
    private String TAG = "Collection--";
    private MyAdapter adapter;

    @BindView(id = R.id.fgt_collection_shop_listViewId)
    ListView listView;
    private LatLng latLng;

    public FgtCollectionShop() {

        // Required empty public constructor
    }
    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fgt_collection_shop, null);
    }
    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        latLng = Common.latLng;
        getCollectionListData();
        getTest();

    }
    private void getCollectionListData() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Tel", Common.Usr_PhoneNumber);
            jsonObject.put("longitude",latLng.longitude);
            jsonObject.put("latitude", latLng.latitude);
            jsonObject.put("pageSize", 6);
            jsonObject.put("pageIndex", 1);
            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJLoger.log(TAG, "--收藏列表提交Json---" + jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Collectionlist", params,false, new StringCallBack(getActivity()) {
                @Override
                public void onSuccess(String json) {
                    KJLoger.log(TAG, "--收藏列表请求数据---" + json);

                    JSONTokener jsonTokener = new JSONTokener(json);
                    try {
                        JSONObject jsonObject1 = (JSONObject) jsonTokener.nextValue();
                        String message = jsonObject1.getString("message");
                        if (jsonObject1.getString("resultState").equals("true")) {

                        }
                        showToast(getActivity(), message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                viewHolder.distanceTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_distanceId);//wschoose_merchant_item_discribleTvId
                viewHolder.productCountTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_all_productId);//wschoose_merchant_item_discribleTvId
                viewHolder.yueShouTv = (TextView) convertView.findViewById(R.id.wschoose_merchant_item_yueShouTvId);//wschoose_merchant_item_discribleTvId
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.wschoose_merchant_item_shop_imageViewId);//wschoose_merchant_item_discribleTvId
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

            KJBitmap bitmap = new KJBitmap();
            bitmap.display(viewHolder.imageView, logo);
            viewHolder.nameTv.setText(name);
            viewHolder.distanceTv.setText(distances);
            viewHolder.productCountTv.setText(number+"件商品");

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
}
