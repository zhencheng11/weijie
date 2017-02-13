package vmediacn.com.allBean.mine;

/**
 * 收货地址body
 * Created by Administrator on 2016/4/11.
 */
/*"resultBody":[{"id":地址id,"name":"地址名字",”mobile”:”电话”,”address1”:”地址”,”street”:”街道”,
”is_default”:”默认收货地址”}]
{"resultState":true,"message":"获取成功",
"resultBody":[{"id":16,"customer_id":null,
"name":"丁兴向","mobile":"13717899174","address1":"北京市朝阳区东村国际创意基地",
"zip_code":null,
"is_default":"0",
"remark":null,
"street":"北京维圣传媒G1-1","
areaRegion":null,
"phone":null,
"whether_ordrer":null,
"sex":null},
}*/
public class MineAddrResultBody {
    private String id,name,mobile,address1,street,is_default;
    private String customer_id,zip_code,remark,areaRegion,phone,whether_ordrer,sex;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getIs_default() {
        return is_default;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAreaRegion() {
        return areaRegion;
    }

    public void setAreaRegion(String areaRegion) {
        this.areaRegion = areaRegion;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWhether_ordrer() {
        return whether_ordrer;
    }

    public void setWhether_ordrer(String whether_ordrer) {
        this.whether_ordrer = whether_ordrer;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
