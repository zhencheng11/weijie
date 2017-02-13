package vmediacn.com.allBean.find;
/*
*//**
 * Created by Administrator on 2016/4/19.
 *//* //{"resultState":true,"message":"获取成功",
* // "resultBody":[{"Id":4,"Name":"方便面",
        * // "Price":20.00,
        * // "Pic":"http://192.168.0.68:8048/Logo/zj.jpg",
        * // "type":"炸鸡是很多快餐店的招牌食品之一。金黄香脆的外皮，鲜嫩多汁的鸡肉，
        * // 还有香辣咸麻的味道，掀起了一股男女老幼吃炸鸡的热浪。主料三黄鸡1000g辅料油适量 盐适量
        * // 五香粉适量 生姜...",
        * // "brand":"方便面",
        * // "ShopsId":10,
        * // "Creat_time":"\/Date(1460364661000)\/",
        * // "State":1,
        * // "sales":564656,
        * // "rx":null}]}*/

public class ProductDetailItemResultBody {
    private String Id,Name,Price,Pic,type,brand,ShopsId,Creat_time,State,sales,rx;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getPic() {
        return Pic;
    }

    public void setPic(String pic) {
        Pic = pic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getShopsId() {
        return ShopsId;
    }

    public void setShopsId(String shopsId) {
        ShopsId = shopsId;
    }

    public String getCreat_time() {
        return Creat_time;
    }

    public void setCreat_time(String creat_time) {
        Creat_time = creat_time;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }
}
