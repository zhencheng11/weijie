package vmediacn.com.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.allBean.mine.BankInfo;

/**
 * Created by DingXingXiang on 2016/5/4.
 * 银行卡信息存储管理类
 */
public class BankCardManager {
    private String TAG = "BankCardManager";
    SQLiteDatabase db;
    BankDBHelper helper;

    public BankCardManager(Context context) {
        helper = new BankDBHelper(context);
        db = helper.getWritableDatabase();
    }
    public boolean savaBankCard(BankInfo bankInfo) {//保存银行卡
        boolean flage = false;
        db.beginTransaction(); // 开始事务

        try {
            ContentValues cv = new ContentValues();
            cv.put("bankName", bankInfo.bankName);
            cv.put("bankNumber", bankInfo.bankNumber);
            cv.put("userName", bankInfo.userName);
            long f = db.insert(BankDBHelper.BankCard_TABLE_NAME, null, cv);//发生错误返回-1
            if (f == -1) {
                flage = false;
            } else {
                flage = true;
            }
            KJLoger.log(TAG,"--sava-bak-card--"+flage);

            db.setTransactionSuccessful(); // 设置事务成功完成
        } finally {
            db.endTransaction(); // 结束事务

        }

        return flage;
    }
    /*查询
    * */
    public List<BankInfo> getBankCardList()
    {
        ArrayList<BankInfo> cartInfos = new ArrayList<BankInfo>();
        Cursor c = queryTheCursor();
        while (c.moveToNext())
        {
            BankInfo cartInfo = new BankInfo();
            cartInfo._id=(c.getInt(c.getColumnIndex("_id")));
            cartInfo.bankName=(c.getString(c.getColumnIndex("bankName")));
            cartInfo.bankNumber = c.getString(c.getColumnIndex("bankNumber"));
            cartInfo.userName = c.getString(c.getColumnIndex("userName"));
            cartInfos.add(cartInfo);
        }
        c.close();
        return cartInfos;
    }
    /*删除某个Item*/

    public int deleteItem(String bankName,String bankNumber,String userName)
    {
        int delete = db.delete(BankDBHelper.BankCard_TABLE_NAME,
                "bankName = ? and bankNumber = ? and userName = ?",
                new String[]{bankName,bankNumber,userName});
        KJLoger.log(TAG, "delete -->" + delete);
        return delete;
    }
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM " + BankDBHelper.BankCard_TABLE_NAME ,null);
        return c;
    }
}
