package vmediacn.com.allBean.weijie;

/**
 * Created by Administrator on 2016/4/8.
 */
//{"resultState":true,"message":"成功","resultBody"
// [{"Id":0,"Sales":564656,"number":1,"Name":"生活超市",
// "brief":"汉堡包，以及薯条、炸鸡、汽水、冰品、沙拉、水果等快餐食品",
// "location":"双桥地铁站附近","Price":null,"qisong":50.00,"fright":8.00,
// "Logo":"http://192.168.0.68:8048/Logo/mdl.jpg","grade":5,"ktime":"8:30","gtime":"20:40","time":"30"}]}

public class WSShopDetilResultBody {
   private String Id,Sales,number,Name,brief,location,Price,fright,Logo,grade,ktime,gtime,time,qisong;

    public void setQisong(String qisong) {
        this.qisong = qisong;

    }

    public String getQisong() {
        return qisong;
    }

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

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getKtime() {
        return ktime;
    }

    public void setKtime(String ktime) {
        this.ktime = ktime;
    }

    public String getGtime() {
        return gtime;
    }

    public void setGtime(String gtime) {
        this.gtime = gtime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
