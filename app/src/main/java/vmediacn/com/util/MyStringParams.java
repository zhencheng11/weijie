package vmediacn.com.util;

import org.json.JSONObject;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.utils.KJLoger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/4/5.
 * 网络请求加密处理类
 */
public class MyStringParams {
    private static String TAG="vmediacn.com";
    public static HttpParams getString(String content) throws Exception {

        KJLoger.log(TAG, "content===" + content);
        //    “消息摘要”
        String encrypt=Encrypt(content.replace("\"","\'"), "SHA-1");
        //    "加密的请求体"
       // String aec= AES256Cipher.AES_Encode(content.replace("\"", "\'")).toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sign", encrypt);
        //jsonObject.put("para", aec);
        KJLoger.log(TAG,"MyStringParams====="+jsonObject.toString());
        KJLoger.log(TAG,jsonObject.toString());
        HttpParams hp = new HttpParams();
        hp.putJsonParams(jsonObject.toString());
        return  hp;
    }


    public static String Encrypt(String strSrc, String encName) {
        // parameter strSrc is a string will be encrypted,
        // parameter encName is the algorithm name will be used.
        // encName dafault to "MD5"
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            if (encName == null || encName.equals("")) {
                encName = "MD5";
            }
            md = MessageDigest.getInstance(encName);
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Invalid algorithm.");
            return null;
        }
        return strDes.toUpperCase();
    }

    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }


}
