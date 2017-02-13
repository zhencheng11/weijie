package vmediacn.com.allBean.weijie;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/4/8.
 */
/*{"resultState": true,
    "message": "成功",
resultBody:[{
“Id”:”商品Id”,”Name”:”商品名字”,”C__Specific_location”:”地理位置”,”Logo”:”图片”,”Price”:”单价”,”fright”:”运费”,”Distances”:”距离多少米”,”Sales”:”销量”
}]}

*/
    //附近的商品
public class WSNearProductRespose implements Serializable{
    private String resultState,message;
    private List<WSNearProductBody> resultBody;

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

    public List<WSNearProductBody> getResultBody() {
        return resultBody;
    }

    public void setResultBody(List<WSNearProductBody> resultBody) {
        this.resultBody = resultBody;
    }
}
