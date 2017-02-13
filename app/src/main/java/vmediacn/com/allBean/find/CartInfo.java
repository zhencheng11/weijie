package vmediacn.com.allBean.find;

/**
 * Created by DingXingXiang on 2016/4/23.
 * 购物车信息
 */
public class CartInfo {
    public int _id;
    public String shopId;
    public String goodId;
    public String goodName;
    public double price;
    public int num;

    public CartInfo() {
    }

    public CartInfo(String shopId, String goodId,String goodName,double price,int num) {
        this.shopId = shopId;
        this.goodId = goodId;
        this.goodName = goodName;
        this.num = num;
        this.price = price;
    }
}
