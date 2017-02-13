package vmediacn.com.activity.load;

import android.os.Handler;
import android.os.Message;

import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.PreferenceHelper;

import vmediacn.com.R;
import vmediacn.com.activity.BaseActivity;
import vmediacn.com.activity.MainActivity;
/*加载页
* */
public class SplashActivity extends BaseActivity {
    private String TAG = "vmediacn.com.activity.load.SplashActivity--";
    @Override
    public void setRootView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    boolean isLogin = PreferenceHelper.readBoolean(SplashActivity.this, "UserInfo", "isLogin", false);
                    if (isLogin) {
                        handler.obtainMessage(100).sendToTarget();
                    } else {
                        handler.obtainMessage(101).sendToTarget();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                //已登录，直接跳转
                KJLoger.log(TAG," what--"+msg.what);
                skipActivity(SplashActivity.this, MainActivity.class);

            }else if (msg.what == 101) {
                //跳转登录页面
                KJLoger.log(TAG," what--"+msg.what);
                skipActivity(SplashActivity.this,LoginActivity.class);
            }
        }
    };
}
