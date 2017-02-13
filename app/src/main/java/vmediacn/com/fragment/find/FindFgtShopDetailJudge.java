package vmediacn.com.fragment.find;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.allBean.find.JudgeBean;
import vmediacn.com.fragment.BaseFragment;
import vmediacn.com.util.StringCallBack;

/**
 * A simple {@link Fragment} subclass.
 * 店铺评价
 */
public class FindFgtShopDetailJudge extends BaseFragment {

    @BindView(id = R.id.find_shop_detail_shop_judge_groupId, click = true)
    private RadioGroup radioGroup;
    private String TAG = "vmediacn.com.fragment.find.FindFgtShopDetailJudge--";
    private ListView listView;
    private MyAdapter adapter;
    private List<JudgeBean> list = new ArrayList<>();
    private String shopId;

    public FindFgtShopDetailJudge() {
        // Required empty public constructor
    }


    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fgt_find_shop_detail_judge, viewGroup, false);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        initView(parentView);
    }

    private void initView(View parentView) {
        Bundle bu = getArguments();
        shopId = bu.getString("shopId");
        listView = (ListView) parentView.findViewById(R.id.find_shop_detail_shop_judge_listViewId);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                resetColor(checkedId);

                switch (checkedId) {

                    case R.id.find_shop_detail_shop_judge_allId:

                        getJudeListData(4);
                        break;

                    case R.id.find_shop_detail_shop_judge_goodJudgeId:
                        getJudeListData(1);
                        break;

                    case R.id.find_shop_detail_shop_judge_midJudgeId:
                        getJudeListData(2);
                        break;

                    case R.id.find_shop_detail_shop_judge_badJudgeId:
                        getJudeListData(3);
                        break;

                }
            }
        });
        addTestDate();
        getJudeListData(4);

    }

    private void resetColor(int checkedId) {

        int childCount = radioGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            radioButton.setTextColor(Color.parseColor("#666666"));
        }
        RadioButton checkButton = (RadioButton) getActivity().findViewById(checkedId);
        checkButton.setTextColor(Color.parseColor("#ff5d5d"));
    }

    private void addTestDate() {
        JudgeBean bean = new JudgeBean();
        bean.setName("悠悠");
        bean.setContent("麦当劳，永远值得信赖");
        bean.setTime("2016-4-14 14:14:45");

        JudgeBean bean1 = new JudgeBean();
        bean1.setName("华子");
        bean1.setContent("第一次网上购买，真心不错");
        bean1.setTime("2016-4-14 14:14:45");

        JudgeBean bean2 = new JudgeBean();
        bean2.setName("永不遗忘");
        bean2.setContent("还不错吧，很愉快的一次购物");
        bean2.setTime("2016-4-14 14:14:45");

        JudgeBean bean3 = new JudgeBean();
        bean3.setName("何家劲");
        bean3.setContent("没想下中的那么好");
        bean3.setTime("2016-4-14 14:14:45");
        list.clear();
        for (int i = 0; i < 4; i++) {
            list.add(bean);
            list.add(bean1);
            list.add(bean2);
            list.add(bean3);
        }
        adapter.notifyDataSetChanged();


    }

    public void getJudeListData(int type) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", shopId);
            object.put("Type",type);
            object.put("pageSize", "5");
            object.put("pageIndex", "1");
            HttpParams params = new HttpParams();
            params.putJsonParams(object.toString());
            KJHttp http = new KJHttp();//
            http.jsonPost(Common.MSUri + "Evaluationlist", params, new StringCallBack(getActivity()) {
                @Override
                public void onSuccess(String json) {
                    KJLoger.log(TAG, "--店铺评价列表回调成功--" + json);
                    addTestDate();

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHoler viewHoler = null;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.judge_item, null);
                viewHoler = new ViewHoler();
                viewHoler.timeTv = (TextView) convertView.findViewById(R.id.judge_item_timeId);
                viewHoler.nameTv = (TextView) convertView.findViewById(R.id.judge_item_nameId);
                viewHoler.contentTv = (TextView) convertView.findViewById(R.id.judge_item_contentId);
                convertView.setTag(viewHoler);

            } else {
                viewHoler = (ViewHoler) convertView.getTag();
            }
            JudgeBean bean = list.get(position);
            viewHoler.timeTv.setText(bean.getTime());
            viewHoler.nameTv.setText(bean.getName());
            viewHoler.contentTv.setText(bean.getContent());

            return convertView;
        }

        class ViewHoler {
            ImageView photoIv ;
            TextView timeTv;
            TextView contentTv;
            TextView nameTv;

        }
    }
}
