package vmediacn.com.allBean.weijie;

import java.util.List;

/**
 * Created by Administrator on 2016/4/7.
 */
//{"resultState":true,"message":"成功",
// "resultBody":[{"Id":1,"Name":"麦当劳","number":0,"sales":null,
// "detail":"汉堡包，以及薯条、炸鸡、汽水、冰品、沙拉、水果等快餐食品、汉堡包，以及薯条、炸鸡、汽水、冰品、沙拉、水果等快餐食品",
// "C__Specific_location":"双桥地铁站附近","Logo":"http://192.168.0.68:8048/MS/Logo/麦当劳.jpg",
// "Longitude":"116.57543","Latitude":"39.877001","Distances":"在900米以内","type":"快餐","grade":5}]}
public class WeiJieRespose {
    String resultState,message;
    private List<ResultBody> resultBody;

    public String getResultState() {
        return resultState;
    }

    public void setResultState(String resultState) {
        this.resultState = resultState;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultBody> getResultBody() {
        return resultBody;
    }

    public void setResultBody(List<ResultBody> resultBody) {
        this.resultBody = resultBody;
    }
}
