package vmediacn.com.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/4/18.
 */
public class HistoryDBHelper extends SQLiteOpenHelper {
    private String TAG = "--HistoryDBHelper--";
    // 数据库名
    private static final String DATABASE_NAME = "historyDB.db";
    // 数据表名，一个数据库中可以有多个表（虽然本例中只建立了一个表）
    public static final String TABLE_NAME = "historyTable";

    public HistoryDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public HistoryDBHelper(Context context) {
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE [" +TABLE_NAME + "] "
                + "("+"[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+"[title] TEXT)";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}