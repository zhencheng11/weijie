package vmediacn.com.activity.find;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.allBean.find.History;
import vmediacn.com.fragment.find.FindFgtSearchResult;
import vmediacn.com.ui.ItemContainer;
import vmediacn.com.util.HistoryDBManager;
import vmediacn.com.util.StringCallBack;
/*搜索
* */
public class ActSearch extends BaseActivity {
    private List<String> titleList = new ArrayList<>();
    private String TAG = "vmediacn.com.activity.find.ActSearch--";

    //分类
    private Spinner fenLeiSpinner = null;// 声明一个Spinner组件
    private ArrayAdapter<CharSequence> fenLeiAdapter = null;// 声明一个ArrayAdapter来适配
    private List<CharSequence> fenLeiList = null;// 声明一个放置数据的List
    private Context context;

    //输入框的内容
    @BindView(id = R.id.head_title_view_search_EtId)
    private EditText editText;

    //返回按钮
    @BindView(id = R.id.head_title_search_backIgId,click = true)
    private ImageView backImag;

    //搜索
    @BindView(id = R.id.head_title_view_search_searchIBd,click = true)
    private ImageButton searchIb;

    //取消
    @BindView(id = R.id.head_title_view_search_menuId,click = true)
    private TextView cancleTv;

    @BindView(id = R.id.act_search_gridViewId)
    private GridView reSouGridView;

    @BindView(id = R.id.act_search_listViewId)
    private ListView historyListView;

    //搜索历史
    private HistoryDBManager myHistoryDBManager;
    private List<Map<String, String>> list;
    private SimpleAdapter arrayAdapter;
    private List<String> resouList = new ArrayList<>();

    @BindView(id = R.id.act_search_clearhistoryLayId,click = true)
    LinearLayout clearLay;

    private Double longitude = 116.569144168;
    private Double latitude = 39.8712016955;
    private LatLng latLng = Common.latLng;

    @BindView(id = R.id.head_title_view_search_remenLaycontainerId)
    private ItemContainer container;

    @BindView(id = R.id.act_search_resultLayId)
    private FrameLayout frameLayout;
    private MyResouAdapter adapter;

