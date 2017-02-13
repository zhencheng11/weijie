package vmediacn.com.allBean.find;

import java.util.List;

/**
 * Created by Administrator on 2016/4/19.
 *///{"resultState":true,"message":"获取成功","resultBody":[{"Id":4,"Name":"方便面","Price":20.00,"Pic":"http://192.168.0.68:8048/Logo/zj.jpg","type":"炸鸡是很多快餐店的招牌食品之一。金黄香脆的外皮，鲜嫩多汁的鸡肉，还有香辣咸麻的味道，掀起了一股男女老幼吃炸鸡的热浪。主料三黄鸡1000g辅料油适量 盐适量 五香粉适量 生姜...","brand":"方便面","ShopsId":10,"Creat_time":"\/Date(1460364661000)\/","State":1,"sales":564656,"rx":null}]}

public class ProductDetailItemResponse {
    private String resultState,message;
    private List<ProductDetailItemResultBody> resultBody ;

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

    public List<ProductDetailItemResultBody> getResultBody() {
        return resultBody;
    }

    public void setResultBody(List<ProductDetailItemResultBody> resultBody) {
        this.resultBody = resultBody;
    }
}
