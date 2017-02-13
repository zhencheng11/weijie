package vmediacn.com.activity.mine;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.List;

import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.allBean.mine.BankInfo;
import vmediacn.com.util.BankCardManager;
/*
* 充值
*
* */
public class ActCharge extends BaseActivity {

    @BindView(id = R.id.act_chagee_relayId,click = true)
    private RelativeLayout relativeLayout;

    @BindView(id = R.id.act_chagee_editId)
    private EditText editText;

    @BindView(id = R.id.act_chagee_nextBtnId,click = true)
    private Button nextBtn;

    @BindView(id = R.id.act_charge_bankNameId)
    private TextView bankNameTv;

    private String TAG = "--ActCharge--";

    @Override
    public void setRootView() {
        setContentView(R.layout.act_charge);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeText("充值");
        BankCardManager bankCardManager = new BankCardManager(ActCharge.this);
        List<BankInfo> bankCardList = bankCardManager.getBankCardList();
        Intent intent = getIntent();
        if (intent != null) {
            String userName = intent.getStringExtra("userName");
            String bankName = intent.getStringExtra("bankName");
            String bankNumber = intent.getStringExtra("bankNumber");
            String subNum = bankNumber.substring(15);
            bankNameTv.setText(bankName + "(" + subNum + ")");

        }
        for (int i = 0; i < bankCardList.size(); i++) {
            BankInfo bankInfo = bankCardList.get(i);
            String bankName = bankInfo.bankName;
            String userName = bankInfo.userName;
            String bankNumber = bankInfo.bankNumber;

            KJLoger.log(TAG, "userName=" + userName);
            KJLoger.log(TAG, "bankName=" + bankName);
            KJLoger.log(TAG, "bankNumber=" + bankNumber);

        }
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.act_chagee_relayId:
                Intent intent = new Intent(ActCharge.this, ActChooseBank.class);
                intent.putExtra("hasChoose", true);
                startActivityForResult(intent, 100);
                break;

            case R.id.act_chagee_nextBtnId:
                Intent intent1 = new Intent(ActCharge.this, ActChargeInputPassword.class);
                intent1.putExtra("chageMoney", editText.getText().toString());
                startActivity(intent1);

                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            String bankName = data.getStringExtra("bankName");
            String bankNumber = data.getStringExtra("bankNumber");
            bankNameTv.setText(bankName);

        }
    }
}
