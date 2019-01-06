package com.qinhu.oneschool.DB;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class Device extends BmobObject {
    private String content;
    private List<String> imgPath;

    private MyUser user;                   //关联用户

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImgPath() {
        return imgPath;
    }

    public void setImgPath(List<String> imgPath) {
        this.imgPath = imgPath;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }
}
