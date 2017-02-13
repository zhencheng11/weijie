package vmediacn.com.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.kymjs.kjframe.KJActivity;

import vmediacn.com.R;
import vmediacn.com.VisionMediaApplication;
/*
* 基本Activity 继承类
* 所有的activity都应继承它，方便管理
*
* */
public abstract class BaseActivity extends KJActivity {

    private TextView titleContentTV;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        VisionMediaApplication.getInstance().addActivity(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VisionMediaApplication.getInstance().finishActivity(this);
    }
    /**
     * skip to @param(cls)，and call @param(aty's) finish() method
     */
    @Override
    public void skipActivity(Activity aty, Class<?> cls) {
        super.skipActivity(aty, cls);
    }

    @Override
    public void skipActivity(Activity aty, Intent it) {
        super.skipActivity(aty, it);
    }

    @Override
    public void skipActivity(Activity aty, Class<?> cls, Bundle extras) {
        super.skipActivity(aty, cls, extras);
    }
    /**
     * show to @param(cls)，but can't finish activity
     */
    @Override
    public void showActivity(Activity aty, Class<?> cls) {
        super.showActivity(aty, cls);
    }

    @Override
    public void showActivity(Activity aty, Intent it) {
        super.showActivity(aty, it);

    }

    @Override
    public void showActivity(Activity aty, Class<?> cls, Bundle extras) {
        super.showActivity(aty, cls, extras);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        titleContentTV = (TextView) findViewById(R.id.head_title_view_textId);
        imageView = (ImageView) findViewById(R.id.head_title_view_backIgId);
        if (imageView != null) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

    }
    /**
     * 修改标题的文字
     *
     * @param titleContent
     */
    public void changeText(String titleContent){
        if(titleContentTV!=null) {
            titleContentTV.setText(titleContent);
        }
    }
    public static void ShowToast(Context context,String message){
        Toast.makeText(context ,message ,Toast.LENGTH_SHORT).show();

    }
    public void Goneimage(Boolean is){
        /*if (is){
            if (scan!=null){
                scan.setVisibility(View.GONE);

            }

        }*/

    }
    protected void note(Context context,String title, String content, String button) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(content)
                .setPositiveButton(button, null).setIcon(R.mipmap.ic_note)
                .show();
    }
}
