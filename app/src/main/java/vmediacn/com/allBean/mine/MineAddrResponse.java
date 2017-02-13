package vmediacn.com.allBean.mine;

import java.util.List;

/**
 * 收货地址请求体
 * Created by Administrator on 2016/4/11.
 */
/*{
"resultState": true,
    "message": "获取成功",

"resultBody":[{"id":地址id,"name":"地址名字",”mobile”:”电话”,”address1”:”地址”,”street”:”街道”,”is_default”:”默认收货地址”}]

}
*/
public class MineAddrResponse {
    private String resultState,message;
    private List<MineAddrResultBody> resultBody;

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

    public List<MineAddrResultBody> getResultBody() {
        return resultBody;
    }

    public void setResultBody(List<MineAddrResultBody> resultBody) {
        this.resultBody = resultBody;
    }
}
