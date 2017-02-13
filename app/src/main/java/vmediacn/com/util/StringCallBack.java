package vmediacn.com.util;

import android.app.Activity;
import android.app.AlertDialog;

import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.utils.KJLoger;

import vmediacn.com.R;

/**
 * 用于处理JSON的http请求回调类
 *
 * 2016-4.1
 *
 */
public abstract class StringCallBack extends HttpCallBack {
    private static Activity activity;
    /**
     * 网络请求成功后回调
     */
    public StringCallBack(Activity activity){
        this.activity=activity;


    }
    abstract public void onSuccess(String json);


    /**
     * 上传进度回调，必须调用了setProgress(true)，该方法才会回调
     */
    @Override
    public void onLoading(long count, long current) {

    }

    /**
     * 网络请求异常后回调
     */
    public void onFailure(Throwable t, int errorNo, String strMsg) {
        super.onFailure(errorNo,strMsg);
        KJLoger.log("错误信息", strMsg);
        new AlertDialog.Builder(activity).setTitle("提示：").setMessage("网络请求失败，请检查网络")
                .setPositiveButton("确定", null).setIcon(R.mipmap.ic_note).create()
                .show();
    }


    public void onSuccess(Object t) {
        onSuccess(t.toString());
    }

    @Override
    public void onFinish() {
        super.onFinish();
        MyDialog.closeDialog();
    }

    @Override
    public void onPreStart() {
        super.onPreStart();
        MyDialog.showDialog(activity);
    }
}
