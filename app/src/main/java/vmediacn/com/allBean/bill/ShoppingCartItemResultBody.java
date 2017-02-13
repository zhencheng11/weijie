package vmediacn.com.allBean.bill;

/**
 * Created by Administrator on 2016/4/12.
 * 购物车数据内容
 * resultBody”:[{
 “Id”:”购物车ID”,”pic”:”图片”,”price”:”单价”,”Attribute_value”:”属性”，”name”:”商品名称”
 “number”:”个数”
 }]
//{"id":"11621827VM11621827","goodid":1,"name":"炸鸡",
 "pic":"http://192.168.0.68:8048/Logo/zj.jpg","number":10,"price":15.00,"Attribute_value":"家用"}
 */
public class ShoppingCartItemResultBody {
    private String id,goodid,name,number,price,Attribute_value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoodid() {
        return goodid;
    }

    public void setGoodid(String goodid) {
        this.goodid = goodid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAttribute_value() {
        return Attribute_value;
    }

    public void setAttribute_value(String attribute_value) {
        Attribute_value = attribute_value;
    }
}
