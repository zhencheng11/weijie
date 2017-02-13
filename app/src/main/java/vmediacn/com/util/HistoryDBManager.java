package vmediacn.com.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.allBean.find.History;

/**
 * Created by DingXingXiang on 2016/4/18.
 */
public class HistoryDBManager {
    private HistoryDBHelper helper;
    private SQLiteDatabase db;

    public HistoryDBManager(Context context) {
        helper = new HistoryDBHelper(context);
        db = helper.getWritableDatabase();
    }

    public void add(History history) {

        db.beginTransaction(); // 开始事务


        String title0 = history.getTitle();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex("title"));
            if (title0.equals(title)) {
                c.close();
                return;
            }
         }
        c.close();
        try
        {
        db.execSQL("INSERT INTO " + HistoryDBHelper.TABLE_NAME +
        " VALUES(null, ?)", new Object[] { title0});
        db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    public void updateHistoty(History history)
    {
        ContentValues cv = new ContentValues();
        cv.put("title", history.getTitle());
        db.update(HistoryDBHelper.TABLE_NAME, cv, "title = ?",
                new String[] { history.getTitle() });
    }
    public void clearHistoty(List<History> histories)
    {
        for (History history:histories) {
            db.delete(HistoryDBHelper.TABLE_NAME, "title = ?",
                    new String[] { history.getTitle() });
        }

    }
    public void deleteItem(History history)
    {
        db.delete(HistoryDBHelper.TABLE_NAME, "title = ?", new String[] { history.getTitle()});
    }
    public List<History> query()
    {
        ArrayList<History> historys = new ArrayList<History>();
        Cursor c = queryTheCursor();
        while (c.moveToNext())
        {
            History history = new History();
            history.set_id(c.getInt(c.getColumnIndex("_id")));
            history.setTitle(c.getString(c.getColumnIndex("title")));
            historys.add(history);
        }
        c.close();
        return historys;
    }
    public Cursor queryTheCursor()
    {
        Cursor c = db.rawQuery("SELECT * FROM " + HistoryDBHelper.TABLE_NAME, null);
        return c;
    }
    public void closeDB()
    {
        // 释放数据库资源
        db.close();
    }
}

