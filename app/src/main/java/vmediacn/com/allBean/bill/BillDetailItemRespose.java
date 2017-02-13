package vmediacn.com.allBean.bill;

import java.util.List;

/**
 * Created by Administrator on 2016/4/21.
 *//*
//订单详情请求
*//*
{
    "resultState": true,
    "message": "获取成功",
    "Shops": [
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
    ],
    "Goods": [
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
    ]
}
*/

public class BillDetailItemRespose {
    private String resultState,message;
    private List<BillShopsBody> Shops;
    private List<BillGoodsBody> Goods;

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

    public List<BillShopsBody> getShops() {
        return Shops;
    }

    public void setShops(List<BillShopsBody> shops) {
        Shops = shops;
    }

    public List<BillGoodsBody> getGoods() {
        return Goods;
    }

    public void setGoods(List<BillGoodsBody> goods) {
        Goods = goods;
    }
}
