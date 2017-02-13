package vmediacn.com;

import android.app.ActivityManager;
import android.content.Context;
import android.os.SystemClock;

import org.kymjs.kjframe.utils.KJLoger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * 异常处理类
 *
 */
public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Context mContext;
    private String TAG = "homory.visionmedia";
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public DefaultExceptionHandler(Context act) {
        mContext = act;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
//        MobclickAgent.reportError(mContext, ex);
        sendCrashReport(ex);

        KJLoger.debug(TAG + ex.getMessage(), ex);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {

        }

        handleException();

    }
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private void sendCrashReport(Throwable ex) {

        File file = mContext.getExternalFilesDir("Crash_Rong");

        if (!file.exists()) {
            file.mkdirs();
        }

        File outFile = new File(file, getCurProcessName(mContext) + SystemClock.elapsedRealtime());

        try {
            outFile.createNewFile();
            saveCrashInfo2File(outFile, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleException() {
        KJLoger.log(TAG, "===异常捕获触发========");

        VisionMediaApplication.getInstance().finishAllActivity();
        System.exit(0);
    }

    String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }

    private void saveCrashInfo2File(File file, Throwable ex) throws IOException {

        KJLoger.log(TAG,"异常捕获文件存储路径==="+file.getPath());

        StringBuffer sb = new StringBuffer();

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(sb.toString().getBytes());
        fos.close();
    }

}