package vmediacn.com.fragment.find;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.kymjs.kjframe.ui.BindView;

import java.util.List;

import vmediacn.com.R;
import vmediacn.com.activity.find.ActNavigation;
import vmediacn.com.activity.weijie.ActWeiJieShopDetail;
import vmediacn.com.allBean.weijie.WSShopDetailRespose;
import vmediacn.com.allBean.weijie.WSShopDetilResultBody;
import vmediacn.com.fragment.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * 店铺详情描述
 */
public class FindFgtShopDetailXiangQ extends BaseFragment {
    @BindView(id = R.id.find_shop_xiangQing_locationLayId,click = true)
    LinearLayout locationLay;

    //位置
    @BindView(id = R.id.find_shop_xiangQing_locationId,click = true)
    private TextView locationTv;

    //配送时间
    @BindView(id = R.id.find_shop_xiangQing_peiSongTimeId,click = true)
    private TextView peiSongTimeTv;

    //描述
    @BindView(id = R.id.find_shop_xiangQing_brefId,click = true)
    private TextView briefTv;



    public FindFgtShopDetailXiangQ() {
        // Required empty public constructor
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fgt_find_shop_detail_detail, viewGroup, false);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        addData();
    }

    private void addData() {
        ActWeiJieShopDetail detail = (ActWeiJieShopDetail) getActivity();
        String json = detail.getJson();
        if (json != null) {
            Gson gson = new Gson();
            WSShopDetailRespose respose = gson.fromJson(json, WSShopDetailRespose.class);
            if (respose.getResultState().equals("true")) {
                List<WSShopDetilResultBody> resultBodyList = respose.getResultBody();
                if (resultBodyList != null && resultBodyList.size() > 0) {
                    WSShopDetilResultBody resultBody = resultBodyList.get(0);

                    briefTv.setText(resultBody.getBrief());
                    String gtime = resultBody.getGtime();
                    String ktime = resultBody.getKtime();
                    peiSongTimeTv.setText("配送时间："+ ktime +"  -  "+ gtime);
                    locationTv.setText(resultBody.getLocation());
                }
            }
        }
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.find_shop_xiangQing_locationId:
            case R.id.find_shop_xiangQing_locationLayId://跳转导航页
                Intent intent = new Intent(getActivity(), ActNavigation.class);
                startActivity(intent);
                break;
        }
    }
}
