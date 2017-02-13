package vmediacn.com;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

import java.util.Stack;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/4/5.
 */
public class VisionMediaApplication extends Application {
    private static Stack<Activity> activityStack;
    private static VisionMediaApplication singleton;
    private String TAG = "com.visionmedia";
    @Override
    public void onCreate() {
        super.onCreate();
        singleton=this;
        /*短信验证*/
        SMSSDK.initSDK(this, "114288cf82762", "6520ef8710439cf0133bfa9012ccb2d2");

        /*百度地图*/
        SDKInitializer.initialize(getApplicationContext());

        //奔溃日志处理
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));

        //极光推送
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(getApplicationContext()); // 初始化 JPush

    }
    public static VisionMediaApplication getInstance() {
        return singleton;
    }
    /*
     * 获得当前进程号
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity){
        if(activityStack ==null){
            activityStack =new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }
    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

