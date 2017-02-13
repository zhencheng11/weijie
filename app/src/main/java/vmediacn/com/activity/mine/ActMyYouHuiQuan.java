package vmediacn.com.activity.mine;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.kymjs.kjframe.ui.BindView;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
/*
* 我的优惠券
* */
public class ActMyYouHuiQuan extends BaseActivity {

    @BindView(id = R.id.shouCang_ListViewId)
    private ListView listView;
    private List<ShouCangBean> list = new ArrayList<>();

    private MyAdapter adapter = new MyAdapter();
    private String TAG = "ActMyYouHuiQuan----";
    @Override
    public void setRootView() {
        setContentView(R.layout.act_my_you_hui_quan);

    }

    @Override
    public void initWidget() {
        super.initWidget();


        changeText("我的优惠券");
        listView.setAdapter(adapter);

        getTestData();
    }

    public void getTestData() {
        ShouCangBean bean = new ShouCangBean();
        bean.content = "¥ 50 元代金券";
        bean.time = "有限时间：2016.4.25-2017.8.8";
        bean.res = R.drawable.coupon_blue;

        ShouCangBean bean1 = new ShouCangBean();
        bean1.content = "¥ 150 元代金券";
        bean1.time = "有限时间：2016.4.25-2019.4.8";
        bean1.res = R.drawable.coupon_green;

        ShouCangBean bean2 = new ShouCangBean();
        bean2.content = "¥ 250 元代金券";
        bean2.time = "有限时间：2016.4.25-2018.8.8";
        bean2.res = R.drawable.coupon_red;
        list.clear();
        list.add(bean1);
        list.add(bean2);
        list.add(bean2);
        adapter.notifyDataSetChanged();
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
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.act_shoucang_item, null);
                viewHolder = new ViewHolder();
                viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.act_shoucang_item_reLayId);
                viewHolder.contentTv = (TextView) convertView.findViewById(R.id.act_shoucang_item_contenId);
                viewHolder.timeTv = (TextView) convertView.findViewById(R.id.act_shoucang_item_timeId);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ShouCangBean bean = list.get(position);
            viewHolder.relativeLayout.setBackgroundResource(bean.res);
            viewHolder.contentTv.setText(bean.content);
            viewHolder.timeTv.setText(bean.time);

            return convertView;
        }

        private class ViewHolder {

            RelativeLayout relativeLayout;
            TextView contentTv;
            TextView timeTv;

        }
    }

    public class ShouCangBean {
        private int res;
        private String content;
        private String time;

    }
}
