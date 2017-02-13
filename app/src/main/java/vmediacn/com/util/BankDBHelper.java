package vmediacn.com.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.kymjs.kjframe.utils.KJLoger;

/**
 * Created by DingXingXiang on 2016/5/4.
 * 银行卡信息存储管理类
 */
public class BankDBHelper extends SQLiteOpenHelper {
    private String TAG = "--BankDBHelper--";
    private static final String BankCard_DATABASE_NAME = "bankcard.db";
    public static final String  BankCard_TABLE_NAME = "bankInfos";

    public BankDBHelper(Context context) {
        super(context,BankCard_DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        KJLoger.log(TAG, "--BankDBHelper onCreate--");
        // 构建创建表的SQL语句（可以从SQLite Expert工具的DDL粘贴过来加进StringBuffer中）
        StringBuffer sBuffer = new StringBuffer();

        sBuffer.append("CREATE TABLE [" + BankCard_TABLE_NAME + "] (");
        sBuffer.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sBuffer.append("[bankName] TEXT,");
        sBuffer.append("[bankNumber] TEXT,");
        sBuffer.append("[userName] TEXT)");
        // 执行创建表的SQL语句
        KJLoger.log(TAG, "-执行创建表的SQL语句---"+sBuffer.toString());
        db.execSQL(sBuffer.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BankCard_TABLE_NAME);
        onCreate(db);
    }
}