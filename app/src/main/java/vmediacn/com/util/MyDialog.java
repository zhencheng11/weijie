package vmediacn.com.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Administrator on 2016.4.1
 * 加载过度dialog
 */
public class MyDialog {
    private static ProgressDialog proDia =null;
    public static void showDialog(Context context){
        proDia=new ProgressDialog(context);
        proDia.setTitle("加载中");
        proDia.setMessage("请等待");
        proDia.onStart();
        proDia.setCanceledOnTouchOutside(false);
        proDia.show();
    }


    public static void closeDialog(){
        if (proDia!=null) {
            proDia.dismiss();
        }
    }

}
