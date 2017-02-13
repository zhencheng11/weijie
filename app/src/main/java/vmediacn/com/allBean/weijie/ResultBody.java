package vmediacn.com.allBean.weijie;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Administrator on 2016/4/7.
 */
 // [{"Id":10,"Name":"生活超市","number":1,"sales":564656,"yh":1.00,"detail":"汉堡包，
 // 以及薯条、炸鸡、汽水、冰品、沙拉、水果等快餐食品","C__Specific_location":"双桥地铁站附近",
 // "Logo":"http://192.168.0.68:8048/Logo/mdl.jpg","Longitude":"116.57843","Latitude":"39.875435",
 // "Distances":"在400米以内","type":"便利超市","grade":5}]}

public class ResultBody {
    private String Id,Name,number,sales,detail,C__Specific_location,Logo,Distances;
    private String type,grade,yh;
    private Double Longitude,Latitude;

    public LatLng getLocation() {
        return new LatLng(getLatitude(), getLongitude());
    }
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public String getDistances() {
        return Distances;
    }

    public void setDistances(String distances) {
        Distances = distances;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getYh() {
        return yh;
    }

    public void setYh(String yh) {
        this.yh = yh;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }
}
