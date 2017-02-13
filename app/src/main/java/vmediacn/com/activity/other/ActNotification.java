package vmediacn.com.activity.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;
import vmediacn.com.activity.BaseActivity;
/*jpush
* 打开推送消息
* */
public class ActNotification extends BaseActivity {
    @Override
    public void setRootView() {
        TextView tv = new TextView(this);
        tv.setText("用户自定义打开的Activity");
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = getIntent().getExtras();
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            tv.setText("Title : " + title + "  " + "Content : " + content);
        }
        addContentView(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

    }
}
