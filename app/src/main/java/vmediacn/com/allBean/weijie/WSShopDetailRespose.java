package vmediacn.com.allBean.weijie;

import java.util.List;

/**
 * Created by Administrator on 2016/4/8.
 */
//
public class WSShopDetailRespose {
    private String resultState,message;
    private List<WSShopDetilResultBody> resultBody;

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

    public List<WSShopDetilResultBody> getResultBody() {
        return resultBody;
    }

    public void setResultBody(List<WSShopDetilResultBody> resultBody) {
        this.resultBody = resultBody;
    }
}
