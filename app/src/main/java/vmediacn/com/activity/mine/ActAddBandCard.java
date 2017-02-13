package vmediacn.com.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import vmediacn.com.Common;
import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.allBean.mine.BankInfo;
import vmediacn.com.util.BankCardManager;
import vmediacn.com.util.MyUtil;
import vmediacn.com.util.StringCallBack;
/*
* 添加银行卡
*
* */
public class ActAddBandCard extends BaseActivity {

    //用户名
    @BindView(id = R.id.act_add_bandcard_userNameId)
    private EditText userNameEt;

    //银行卡号
    @BindView(id = R.id.act_add_bandcard_bandcardId)
    private EditText bankcartEt;

    //开户行
    @BindView(id = R.id.act_add_bandcard_bandNameId)
    private EditText banckNameEt;

    //下一步
    @BindView(id = R.id.act_add_bandcard_nextBtnId,click = true)
    private Button nextBtn;

    private String TAG = "--ActAddBandCard--";
    private BankCardManager bankCardManager;

    @Override
    public void setRootView() {

        setContentView(R.layout.act_add_band_card);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeText("添加银行卡");
        bankCardManager = new BankCardManager(ActAddBandCard.this);
        bankcartEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            private int allcount = 0;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                allcount = allcount + count;
                KJLoger.log(TAG, "s==" + s);
                KJLoger.log(TAG, "count==" + count);
                if (allcount == 19) {
                    //loadProvinceData();
                    getBankName(s);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.act_add_bandcard_nextBtnId:
                if (verification()) {
                    if (!Common.isPaypwd.equals("1")) {
                        Intent intent = new Intent(ActAddBandCard.this, ActSetPayPwdSet.class);
                        startActivity(intent);
                    } else {

                        BankInfo bankInfo = new BankInfo();
                        bankInfo.userName = userNameEt.getText().toString();
                        bankInfo.bankNumber = bankcartEt.getText().toString();
                        bankInfo.bankName = banckNameEt.getText().toString();

                        boolean sucucces = bankCardManager.savaBankCard(bankInfo);
                        if (sucucces) {

                            Intent intent = new Intent(ActAddBandCard.this, ActCharge.class);
                            intent.putExtra("userName",userNameEt.getText().toString());
                            intent.putExtra("bankNumber",bankcartEt.getText().toString());
                            intent.putExtra("bankName",banckNameEt.getText().toString());

                            startActivity(intent);
                        }

                    }

                }
                break;
        }
    }

    private boolean verification() {
        Context context = ActAddBandCard.this;
        String usName = userNameEt.getText().toString();
        String bankCode = bankcartEt.getText().toString();
        String banckName = banckNameEt.getText().toString();
        if (usName.equals("")) {
            userNameEt.requestFocus();
            note(context, "提示", "用户名不能为空", "确定");
            return false;
        }

        if (bankCode.equals("")) {
            bankcartEt.requestFocus();
            note(context, "提示", "银行卡号不能为空", "确定");
            return false;
        }
        if (!MyUtil.checkBankCard(bankCode)) {
            bankcartEt.requestFocus();
            note(context, "提示", "银行卡号不正确", "确定");
            return false;
        }
        if (banckName.equals("")) {
            bankcartEt.requestFocus();
            note(context, "提示", "请输入银行名字", "确定");
            return false;
        }
        return true;
    }

