package vmediacn.com.allBean.weijie;

import java.util.List;

/**
 * Created by Administrator on 2016/4/8.
 */
//{"resultState":true,"message":"成功","resultBody":[{"Id":0,"Sales":546644,"number":2,"ShopSales":
// 1111300,"Name":"炸鸡","goodSales":"炸鸡是很多快餐店的招牌食品之一。金黄香脆的外皮，鲜嫩多汁的鸡肉，
// 还有香辣咸麻的味道，掀起了一股男女老幼吃炸鸡的热浪。主料三黄鸡1000g辅料油适量 盐适量 五香粉适量 生姜..
// .","Price":15.00,"ShopLogo":"http://192.168.0.68:8048/MS/Logo/麦当劳.jpg",
// "GoodLogo":"http://192.168.0.68:8048/MS/Logo/炸鸡.jpg","ShopName":"麦当劳","grade":5}]}

public class WSProductDetailRespose {
    private String resultState,message;
    private List<ProductDetailResultBody> resultBody;

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

    public List<ProductDetailResultBody> getResultBody() {
        return resultBody;
    }

    public void setResultBody(List<ProductDetailResultBody> resultBody) {
        this.resultBody = resultBody;
    }
}
