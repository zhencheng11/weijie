package vmediacn.com.activity.mine;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.SystemTool;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.util.MyUtil;
/*收货地址
*
* */
public class AddAddress extends BaseActivity {

    @BindView(id = R.id.act_add_address_save_addressId, click = true)//act_add_address_location_address
    private Button saveBtn;

    @BindView(id = R.id.act_add_address_location_address, click = true)//获取地址
    private EditText locationEt;

    @BindView(id = R.id.act_add_address_location_imagId, click = true)//跳转定位
    private ImageView locationIv;

    @BindView(id = R.id.act_add_address_location_arrowId, click = true)//跳转定位
    private ImageView locationArrowIv;

    private String TAG = "vmediacn.com.activity.mine.AddAddress--";
    private String adddress;

    private int is_default=0;//是否默认地址

    private String sex = "先生";
    private LocationClient locationClient;
    private EditText nameTv;
    private EditText mobileTv;
    private EditText streetTv;
    private String type;
    private String id;


    @Override
    public void setRootView() {
        setContentView(R.layout.act_add_address);

    }

    @Override
    public void initWidget() {
        super.initWidget();
        loadData();
        initLocation();

        if (Common.currentLocation == null) {
            locationEt.setText("没定位到附近位置");
        } else {
            locationEt.setText(Common.currentLocation);
        }

    }