    private void getBankName(CharSequence s) {
        JSONObject object = new JSONObject();
        HttpParams params = new HttpParams();
        params.putJsonParams(object.toString());
        KJHttp http = new KJHttp();
        http.get("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo" +
                "=" + s + "&cardBinCheck=true", params, false, new HttpCallBack() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                KJLoger.log(TAG, "请求银行的名字失败" + strMsg);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                //{"bank":"ABC","validated":true,"cardType":"DC","stat":"ok","messages":[],"key":"6228480283235924117"}
                KJLoger.log(TAG, "请求银行的名字--" + t);
                JSONTokener jsonTokener = new JSONTokener(t);
                try {
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    String bankCode = jsonObject.getString("bank");
                    boolean validated = jsonObject.getBoolean("validated");
                    String state = jsonObject.getString("stat");
                    KJLoger.log(TAG, "bankCode==" + bankCode);
                    if (bankCode != null) {
                        JSONTokener jsonTokener1 = new JSONTokener(json);
                        JSONObject jsonObject1 = (JSONObject) jsonTokener1.nextValue();
                        String bankName = jsonObject1.getString(bankCode);
                        KJLoger.log(TAG, "bancName==" + bankName);
                        if (bankName != null) {
                            banckNameEt.setText(bankName);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void loadProvinceData() {

        KJHttp http = new KJHttp();
        try {
            HttpParams params = new HttpParams();
            params.putJsonParams("");
            http.jsonPost( "http://182.92.188.3:8081/Transfer/GetPro", params,false,  new StringCallBack(ActAddBandCard.this) {
                @Override
                public void onSuccess(String json) {
                    KJLoger.log(TAG, "请求省份--" + json);
                    JSONTokener token = new JSONTokener(json);
                    try {
                        JSONObject object  = (JSONObject) token.nextValue();
                        String resultState = object.getString("resultState");
                        if (resultState.equals("true")) {
                            String resultBody = object.getString("resultbody");
                            JSONArray array = new JSONArray(resultBody);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject Pro = (JSONObject) array.get(i);
                                String getPro = Pro.getString("Pro");
                            }

                        } else {
                            String message =object.getString("message");
                            Toast.makeText(ActAddBandCard.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    KJLoger.log(TAG, "请求省份失败" + strMsg);
                }
            });
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    private String json ="{\n" +
            "  \"SRCB\": \"深圳农村商业银行\", \n" +
            "  \"BGB\": \"广西北部湾银行\", \n" +
            "  \"SHRCB\": \"上海农村商业银行\", \n" +
            "  \"BJBANK\": \"北京银行\", \n" +
            "  \"WHCCB\": \"威海市商业银行\", \n" +
            "  \"BOZK\": \"周口银行\", \n" +
            "  \"KORLABANK\": \"库尔勒市商业银行\", \n" +
            "  \"SPABANK\": \"平安银行\", \n" +
            "  \"SDEB\": \"顺德农商银行\", \n" +
            "  \"HURCB\": \"湖北省农村信用社\", \n" +
            "  \"WRCB\": \"无锡农村商业银行\", \n" +
            "  \"BOCY\": \"朝阳银行\", \n" +
            "  \"CZBANK\": \"浙商银行\", \n" +
            "  \"HDBANK\": \"邯郸银行\", \n" +
            "  \"BOC\": \"中国银行\", \n" +
            "  \"BOD\": \"东莞银行\", \n" +
            "  \"CCB\": \"中国建设银行\", \n" +
            "  \"ZYCBANK\": \"遵义市商业银行\", \n" +
            "  \"SXCB\": \"绍兴银行\", \n" +
            "  \"GZRCU\": \"贵州省农村信用社\", \n" +
            "  \"ZJKCCB\": \"张家口市商业银行\", \n" +
            "  \"BOJZ\": \"锦州银行\", \n" +
            "  \"BOP\": \"平顶山银行\", \n" +
            "  \"HKB\": \"汉口银行\", \n" +
            "  \"SPDB\": \"上海浦东发展银行\", \n" +
            "  \"NXRCU\": \"宁夏黄河农村商业银行\", \n" +
            "  \"NYNB\": \"广东南粤银行\", \n" +
            "  \"GRCB\": \"广州农商银行\", \n" +
            "  \"BOSZ\": \"苏州银行\", \n" +
            "  \"HZCB\": \"杭州银行\", \n" +
            "  \"HSBK\": \"衡水银行\", \n" +
            "  \"HBC\": \"湖北银行\", \n" +
            "  \"JXBANK\": \"嘉兴银行\", \n" +
            "  \"HRXJB\": \"华融湘江银行\", \n" +
            "  \"BODD\": \"丹东银行\", \n" +
            "  \"AYCB\": \"安阳银行\", \n" +
            "  \"EGBANK\": \"恒丰银行\", \n" +
            "  \"CDB\": \"国家开发银行\", \n" +
            "  \"TCRCB\": \"江苏太仓农村商业银行\", \n" +
            "  \"NJCB\": \"南京银行\", \n" +
            "  \"ZZBANK\": \"郑州银行\", \n" +
            "  \"DYCB\": \"德阳商业银行\", \n" +
            "  \"YBCCB\": \"宜宾市商业银行\", \n" +
            "  \"SCRCU\": \"四川省农村信用\", \n" +
            "  \"KLB\": \"昆仑银行\", \n" +
            "  \"LSBANK\": \"莱商银行\", \n" +
            "  \"YDRCB\": \"尧都农商行\", \n" +
            "  \"CCQTGB\": \"重庆三峡银行\", \n" +
            "  \"FDB\": \"富滇银行\", \n" +
            "  \"JSRCU\": \"江苏省农村信用联合社\", \n" +
            "  \"JNBANK\": \"济宁银行\", \n" +
            "  \"CMB\": \"招商银行\", \n" +
            "  \"JINCHB\": \"晋城银行JCBANK\", \n" +
            "  \"FXCB\": \"阜新银行\", \n" +
            "  \"WHRCB\": \"武汉农村商业银行\", \n" +
            "  \"HBYCBANK\": \"湖北银行宜昌分行\", \n" +
            "  \"TZCB\": \"台州银行\", \n" +
            "  \"TACCB\": \"泰安市商业银行\", \n" +
            "  \"XCYH\": \"许昌银行\", \n" +
            "  \"CEB\": \"中国光大银行\", \n" +
            "  \"NXBANK\": \"宁夏银行\", \n" +
            "  \"HSBANK\": \"徽商银行\", \n" +
            "  \"JJBANK\": \"九江银行\", \n" +
            "  \"NHQS\": \"农信银清算中心\", \n" +
            "  \"MTBANK\": \"浙江民泰商业银行\", \n" +
            "  \"LANGFB\": \"廊坊银行\", \n" +
            "  \"ASCB\": \"鞍山银行\", \n" +
            "  \"KSRB\": \"昆山农村商业银行\", \n" +
            "  \"YXCCB\": \"玉溪市商业银行\", \n" +
            "  \"DLB\": \"大连银行\", \n" +
            "  \"DRCBCL\": \"东莞农村商业银行\", \n" +
            "  \"GCB\": \"广州银行\", \n" +
            "  \"NBBANK\": \"宁波银行\", \n" +
            "  \"BOYK\": \"营口银行\", \n" +
            "  \"SXRCCU\": \"陕西信合\", \n" +
            "  \"GLBANK\": \"桂林银行\", \n" +
            "  \"BOQH\": \"青海银行\", \n" +
            "  \"CDRCB\": \"成都农商银行\", \n" +
            "  \"QDCCB\": \"青岛银行\", \n" +
            "  \"HKBEA\": \"东亚银行\", \n" +
            "  \"HBHSBANK\": \"湖北银行黄石分行\", \n" +
            "  \"WZCB\": \"温州银行\", \n" +
            "  \"TRCB\": \"天津农商银行\", \n" +
            "  \"QLBANK\": \"齐鲁银行\", \n" +
            "  \"GDRCC\": \"广东省农村信用社联合社\", \n" +
            "  \"ZJTLCB\": \"浙江泰隆商业银行\", \n" +
            "  \"GZB\": \"赣州银行\", \n" +
            "  \"GYCB\": \"贵阳市商业银行\", \n" +
            "  \"CQBANK\": \"重庆银行\", \n" +
            "  \"DAQINGB\": \"龙江银行\", \n" +
            "  \"CGNB\": \"南充市商业银行\", \n" +
            "  \"SCCB\": \"三门峡银行\", \n" +
            "  \"CSRCB\": \"常熟农村商业银行\", \n" +
            "  \"SHBANK\": \"上海银行\", \n" +
            "  \"JLBANK\": \"吉林银行\", \n" +
            "  \"CZRCB\": \"常州农村信用联社\", \n" +
            "  \"BANKWF\": \"潍坊银行\", \n" +
            "  \"ZRCBANK\": \"张家港农村商业银行\", \n" +
            "  \"FJHXBC\": \"福建海峡银行\", \n" +
            "  \"ZJNX\": \"浙江省农村信用社联合社\", \n" +
            "  \"LZYH\": \"兰州银行\", \n" +
            "  \"JSB\": \"晋商银行\", \n" +
            "  \"BOHAIB\": \"渤海银行\", \n" +
            "  \"CZCB\": \"浙江稠州商业银行\", \n" +
            "  \"YQCCB\": \"阳泉银行\", \n" +
            "  \"SJBANK\": \"盛京银行\", \n" +
            "  \"XABANK\": \"西安银行\", \n" +
            "  \"BSB\": \"包商银行\", \n" +
            "  \"JSBANK\": \"江苏银行\", \n" +
            "  \"FSCB\": \"抚顺银行\", \n" +
            "  \"HNRCU\": \"河南省农村信用\", \n" +
            "  \"COMM\": \"交通银行\", \n" +
            "  \"XTB\": \"邢台银行\", \n" +
            "  \"CITIC\": \"中信银行\", \n" +
            "  \"HXBANK\": \"华夏银行\", \n" +
            "  \"HNRCC\": \"湖南省农村信用社\", \n" +
            "  \"DYCCB\": \"东营市商业银行\", \n" +
            "  \"ORBANK\": \"鄂尔多斯银行\", \n" +
            "  \"BJRCB\": \"北京农村商业银行\", \n" +
            "  \"XYBANK\": \"信阳银行\", \n" +
            "  \"ZGCCB\": \"自贡市商业银行\", \n" +
            "  \"CDCB\": \"成都银行\", \n" +
            "  \"HANABANK\": \"韩亚银行\", \n" +
            "  \"CMBC\": \"中国民生银行\", \n" +
            "  \"LYBANK\": \"洛阳银行\", \n" +
            "  \"GDB\": \"广东发展银行\", \n" +
            "  \"ZBCB\": \"齐商银行\", \n" +
            "  \"CBKF\": \"开封市商业银行\", \n" +
            "  \"H3CB\": \"内蒙古银行\", \n" +
            "  \"CIB\": \"兴业银行\", \n" +
            "  \"CRCBANK\": \"重庆农村商业银行\", \n" +
            "  \"SZSBK\": \"石嘴山银行\", \n" +
            "  \"DZBANK\": \"德州银行\", \n" +
            "  \"SRBANK\": \"上饶银行\", \n" +
            "  \"LSCCB\": \"乐山市商业银行\", \n" +
            "  \"JXRCU\": \"江西省农村信用\", \n" +
            "  \"ICBC\": \"中国工商银行\", \n" +
            "  \"JZBANK\": \"晋中市商业银行\", \n" +
            "  \"HZCCB\": \"湖州市商业银行\", \n" +
            "  \"NHB\": \"南海农村信用联社\", \n" +
            "  \"XXBANK\": \"新乡银行\", \n" +
            "  \"JRCB\": \"江苏江阴农村商业银行\", \n" +
            "  \"YNRCC\": \"云南省农村信用社\", \n" +
            "  \"ABC\": \"中国农业银行\", \n" +
            "  \"GXRCU\": \"广西省农村信用\", \n" +
            "  \"PSBC\": \"中国邮政储蓄银行\", \n" +
            "  \"BZMD\": \"驻马店银行\", \n" +
            "  \"ARCU\": \"安徽省农村信用社\", \n" +
            "  \"GSRCU\": \"甘肃省农村信用\", \n" +
            "  \"LYCB\": \"辽阳市商业银行\", \n" +
            "  \"JLRCU\": \"吉林农信\", \n" +
            "  \"URMQCCB\": \"乌鲁木齐市商业银行\", \n" +
            "  \"XLBANK\": \"中山小榄村镇银行\", \n" +
            "  \"CSCB\": \"长沙银行\", \n" +
            "  \"JHBANK\": \"金华银行\", \n" +
            "  \"BHB\": \"河北银行\", \n" +
            "  \"NBYZ\": \"鄞州银行\", \n" +
            "  \"LSBC\": \"临商银行\", \n" +
            "  \"BOCD\": \"承德银行\", \n" +
            "  \"SDRCU\": \"山东农信\", \n" +
            "  \"NCB\": \"南昌银行\", \n" +
            "  \"TCCB\": \"天津银行\", \n" +
            "  \"WJRCB\": \"吴江农商银行\", \n" +
            "  \"CBBQS\": \"城市商业银行资金清算中心\", \n" +
            "  \"HBRCU\": \"河北省农村信用社\"\n" +
            "}";
}