    @Override
    public void setRootView() {
        setContentView(R.layout.act_search);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        //latLng = new LatLng(latitude, longitude);
        getReSouData();
        historyListView.setAdapter(arrayAdapter);

        adapter = new MyResouAdapter();
        reSouGridView.setAdapter(adapter);


        refrushData();
        historyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPop(position);
                return false;
            }
        });
        reSouGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = resouList.get(position);
                editText.setText(key);
                requestSearchData(key, latLng);
                addToHistory(key);//添加到历史记录
            }
        });
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> map = list.get(position);
                String key = map.get("title");
                editText.setText(key);

                requestSearchData(key, latLng);
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        myHistoryDBManager = new HistoryDBManager(this);
        context = ActSearch.this;
        fenLeiSpinnerDate(context);

        String[] hotTitels = {"跑步鞋","面膜","健身器","连衣裙","运动服","速冻水饺","香烟","红毯山"};

        list = new ArrayList<>();
        arrayAdapter = new SimpleAdapter(context, list,android.R.layout.simple_list_item_1,
                new String[] { "title"},new int[]{android.R.id.text1});

    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);


        switch (v.getId()) {
            case R.id.head_title_view_search_searchIBd://搜索
                String key = editText.getText().toString();
                requestSearchData(key, latLng);

                addToHistory(key);//添加到历史记录
                break;

            case R.id.head_title_search_backIgId://返回
                if (frameLayout.getVisibility() == View.VISIBLE) {
                    frameLayout.setVisibility(View.GONE);
                } else {
                    onBackPressed();
                }

                break;

            case R.id.head_title_view_search_menuId://取消
                if (frameLayout.getVisibility() == View.VISIBLE) {
                    frameLayout.setVisibility(View.GONE);
                } else {
                    onBackPressed();
                }
                break;

            case R.id.act_search_clearhistoryLayId://清除历史
                getdeleallPop();

                break;
        }
    }

    private void addToHistory(String key) {
        if (!key.isEmpty()) {
            History history = new History();
            history.setTitle(key);
            myHistoryDBManager.add(history);
            refrushData();
        }

    }
    private void requestSearchData(String key,LatLng latLng) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", key);
            jsonObject.put("Type", "JL");
            KJLoger.log(TAG, "--搜索关键字--" + key);
            jsonObject.put("longitude", latLng.longitude);
            jsonObject.put("latitude", latLng.latitude);
            jsonObject.put("pageSize", 6);
            jsonObject.put("pageIndex", 1);

            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "GetNearGoods", params, false, new StringCallBack(ActSearch.this) {

                @Override
                public void onSuccess(String json) {

                    JSONTokener tokener = new JSONTokener(json);
                    try {
                        JSONObject jsonObject1 = (JSONObject) tokener.nextValue();
                        String resultState = jsonObject1.getString("resultState");
                        String message = jsonObject1.getString("message");
                        if (resultState.equals("true")) {
                            String resultBody = jsonObject1.getString("resultBody");
                            JSONArray jsonArray = new JSONArray(resultBody);
                            if (jsonArray.length() > 0) {
                                frameLayout.setVisibility(View.VISIBLE);
                                FindFgtSearchResult findFgtSearchResult = new FindFgtSearchResult();
                                Bundle bundle = new Bundle();
                                bundle.putString("jsonStr", json);
                                findFgtSearchResult.setArguments(bundle);
                                changeFragment(R.id.act_search_resultLayId, findFgtSearchResult);
                            } else {
                                ShowToast(ActSearch.this,"没有数据");
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void fenLeiSpinnerDate(Context context) {
        fenLeiSpinner = new Spinner(context);// 创建Spinner对象
        fenLeiList = Arrays.asList(new CharSequence[]{"商品", "店铺", "服務"});// 设置年龄段数组并最终转换为List类型
        fenLeiAdapter = new ArrayAdapter<CharSequence>(context,
                android.R.layout.simple_spinner_item, fenLeiList);// 实例化ArrayAdapter
        fenLeiAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);// 设置列表项显示风格
        fenLeiSpinner.setAdapter(fenLeiAdapter);// 设置显示信息
        LinearLayout layout = (LinearLayout)findViewById(R.id.act_search_spinnerLayId);
        TextView ageLabel=new TextView(context);
        layout.addView(ageLabel);
        layout.addView(fenLeiSpinner);

    }

    public void getReSouData() {
        JSONObject jsonObject = new JSONObject();
        HttpParams params = new HttpParams();
        params.putJsonParams(jsonObject.toString());
        KJHttp http = new KJHttp();
        http.get(Common.MSUri + "ReSou", new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                KJLoger.log(TAG, "--热搜索商品请求数据成功" + t);
                JSONTokener tokener = new JSONTokener(t);
                try {
                    JSONObject jsonObject1 = (JSONObject) tokener.nextValue();
                    if (jsonObject1.getString("resultState").equals("true")) {
                        String resultBody = jsonObject1.getString("resultBody");
                        JSONArray array = new JSONArray(resultBody);
                        if (array.length() > 0) {
                            List<String> stringList = new ArrayList<String>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = (JSONObject) array.get(i);
                                String name = object.getString("name");
                                stringList.add(name);
                            }
                            resouList.clear();
                            resouList.addAll(stringList);
                            adapter.notifyDataSetChanged();
                            //addResouDate(stringList);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                KJLoger.log(TAG, "--热搜索商品请求数据失败" + strMsg);
            }
        });


    }

    private void addResouDate(List<String> titleList) {
        if (container.getChildCount()>0){
            container.removeAllViews();
        }

        for (int i = 0; i < titleList.size(); i++) {
            TextView textView = new TextView(context);
            textView.setText(titleList.get(i));
            textView.setTextSize(22);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 5, 5, 5);
            textView.setBackgroundResource(R.drawable.searchtv_bg);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10 , 0 , 0 ,0 );
            container.addView(textView);
        }
    }
    private void showPop(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("删除？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<History> historyList = myHistoryDBManager.query();
                History history = historyList.get(position);
                myHistoryDBManager.deleteItem(history);
                refrushData();
            }
        }).setNegativeButton("取消",null).create().show();

    }
    /*刷新数据*/
    private void refrushData() {
        List<History> historyList = myHistoryDBManager.query();
        if (historyList.size() == 0 || historyList == null) {
            clearLay.setVisibility(View.GONE);
        } else {
            clearLay.setVisibility(View.VISIBLE);
        }
        list.clear();
        for (int i = 0; i < historyList.size(); i++) {
            HashMap<String,String> map = new HashMap<>();
            map.put("title", historyList.get(i).getTitle());
            list.add(map);
        }
        arrayAdapter.notifyDataSetChanged();
    }

    public void getdeleallPop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("清除全部历史记录");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<History> historyList = myHistoryDBManager.query();
                myHistoryDBManager.clearHistoty(historyList);
                refrushData();
            }
        }).setNegativeButton("取消",null).create().show();

    }

    private class MyResouAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return resouList.size();
        }

        @Override
        public Object getItem(int position) {
            return resouList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodler viewHodler = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.resou_lay, null);
                viewHodler = new ViewHodler();
                viewHodler.textView = (TextView) convertView.findViewById(R.id.resou_tvId);
                convertView.setTag(viewHodler);

            } else {
                viewHodler = (ViewHodler) convertView.getTag();
            }
            String name = resouList.get(position);
            viewHodler.textView.setText(name);

            return convertView;
        }

        private class ViewHodler {
            TextView textView;
        }
    }
}
