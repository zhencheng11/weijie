package vmediacn.com.allBean.bill;

import java.util.List;

/**
 * Created by Administrator on 2016/4/18.
 */

//"resultState":true,"message":"获取成功","resultBody":[{"Id":10,"state":1,"Name":"炸鸡等2件商品","Money":308.00,"ShopLogo":"http://192.168.0.68:8048/Logo/mdl.jpg","ShopName":"mdl","time":"\/Date(1460951900268)\/"},{"Id":11,"state":1,"Name":"炸鸡等1件商品","Money":158.00,"ShopLogo":"http://192.168.0.68:8048/Logo/mdl.jpg","ShopName":"mdl","time":"\/Date(1460951931343)\/"},{"Id":12,"state":1,"Name":"炸鸡等2件商品","Money":308.00,"ShopLogo":"http://192.168.0.68:8048/Logo/mdl.jpg","ShopName":"mdl","time":"\/Date(1460963482286)\/"}]}

public class BillResponse {
    private String resultState;
    private String message;
    private List<BillResultBody> resultBody;

    public String getResultState() {
        return resultState;
    }

    public void setResultState(String resultState) {
        this.resultState = resultState;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BillResultBody> getResultBody() {
        return resultBody;
    }

    public void setResultBody(List<BillResultBody> resultBody) {
        this.resultBody = resultBody;
    }
}
