package vmediacn.com.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.kymjs.kjframe.utils.KJLoger;

/**
 * Created by DingXingXiang on 2016/4/23.
 * 购物车信息存储管理类
 */
public class CartDatabaseHelper extends SQLiteOpenHelper {
    private String TAG = "--HistoryDBHelper--";
    private static final String Cart_DATABASE_NAME = "cart.db";
    public static final String  Cart_TABLE_NAME = "cartInfo";

    public CartDatabaseHelper(Context context) {
        super(context,Cart_DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        KJLoger.log(TAG, "--HistoryDBHelper onCreate--");
        // 构建创建表的SQL语句（可以从SQLite Expert工具的DDL粘贴过来加进StringBuffer中）
        StringBuffer sBuffer = new StringBuffer();

        sBuffer.append("CREATE TABLE [" + Cart_TABLE_NAME + "] (");
        sBuffer.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sBuffer.append("[shopId] TEXT,");
        sBuffer.append("[goodId] TEXT,");
        sBuffer.append("[goodName] TEXT,");
        sBuffer.append("[price] REAL,");
        sBuffer.append("[num] INTEGER)");
        // 执行创建表的SQL语句
        KJLoger.log(TAG, "-执行创建表的SQL语句---"+sBuffer.toString());
        db.execSQL(sBuffer.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Cart_TABLE_NAME);
        onCreate(db);
    }
}
