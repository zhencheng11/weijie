package vmediacn.com.allBean.bill;

import vmediacn.com.util.MyUtil;

/**
 * Created by Administrator on 2016/4/21.
 */
/*"Shops": [
        {
            "ShopName": "mdl",
            "Name": "科比",
            "fright": 8,
            "Money": 306,
            "yh": 2,
            "code": "2016041800120064695826",
            "time": "/Date(1460951900268)/",
            "tel": null,
            "Address": "北京市朝阳区东村国际创意基地北京维圣传媒G1-1"
        }
    ],*/
public class BillShopsBody {
    private String ShopName,Name,fright,yh,code,time,tel,Address;

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFright() {
        return fright;
    }

    public void setFright(String fright) {
        this.fright = fright;
    }

    public String getYh() {
        return yh;
    }

    public void setYh(String yh) {
        this.yh = yh;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTime() {
        time = MyUtil.changeTime(this.time);
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
