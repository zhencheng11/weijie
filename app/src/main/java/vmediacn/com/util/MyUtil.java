package vmediacn.com.util;

/**
 * Created by Administrator on 2015/7/21.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.kymjs.kjframe.utils.KJLoger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用工具类
 *
 * @author Administrator
 */
public class MyUtil {

    /**
     * 服务器传来的时间转换成本地可用的
     * /Date(1441866722380+0800)/
     *
     * @param content
     * @return
     */
    public static String changeTime(String content) {
        if (!TextUtils.isEmpty(content) && content.contains("(") && content.contains("+")) {
            KJLoger.log("homory.visionmedia.util===时间数据==", content);
            int b = content.indexOf("(");
            int e = content.lastIndexOf(")");
            final String time = content.substring(b + 1, e - 5);
            final Date date = new Date(Long.parseLong(time));
            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        }else if(!TextUtils.isEmpty(content) && content.contains("(")){
            KJLoger.log("homory.visionmedia.util===时间数据==", content);
            int b = content.indexOf("(");
            int e = content.lastIndexOf(")");
            final String time = content.substring(b + 1, e);
            final Date date = new Date(Long.parseLong(time));
            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        }
        return content;
    }
    public static String cTime2jTimes(String content) {
        if (!TextUtils.isEmpty(content) && content.contains("(")) {
            KJLoger.log("homory.visionmedia.util===时间数据==", content);
            int b = content.indexOf("(");
            int e = content.lastIndexOf(")");
            final String time = content.substring(b + 1, e - 5);
            final Date date = new Date(Long.parseLong(time));
            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.format(date);
        }
        return content;
    }

    /**
     * 获取月份
     * @param content
     * @return
     */
    public static String getMyDate(String content){
        if (!TextUtils.isEmpty(content) ) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String[] spit = content.split(" ");
            KJLoger.log("homory.visionmedia.util===时间数据==", content);
            final Date date;
            try {
                date = sdf.parse(spit[0].replace("/", "-"));
                Calendar cld = Calendar.getInstance();
                cld.setTime(date);
                int month = cld.get(Calendar.MONTH)+1;
                int dates = cld.get(Calendar.DATE);
                return month+"-"+dates;
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return content;
    }
    public static int  getMonth(String content) {

        if (!TextUtils.isEmpty(content) ) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String[] spit = content.split(" ");
            KJLoger.log("homory.visionmedia.util===时间数据==", content);
            final Date date;
            try {
                date = sdf.parse(spit[0].replace("/", "-"));
                Calendar cld = Calendar.getInstance();
                cld.setTime(date);
                int weed = cld.get(Calendar.MONTH)+1;
                return weed;
            } catch (ParseException e) {
                e.printStackTrace();
            }
    }
        return 0;
    }
    public static String  getWeed(String content) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        if (!TextUtils.isEmpty(content)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String[] spit = content.split(" ");
            KJLoger.log("homory.visionmedia.util===时间数据==", content);
            final Date date;
            try {
                date = sdf.parse(spit[0].replace("/", "-"));
                Calendar cld = Calendar.getInstance();
                cld.setTime(date);
                int weed = cld.get(Calendar.DAY_OF_WEEK)+1;
                if (weed < 0)
                    weed = 0;
                return weekDays[weed%7];
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "星期日";
    }

    /**
     * 验证URL
     *
     * @param
     * @return
     */
    public static boolean checkURL(String url) {
        boolean flag = false;
        try {
//			String check = "http://|https://)?(\w+\.){1,3}(com(\.cn)?|cn|net|info|org|us|tk)\b";
            String check = "(http://|https://)([\\w-]+\\.)+[\\w-]+(/[\\w-] ./?%&=*)?";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(url);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证邮箱
     *
     * @param
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 校验银行卡卡号
     *
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return
     */
    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * 验证手机号是否合法
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNumber(String mobiles) {
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 获取当前网络连接的类型信息
     *
     * @param context
     * @return
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * 判断MOBILE网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断WIFI网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private void getInfo(Context context) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String imsi = mTm.getSubscriberId();
        String mtyversion = android.os.Build.VERSION.RELEASE;//手机系统版本
        String mtype = android.os.Build.MODEL; // 手机型号
        String mtyb = android.os.Build.BRAND;//手机品牌
        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
        Log.i("text", "手机IMEI号：" + imei + "手机IESI号：" + imsi + "手机型号：" + mtype + "手机品牌：" + mtyb + "手机号码" + numer + "手机系统版本" + mtyversion);
    }

    /**
     * 限制输入的小数点（两位）
     *
     * @param
     */
    public static void setPricePoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

        });

    }

    /**
     * Map a value within a given range to another range.
     *
     * @param value    the value to map
     * @param fromLow  the low end of the range the value is within
     * @param fromHigh the high end of the range the value is within
     * @param toLow    the low end of the range to map to
     * @param toHigh   the high end of the range to map to
     * @return the mapped value
     */
    public static double mapValueFromRangeToRange(
            double value,
            double fromLow,
            double fromHigh,
            double toLow,
            double toHigh) {
        double fromRangeSize = fromHigh - fromLow;
        double toRangeSize = toHigh - toLow;
        double valueScale = (value - fromLow) / fromRangeSize;
        return toLow + (valueScale * toRangeSize);
    }

    /**
     * set margins of the specific view
     *
     * @param target
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public static void setMargin(View target, int l, int t, int r, int b) {
        if (target.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) target.getLayoutParams();
            p.setMargins(l, t, r, b);
            target.requestLayout();
        }
    }

    /**
     * convert drawable to bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }
    /**
     * 从字符串中截取数字
     */
    //截取数字
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    /**
     * 倒计时
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format
     */
    public static String dateDiff(String startTime, String endTime, String format) {
        String message = null;
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            if (diff>=0){
                long day = diff / nd;// 计算差多少天
                long hour = diff % nd / nh;// 计算差多少小时
                long min = diff % nd % nh / nm;// 计算差多少分钟
                long sec = diff % nd % nh % nm / ns;// 计算差多少秒
                // 输出结果
                message = "倒计时：" + day + "天" + hour + "小时" + min + "分钟" + sec + "秒。";
            }else {
                message ="该项目已到期";
            }
            return message;
        } catch (ParseException e) {
            e.printStackTrace();
        }   catch (NullPointerException ex){
            ex.printStackTrace();
        }
        return null;
    }
}
