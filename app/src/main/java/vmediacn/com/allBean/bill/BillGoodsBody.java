package vmediacn.com.allBean.bill;

/**
 * Created by Administrator on 2016/4/21.
 *//*"Goods": [
        {
            "Name": "炸鸡家用",
            "Money": 150,
            "count": 10
        },
        {
            "Name": "炸鸡家用",
            "Money": 150,
            "count": 10
        }
    ]*/
public class BillGoodsBody {
    private String Name,Money,count;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMoney() {
        return Money;
    }

    public void setMoney(String money) {
        Money = money;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
