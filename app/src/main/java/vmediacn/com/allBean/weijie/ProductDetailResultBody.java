package vmediacn.com.allBean.weijie;

/**
 * 商品详情
 * Created by Administrator on 2016/4/8.
 */
//{"resultState":true,"message":"成功","resultBody":[{"Id":0,"Sales":546644,"number":2,"ShopSales":1111300,"Name":"炸鸡",
// "goodSales":"炸鸡是很多快餐店的招牌食品之一。金黄香脆的外皮，鲜嫩多汁的鸡肉，还有香辣咸麻的味道，
// 掀起了一股男女老幼吃炸鸡的热浪。主料三黄鸡1000g辅料油适量 盐适量 五香粉适量 生姜...",
// "Price":15.00,"ShopLogo":"http://192.168.0.68:8048/MS/Logo/麦当劳.jpg",
// "GoodLogo":"http://192.168.0.68:8048/MS/Logo/炸鸡.jpg","ShopName":"麦当劳","grade":5}]}

public class ProductDetailResultBody {
    private String Id,Sales,number,ShopSales,Name,goodSales,Price,ShopLogo,GoodLogo,ShopName,grade;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getSales() {
        return Sales;
    }

    public void setSales(String sales) {
        Sales = sales;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getShopSales() {
        return ShopSales;
    }

    public void setShopSales(String shopSales) {
        ShopSales = shopSales;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getGoodSales() {
        return goodSales;
    }

    public void setGoodSales(String goodSales) {
        this.goodSales = goodSales;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getShopLogo() {
        return ShopLogo;
    }

    public void setShopLogo(String shopLogo) {
        ShopLogo = shopLogo;
    }

    public String getGoodLogo() {
        return GoodLogo;
    }

    public void setGoodLogo(String goodLogo) {
        GoodLogo = goodLogo;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
