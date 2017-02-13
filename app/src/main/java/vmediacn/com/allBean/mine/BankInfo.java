package vmediacn.com.allBean.mine;

/**
 * 银行卡
 * Created by DingXingXiang on 2016/5/4.
 */
public class BankInfo {
    public int _id;
    public String bankName;
    public String bankNumber;
    public String userName;

    public BankInfo() {
    }

    public BankInfo(String bankName, String bankNumber, String userName) {
        this.bankName = bankName;
        this.bankNumber = bankNumber;
        this.userName = userName;
    }
}
