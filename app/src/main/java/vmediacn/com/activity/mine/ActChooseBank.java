package vmediacn.com.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.List;

import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.allBean.mine.BankInfo;
import vmediacn.com.util.BankCardManager;
/*
* 选择银行卡
*
* */
public class ActChooseBank extends BaseActivity {

    @BindView(id = R.id.act_yue_chargeLayId, click = true)
    private LinearLayout chargeLay;

    @BindView(id = R.id.act_choose_bank_listViewId)
    private SwipeMenuListView listView;
    private List<BankInfo> bankCardList;
    private boolean hasChoose;

    private String TAG = "--ActChooseBank--";
    private SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem openItem = new SwipeMenuItem(ActChooseBank.this);
            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
            openItem.setWidth(dp2px(90));
            openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
            openItem.setTitleColor(Color.WHITE);
            openItem.setIcon(R.mipmap.ic_delete);
            menu.addMenuItem(openItem);
        }
    };
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
    @Override
    public void setRootView() {

        setContentView(R.layout.act_choose_bankcard);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeText("选择银行卡");
        Intent intent = getIntent();
        if (intent != null) {
            hasChoose = intent.getBooleanExtra("hasChoose", true);
        }
        View view = getLayoutInflater().inflate(R.layout.addbank_lay, null);
        listView.addFooterView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//添加银行卡
                Intent intent = new Intent(ActChooseBank.this, ActAddBandCard.class);
                startActivity(intent);

            }
        });
        final BankCardManager bankCardManager = new BankCardManager(ActChooseBank.this);
        bankCardList = bankCardManager.getBankCardList();

        final MyAdater adater = new MyAdater();
        listView.setAdapter(adater);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int i1) {
                if (i1 == 0) {
                    KJLoger.log(TAG, "onMenuItemClick-position-" + i + "--i1--" + i1);
                    BankInfo bankInfo = bankCardList.get(i);
                    String userName = bankInfo.userName;
                    String bankName = bankInfo.bankName;
                    String bankNumber = bankInfo.bankNumber;
                    int result = bankCardManager.deleteItem(bankName, bankNumber, userName);
                    KJLoger.log(TAG, "result==" + result);
                    if (result != 0) {
                        bankCardList.remove(i);
                        adater.notifyDataSetChanged();
                    }


                }
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BankInfo bankInfo = bankCardList.get(position);
                String bankName = bankInfo.bankName;
                String bankNumber = bankInfo.bankNumber;
                if (hasChoose) {
                    Intent intent = new Intent();
                    intent.putExtra("bankName", bankName);
                    intent.putExtra("bankNumber", bankNumber);
                    setResult(100, intent);
                    onBackPressed();
                } else {
                    Intent intent1 = new Intent(ActChooseBank.this, ActCharge.class);
                    intent1.putExtra("bankName", bankName);
                    intent1.putExtra("bankNumber", bankNumber);
                    startActivity(intent1);
                }

            }
        });
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.act_yue_chargeLayId:

                break;
        }
    }

    private class MyAdater extends BaseAdapter {

        @Override
        public int getCount() {
            return bankCardList.size();
        }

        @Override
        public Object getItem(int position) {
            return bankCardList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.act_choose_bankcard_item, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.act_choose_bankcard_nameId);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.act_choose_bankcard_chekedId);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                viewHolder.imageView.setVisibility(View.VISIBLE);

            }
            BankInfo bankInfo = bankCardList.get(position);
            String bankNumber = bankInfo.bankNumber;
            String subNum = bankNumber.substring(15);
            viewHolder.textView.setText(bankInfo.bankName + " (" + subNum + " )");

            return convertView;
        }
        private class ViewHolder{
            TextView textView;
            ImageView imageView;
        }
    }
}
