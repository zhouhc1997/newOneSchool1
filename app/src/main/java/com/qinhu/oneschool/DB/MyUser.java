package com.qinhu.oneschool.DB;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {
    private String avatar;          //头像
    private String nickname;          //昵称
    private String sex;           //性别
    private String birthday;      //生日
    private String token;          //融云token
    private String signturn;

    private String truename;   //真实姓名

    private Student stu;


    public String getAvatar(){return avatar;}

    public void setAvatar(String avatar){this.avatar = avatar;}

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSignturn() {
        return signturn;
    }

    public void setSignturn(String signturn) {
        this.signturn = signturn;
    }


    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public Student getStu() {
        return stu;
    }

    public void setStu(Student stu) {
        this.stu = stu;
    }
}
