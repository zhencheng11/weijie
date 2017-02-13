package vmediacn.com.fragment.find;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.R;
import vmediacn.com.activity.weijie.ActWeiJieProductDetail;
import vmediacn.com.allBean.weijie.WSNearProductBody;
import vmediacn.com.allBean.weijie.WSNearProductRespose;
import vmediacn.com.fragment.BaseFragment;

/**
 *搜索结果
 */
public class FindFgtSearchResult extends BaseFragment {
    private List<WSNearProductBody> resultBodyList = new ArrayList<>();
    private ListView listview;
    private MyAdapter adapter;
    private String TAG = "vmediacn.com.activity.weijie.ActChooseMerchant--";

    public FindFgtSearchResult() {
        // Required empty public constructor
    }
    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fgt_search_result, viewGroup, false);
    }
    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        Bundle bun = getArguments();
        String jsonStr = bun.getString("jsonStr");
        KJLoger.log(TAG, "--jsonStr--" + jsonStr);

        listview = (ListView) parentView.findViewById(R.id.fgt_search_listViewId);
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KJLoger.log(TAG, " item click " + position);
                Intent intent = new Intent(getActivity(), ActWeiJieProductDetail.class);
                intent.putExtra("shopId", resultBodyList.get(position).getId());
                intent.putExtra("goodId", resultBodyList.get(position).getId());
                KJLoger.log(TAG, " shopId  " + resultBodyList.get(position).getId());
                startActivity(intent);
            }
        });
        addDate(jsonStr);
        //getProductTestData();
    }

    private void addDate(String jsonStr) {
        Gson gson = new Gson();
        WSNearProductRespose respose = gson.fromJson(jsonStr, WSNearProductRespose.class);
        List<WSNearProductBody> resultBody = respose.getResultBody();
        resultBodyList.clear();
        resultBodyList.addAll(resultBody);
        adapter.notifyDataSetChanged();

    }

    private void getProductTestData() {
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHodler viewHodler = null;
            if (convertView == null) {
                viewHodler = new ViewHodler();
                convertView = getActivity().getLayoutInflater().inflate(R.layout.wschoose_merchant_listview_item, null);
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
                viewHodler.handLay = (LinearLayout) convertView.findViewById(R.id.wschoose_merchant_item_Lay2Id);
                viewHodler.bottomLay = (LinearLayout) convertView.findViewById(R.id.wschoose_merchant_item_bottomLayId);

                convertView.setTag(viewHodler);

            }else {
                viewHodler = (ViewHodler) convertView.getTag();
            }
            WSNearProductBody productBody = resultBodyList.get(position);
            viewHodler.nameTV.setText(productBody.getName());
            viewHodler.yueShouTv.setText("月售"+productBody.getSales());
            viewHodler.priceTv.setText(productBody.getPrice()+"元");
            viewHodler.distanceTv.setText(productBody.getDistances());
            viewHodler.locationTv.setText(productBody.getC__Specific_location());
            viewHodler.handLay.setVisibility(View.GONE );
            //选择
            viewHodler.chooseTv.setTag(productBody);
            viewHodler.bottomLay.setVisibility(View.GONE);

            return convertView;
        }
        class ViewHodler{

            TextView nameTV,yueShouTv,distanceTv,locationTv,priceTv,countTv, chooseTv;
            ImageButton addBtn,deleBtn;
            ImageView logonImag;
            LinearLayout handLay;
            LinearLayout bottomLay;
        }
    }
}
