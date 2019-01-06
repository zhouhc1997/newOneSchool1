package com.qinhu.oneschool.Litepal;

import org.litepal.crud.DataSupport;

public class LoaclUser extends DataSupport {
    private int id;
    private String avatar;          //头像
    private String username;        //账号
    private String nickname;          //姓名
    private String sex;           //性别
    private String birthday;      //生日
    private String signturn;
    private String email;       //邮箱
    private String account;     //当前好友所属的账户;
    private String beizhu ;       //备注

    private String updateTime;      //最后更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSignturn() {
        return signturn;
    }

    public void setSignturn(String signturn) {
        this.signturn = signturn;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isExitsDisplayName(){
        return !(beizhu==null||beizhu.equals(""));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
