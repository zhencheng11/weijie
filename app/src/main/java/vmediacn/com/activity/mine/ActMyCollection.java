package vmediacn.com.activity.mine;

import android.support.design.widget.TabLayout;

import org.kymjs.kjframe.ui.KJFragment;

import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.fragment.mine.FgtCollectionProduct;
import vmediacn.com.fragment.mine.FgtCollectionShop;
/*
* 我的收藏
*
* */
public class ActMyCollection extends BaseActivity {

    private String TAG = "Collection--";

    private KJFragment fragment;
    private double latitude;
    private double longitude;

    @Override
    public void setRootView() {
        setContentView(R.layout.act_my_collection);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeText("我的收藏");

        fragment = new FgtCollectionShop();

        changeFragment(R.id.my_collection_fragementId, fragment);

        TabLayout tabLayotut = (TabLayout) findViewById(R.id.my_collection_tabLayoutId);
        TabLayout.Tab tab1 = tabLayotut.newTab();
        tab1.setText("店铺");

        TabLayout.Tab tab2 = tabLayotut.newTab();
        tab2.setText("商品");

        tabLayotut.addTab(tab1, true);
        tabLayotut.addTab(tab2);

        tabLayotut.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragment = null;
                switch (tab.getPosition()) {

                    case 0:
                        fragment = new FgtCollectionShop();
                        break;

                    case  1:

                        fragment = new FgtCollectionProduct();
                        break;

                }
                changeFragment(R.id.my_collection_fragementId,fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
