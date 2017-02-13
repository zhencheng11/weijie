package vmediacn.com.allBean.bill;

/**
 * Created by Administrator on 2016/4/18.
 */
//"resultBody":[{"Id":10,"state":1,"Name":"炸鸡等2件商品","Money":308.00,
// "ShopLogo":"http://192.168.0.68:8048/Logo/mdl.jpg","ShopName":"mdl","time":"\/Date(1460951900268)\/"},
// {"Id":11,"state":1,"Name":"炸鸡等1件商品","Money":158.00,"ShopLogo":"http://192.168.0.68:8048/Logo/mdl.jpg","ShopName":"mdl","time":"\/Date(1460951931343)\/"},{"Id":12,"state":1,"Name":"炸鸡等2件商品","Money":308.00,"ShopLogo":"http://192.168.0.68:8048/Logo/mdl.jpg","ShopName":"mdl","time":"\/Date(1460963482286)\/"}]}

public class BillResultBody {
    private String Id,state,Name,Money,ShopLogo,ShopName,time;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMoney() {
        return Money;
    }

    public void setMoney(String money) {
        Money = money;
    }

    public String getShopLogo() {
        return ShopLogo;
    }

    public void setShopLogo(String shopLogo) {
        ShopLogo = shopLogo;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
