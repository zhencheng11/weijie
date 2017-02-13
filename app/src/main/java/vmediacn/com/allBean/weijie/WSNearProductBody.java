package vmediacn.com.allBean.weijie;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/8.
 */
/*/*{"resultState": true,
    "message": "成功",
resultBody:[{
“Id”:”商品Id”,”Name”:”商品名字”,”C__Specific_location”:”地理位置”,”Logo”:”图片”,”
Price”:”单价”,”fright”:”运费”,”Distances”:”距离多少米”,”Sales”:”销量”
}]}
//{"resultState":true,"message":"获取成功","resultBody":
[{"Id":4,"Name":"方便面","Price":20.00,"Pic":"http://192.168.0.68:8048/Logo/zj.jpg",
"type":"炸鸡是很多快餐店的招牌食品之一。金黄香脆的外皮，鲜嫩多汁的鸡肉，还有香辣咸麻的味道
，掀起了一股男女老幼吃炸鸡的热浪。主料三黄鸡1000g辅料油适量 盐适量 五香粉适量 生姜...",
"brand":"方便面","ShopsId":10,"Creat_time":"\/Date(1460364661000)\/","State":1,"sales":564656,"rx":null}]}

*/
public class WSNearProductBody implements Serializable{
    private String Id,Name,C__Specific_location,Logo,Price,fright,Distances,Sales;

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

    public String getC__Specific_location() {
        return C__Specific_location;
    }

    public void setC__Specific_location(String c__Specific_location) {
        C__Specific_location = c__Specific_location;
    }

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getFright() {
        return fright;
    }

    public void setFright(String fright) {
        this.fright = fright;
    }

    public String getDistances() {
        return Distances;
    }

    public void setDistances(String distances) {
        Distances = distances;
    }

    public String getSales() {
        return Sales;
    }

    public void setSales(String sales) {
        Sales = sales;
    }
}
