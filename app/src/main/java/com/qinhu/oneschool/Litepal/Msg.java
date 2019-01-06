package com.qinhu.oneschool.Litepal;

import org.litepal.crud.DataSupport;

public class Msg extends DataSupport {
    private int id;
    private boolean type;//什么类型性的消息;
    private boolean isRead;//是否已读;
    private boolean isSingle;//是否为单聊
    private String time;//时间
    private String account;//接收账号
    private String accountSend;//发送方账号
    private String text;//消息内容

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccountSend() {
        return accountSend;
    }

    public void setAccountSend(String accountSend) {
        this.accountSend = accountSend;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
