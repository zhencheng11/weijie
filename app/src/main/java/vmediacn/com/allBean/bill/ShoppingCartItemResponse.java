package vmediacn.com.allBean.bill;

import java.util.List;

/**
 * Created by Administrator on 2016/4/12.
 * 购物车数据响应体
 */
public class ShoppingCartItemResponse {
    private String resultState,message;
    private List<ShoppingCartItemResultBody> resultBody;

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

    public List<ShoppingCartItemResultBody> getResultBody() {
        return resultBody;
    }

    public void setResultBody(List<ShoppingCartItemResultBody> resultBody) {
        this.resultBody = resultBody;
    }
}
