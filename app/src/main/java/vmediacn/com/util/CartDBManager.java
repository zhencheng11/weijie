package vmediacn.com.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.kymjs.kjframe.utils.KJLoger;

import java.util.ArrayList;
import java.util.List;

import vmediacn.com.allBean.find.CartInfo;

/**
 * Created by DingXingXiang on 2016/4/23.
 * 购物车的数据库处理工具
 */
public class CartDBManager {
    private String TAG = "CartDBManager";
    SQLiteDatabase db;
    CartDatabaseHelper helper;

    public CartDBManager(Context context) {
        helper = new CartDatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public boolean addToCart(CartInfo cartInfo) {//添加购物车
        boolean flage = false;
        db.beginTransaction(); // 开始事务

        try {
            ContentValues cv = new ContentValues();
            cv.put("shopId", cartInfo.shopId);
            cv.put("goodId", cartInfo.goodId);
            cv.put("goodName", cartInfo.goodName);
            cv.put("price", cartInfo.price);
            cv.put("num", cartInfo.num);
            long f = db.insert(CartDatabaseHelper.Cart_TABLE_NAME, null, cv);//发生错误返回-1
            /*db.execSQL("INSERT INTO " + CartDatabaseHelper.Cart_TABLE_NAME +
                    " VALUES(null, ?, ?, ?)",
                    new Object[]{cartInfo.shopId, cartInfo.goodId, cartInfo.num});*/
            if (f == -1) {
                flage = false;
            } else {
                flage = true;
            }
            KJLoger.log(TAG,"--addToCart--"+flage);

            db.setTransactionSuccessful(); // 设置事务成功完成
        } finally {
            db.endTransaction(); // 结束事务

        }

        return flage;
    }

    public int updateCartNum(String shopId, String goodId, int num) {
        KJLoger.log(TAG, "HistoryDBManager --> updateNum");
        ContentValues cv = new ContentValues();
        cv.put("num", num);
        int update = db.update(CartDatabaseHelper.Cart_TABLE_NAME, cv, "shopId = ? and goodId = ?",
                new String[]{shopId, goodId});
        KJLoger.log(TAG, "update -->"+ update);
        return update;
    }

    /*删除某个Item*/

    public int deleteItem(String goodId)
    {
        int delete = db.delete(CartDatabaseHelper.Cart_TABLE_NAME, "goodId = ?", new String[]{goodId});
        KJLoger.log(TAG, "delete -->"+ delete);
        return delete;
    }
    /*清空购物车
    @params shopId的集合
    * */
    public void clearCart(String shopId)
    {
        int deleall = db.delete(CartDatabaseHelper.Cart_TABLE_NAME, "shopId = ?",
                new String[]{shopId});
        KJLoger.log(TAG, "deleall -->"+ deleall);


    }
    /*查询
    * */
    public List<CartInfo> query(String shopId)
    {
        ArrayList<CartInfo> cartInfos = new ArrayList<CartInfo>();
        Cursor c = queryTheCursor(shopId);
        while (c.moveToNext())
        {
            CartInfo cartInfo = new CartInfo();
            cartInfo._id=(c.getInt(c.getColumnIndex("_id")));
            cartInfo.shopId=(c.getString(c.getColumnIndex("shopId")));
            cartInfo.goodId = c.getString(c.getColumnIndex("goodId"));
            cartInfo.goodName = c.getString(c.getColumnIndex("goodName"));
            cartInfo.price = c.getDouble(c.getColumnIndex("price"));
            cartInfo.num = c.getInt(c.getColumnIndex("num"));
            //KJLoger.log(TAG, "--query-goodName-" + cartInfo.goodName);
            cartInfos.add(cartInfo);
        }
        c.close();
        return cartInfos;
    }
    public Cursor queryTheCursor(String shopId) {
        Cursor c = db.rawQuery("SELECT * FROM " + CartDatabaseHelper.Cart_TABLE_NAME +
                " where shopId = ?", new String[]{shopId});
        return c;
    }
    public int getNum(String shopId,String goodId) {
        Cursor c = db.rawQuery("SELECT * FROM " + CartDatabaseHelper.Cart_TABLE_NAME +
                " where shopId = ? and goodId = ?", new String[]{shopId, goodId});
        int num =0;
        if (c.moveToNext()) {
            num = c.getInt(c.getColumnIndex("num"));
            return num;
        }
       return num;
    }


    public boolean isAdded(String shopId,String goodId) {
        Cursor c = db.rawQuery("SELECT * FROM " + CartDatabaseHelper.Cart_TABLE_NAME +
                " where shopId = ? and goodId = ?", new String[]{shopId, goodId});
        int position = c.getPosition();
        KJLoger.log(TAG, "--cursorPosition--" + position);
        if (c.moveToNext()) {
            return true;
        } else{
            return false;
        }

    }

    public void closeDB() {
        // 释放数据库资源
        db.close();
    }
}
