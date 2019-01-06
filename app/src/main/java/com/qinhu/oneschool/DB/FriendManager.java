package com.qinhu.oneschool.DB;

import cn.bmob.v3.BmobObject;

public class FriendManager extends BmobObject {

    private  int id;
    private String accountSend;
    private String getAccountSend;
    private String type;//可以取1.请求添加add，2.同意添加again，3.删除好友delete,4.拒绝添加refuse；
    private  boolean isAndroid;//是否已经被用户获取

    private boolean isGet;//是否已经反馈

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountSend() {
        return accountSend;
    }

    public void setAccountSend(String accountSend) {
        this.accountSend = accountSend;
    }

    public String getGetAccountSend() {
        return getAccountSend;
    }

    public void setGetAccountSend(String getAccountSend) {
        this.getAccountSend = getAccountSend;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isGet() {
        return isGet;
    }

    public void setGet(boolean get) {
        isGet = get;
    }

    public boolean getIsAndroid() {
        return isAndroid;
    }

    public void setIsAndroid(boolean isAndroid) {
        this.isAndroid = isAndroid;
    }
}