    private void loadData() {
        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
            changeText(type);
            if (type.trim().equals("编辑收货地址".trim())) {
                String name = intent.getStringExtra("name");
                String mobile = intent.getStringExtra("mobile");
                String street = intent.getStringExtra("street");
                id = intent.getStringExtra("id");
                nameTv.setText(name);
                mobileTv.setText(mobile);
                streetTv.setText(street);
            }
        }
    }

    private void initLocation() {
        locationClient = new LocationClient(AddAddress.this);
        locationClient.registerLocationListener(new MyBdLocation());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(0);
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        locationClient.setLocOption(option);
        locationClient.start();
    }

    @Override
    public void initData() {
        super.initData();
        nameTv = (EditText) findViewById(R.id.act_add_address_nameId);
        mobileTv = (EditText) findViewById(R.id.act_add_address_phoneId);
        streetTv = (EditText) findViewById(R.id.act_add_address_detail_addressId);
        CheckBox checkBox = (CheckBox) findViewById(R.id.act_mall_shop_newaddress_check);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    KJLoger.log(TAG, " checked");
                    is_default = 1;
                } else {
                    KJLoger.log(TAG, " no--checked");
                    is_default = 0;
                }
            }
        });
        RadioGroup group = (RadioGroup) findViewById(R.id.act_add_address_groupId);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.act_add_address_manId:
                        sex = "先生";
                        break;
                    case R.id.act_add_address_womanId:
                        sex = "女士";
                        break;
                }
            }
        });


    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.act_add_address_save_addressId:
                KJLoger.log(TAG, "--save点击了");
                savaAdddress(type);
                break;

            case R.id.act_add_address_location_arrowId:


            case R.id.act_add_address_location_imagId:
                Intent intent = new Intent(AddAddress.this, AddressLocation.class);
                startActivityForResult(intent, 100);
                break;
        }
    }

    private void savaAdddress(String type) {
        if (verification()) {
            if (type.equals("添加收货地址")) {
                requstAddAddress();
            } else {
                requstEditAddress();
            }
        }

    }

    private boolean verification() {
        String phone = mobileTv.getText().toString().trim();
        if (nameTv.getText().toString().trim().isEmpty()) {
            note(AddAddress.this, "提示", "请输入收件人姓名", "确定");
            nameTv.requestFocus();
            return false;
        }
        if (phone.isEmpty()) {
            note(AddAddress.this, "提示", "请输入手机号", "确定");
            mobileTv.requestFocus();
            return false;
        }
        if (!MyUtil.isMobileNumber(phone)) {
            mobileTv.requestFocus();
            note(AddAddress.this, "提示：", "请输入正确的手机号", "确定");
            return false;
        }
        if (adddress == null||adddress.equals("")) {
            note(AddAddress.this, "提示", "请输入地址", "确定");
            locationEt.requestFocus();
            return false;
        }
        if (streetTv.getText().toString().trim().isEmpty()) {
            note(AddAddress.this, "提示", "请输入详细街道地址", "确定");
            streetTv.requestFocus();
            return false;
        }

        if(!SystemTool.checkNet(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "无网络，请检查网络", Toast.LENGTH_LONG).show();
            return false;
        }
        KJLoger.log(TAG,"--验证成功");

        return true;
    }

    private void requstEditAddress() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", mobileTv.getText().toString());//收件人手机号
            jsonObject.put("customer_id", Common.Usr_PhoneNumber);//用户名
            jsonObject.put("Address1", adddress);
            jsonObject.put("street", streetTv.getText().toString().trim());
            jsonObject.put("is_default", is_default);//1默认 0不默认
            jsonObject.put("name", nameTv.getText().toString());//收件人
            jsonObject.put("sex", sex);//性别
            jsonObject.put("id", id);//id

            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Editaddress", params, false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "编辑地址请求成功" + t);
                    JSONTokener jsonTokener = new JSONTokener(t);
                    try {
                        JSONObject jsonObject1 = (JSONObject) jsonTokener.nextValue();
                        String resultState = jsonObject1.getString("resultState");
                        String message = jsonObject1.getString("message");
                        if (resultState.equals("true")) {
                            setResult(100);
                            onBackPressed();
                        }
                        ShowToast(AddAddress.this, message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "编辑地址请求失败" + strMsg);
                    ShowToast(AddAddress.this, "保存失败");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void requstAddAddress() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", mobileTv.getText().toString());//收件人手机号
            jsonObject.put("customer_id", Common.Usr_PhoneNumber);//用户名
            jsonObject.put("Address1", adddress);
            jsonObject.put("street", streetTv.getText().toString().trim());
            jsonObject.put("is_default", "1");//1默认 0不默认
            jsonObject.put("name", nameTv.getText().toString());//收件人
            jsonObject.put("sex", sex);//性别

            /*jsonObject.put("mobile", "13717899174");//收件人手机号
            jsonObject.put("customer_id", Common.Usr_PhoneNumber);//用户名
            jsonObject.put("Address1", "北京市朝阳区豆各庄东村创意基地");
            jsonObject.put("street", "维圣传媒");
            jsonObject.put("is_default", is_default);//1默认 0不默认
            jsonObject.put("name", "丁兴向");//收件人
            jsonObject.put("sex", "男");//性别*/

            HttpParams params = new HttpParams();
            params.putJsonParams(jsonObject.toString());
            KJLoger.log(TAG, "--添加收货地址--json--" + jsonObject.toString());
            KJHttp http = new KJHttp();
            http.jsonPost(Common.MSUri + "Addaddress", params, false, new HttpCallBack() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    KJLoger.log(TAG, "添加地址请求成功" + t);
                    JSONTokener jsonTokener = new JSONTokener(t);
                    try {
                        JSONObject jsonObject1 = (JSONObject) jsonTokener.nextValue();
                        String resultState = jsonObject1.getString("resultState");
                        String message = jsonObject1.getString("message");
                        if (resultState.equals("true")) {
                            ShowToast(AddAddress.this, "添加成功");
                            setResult(100);
                            onBackPressed();
                        } else {
                            ShowToast(AddAddress.this, message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.log(TAG, "添加地址请求失败" + strMsg);
                    ShowToast(AddAddress.this, "保存失败");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            if (data != null) {
                String provice = data.getStringExtra("provice");
                String city = data.getStringExtra("city");
                String district = data.getStringExtra("district");
                String name = data.getStringExtra("name");
                locationEt.setText(name);
                KJLoger.log(TAG,"--provice---"+provice);
                KJLoger.log(TAG,"--city---"+city);
                KJLoger.log(TAG,"--district---"+district);
                KJLoger.log(TAG,"--name---"+name);
                if (provice.trim().equals(city.trim())) {
                    adddress = city + district + name;
                } else {
                    adddress = provice+city+district+name;
                }

            }
        }
    }

    private class MyBdLocation implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String province = bdLocation.getProvince();
            String city = bdLocation.getCity();
            String district = bdLocation.getDistrict();
            if (province.trim().equals(city.trim())) {
                adddress  = city+district+Common.currentLocation;
            }
            adddress = province+city+district+Common.currentLocation;
            KJLoger.log(TAG,"--MyBdLocation--adddress-"+adddress);
        }
    }
    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        locationClient.stop();
        super.onDestroy();
    }
}
