package vmediacn.com.allBean.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;

import vmediacn.com.Common;
import vmediacn.com.R;

/**
 * Created by Administrator on 2016/4/14.
 */
public class MyCityChoicePop extends PopupWindow implements View.OnClickListener {
    private String TAG = "vmediacn.com.allBean.mine.MyCityChoicePop--";
    private Button province,city,name,put;
    private ArrayList<Bean> provinceList = new ArrayList<>();
    private ArrayList<Bean>  cityList = new ArrayList<>();
    private ArrayList<String>  areaList = new ArrayList<>();
    private String provinceS="";
    private String cityS="";
    private String areaS="";
    private MyBankListener listener;
    private Activity activity;
    public MyCityChoicePop(Activity activity) {
        super(activity);
        this.activity = activity;
        InitView(activity);
    }
    private void InitView(Activity context){
        View view = LayoutInflater.from(context).inflate(R.layout.item_citychoice_pop, null);
        province = (Button) view.findViewById(R.id.item_citychoice_province);
        city = (Button) view.findViewById(R.id.item_citychoice_city);
        name = (Button) view.findViewById(R.id.item_citychoice_name);
        put = (Button) view.findViewById(R.id.item_citychoice_put);
        province.setOnClickListener(this);
        city.setOnClickListener(this);
        name.setOnClickListener(this);
        put.setOnClickListener(this);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        getProvince();
    }
    public void setMyBankListener(MyBankListener listener){
        this.listener = listener;


    }
    private void showPrvinceMenu() {
        ListView listView = new ListView(activity);
        listView.setAdapter(provinceAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("请选择");
        final AlertDialog dialog = builder.setView(listView).create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Bean provincebean = provinceList.get(position);
                int pcode = provincebean.getId();
                getCity(pcode);
                province.setText(provincebean.getName());
                provinceS=provincebean.getName();
                city.setText("请选择市");
                name.setText("请选择县");
                dialog.dismiss();
            }
        });
    }
    private void showCityMenu() {
        ListView listView = new ListView(activity);
        listView.setAdapter(CityAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("请选择");
        final AlertDialog dialog = builder.setView(listView).create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Bean provincebean = cityList.get(position);
                int pcode = provincebean.getId();
                getArea(pcode);
                city.setText(provincebean.getName());
                cityS=provincebean.getName();
                name.setText("请选择县");
                dialog.dismiss();
            }
        });
    }
    private void showArea(final ArrayList<String> areaList){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setSingleChoiceItems(areaList.toArray(new CharSequence[]{}), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name.setText(areaList.get(which));
                areaS = areaList.get(which);
                dialog.dismiss();
            }
        });
        builder.create().show();

    }
    private void getProvince(){
        KJHttp http = new KJHttp();
        http.get(Common.MSUri + "Province", new HttpCallBack() {
            @Override
            public void onSuccess(String json) {
                KJLoger.log(TAG, "获取省请求成功" + json);
                JSONTokener tokener = new JSONTokener(json);
                try {
                    JSONObject object = (JSONObject) tokener.nextValue();
                    String resultState = object.getString("resultState");
                    if (resultState.equals("true")) {
                        String resultBody = object.getString("resultBody");
                        JSONArray array = new JSONArray(resultBody);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object1 = (JSONObject) array.get(i);
                            int id = object1.getInt("id");
                            String name = object1.getString("name");
                            Bean bean = new Bean();
                            bean.setId(id);
                            bean.setName(name);
                            provinceList.add(bean);
                        }
                        provinceAdapter.notifyDataSetChanged();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                KJLoger.log(TAG, "获取省请求失败" + strMsg);
            }
        });

    }
    private void getCity(int id){
        KJHttp http = new KJHttp();
        JSONObject object = new JSONObject();
        try {
            object.put("id",id);
            HttpParams params = new HttpParams();
            params.putJsonParams(object.toString());
            http.jsonPost(Common.MSUri + "city", params, new HttpCallBack() {
                @Override
                public void onSuccess(String json) {
                    JSONTokener tokener  = new JSONTokener(json);
                    try {
                        JSONObject object = (JSONObject) tokener.nextValue();
                        String resultState = object.getString("resultState");
                        if (resultState.equals("true")){
                            String resultBody  = object.getString("resultBody");
                            JSONArray array = new JSONArray(resultBody);
                            for (int i=0;i<array.length();i++){
                                JSONObject object1 = (JSONObject) array.get(i);
                                int id = object1.getInt("id");
                                String name = object1.getString("name");
                                Bean bean = new Bean();
                                bean.setId(id);
                                bean.setName(name);
                                cityList.add(bean);
                            }
                            CityAdapter.notifyDataSetChanged();
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
    private void getArea(int id){
        KJHttp http = new KJHttp();
        JSONObject object = new JSONObject();
        try {
            object.put("id",id);
            HttpParams params = new HttpParams();
            params.putJsonParams(object.toString());
            http.jsonPost(Common.MSUri + "district", params, new HttpCallBack() {
                @Override
                public void onSuccess(String json) {
                    JSONTokener tokener  = new JSONTokener(json);
                    try {
                        JSONObject object = (JSONObject) tokener.nextValue();
                        String resultState = object.getString("resultState");
                        if (resultState.equals("true")){
                            String resultBody  = object.getString("resultBody");
                            JSONArray array = new JSONArray(resultBody);
                            for (int i=0;i<array.length();i++){
                                JSONObject object1 = (JSONObject) array.get(i);
                                String name = object1.getString("name");
                                areaList.add(name);
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
    private BaseAdapter provinceAdapter = new BaseAdapter() {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            if (convertView == null) {
                textView = (TextView) View.inflate(
                        activity,
                        R.layout.simple_list_item_my, null);
            } else {
                textView = (TextView) convertView;
            }
            Bean province = provinceList.get(position);
            textView.setText(province.getName());
            return textView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return provinceList == null ? null : provinceList.get(position);
        }

        @Override
        public int getCount() {
            return provinceList == null ? 0 : provinceList.size();
        }
    };
    private BaseAdapter CityAdapter = new BaseAdapter() {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            if (convertView == null) {
                textView = (TextView) View.inflate(
                        activity,
                        R.layout.simple_list_item_my, null);
            } else {
                textView = (TextView) convertView;
            }
            Bean province = cityList.get(position);
            textView.setText(province.getName());
            return textView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return cityList == null ? null : cityList.get(position);
        }

        @Override
        public int getCount() {
            return cityList == null ? 0 : cityList.size();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_citychoice_province:
                showPrvinceMenu();
                break;
            case R.id.item_citychoice_city:
                if (cityList.size() > 0) {
                    showCityMenu();
                } else {
                    showToas("请选择省份");
                }
                break;
            case R.id.item_citychoice_name:
                if (areaList.size() > 0) {
                    showArea(areaList);
                } else {
                    showToas("请选择市");
                }
                break;
            case R.id.item_citychoice_put:
                listener.onSuccess(new String(provinceS+cityS+areaS));
                break;
        }

    }
    private void showToas(String s) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
    }

    private class Bean {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

