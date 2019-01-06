package com.qinhu.oneschool.DB;

import cn.bmob.v3.BmobObject;

public class Friend extends BmobObject {
    private int id;
    private String user;
    private String friendUser;
    private String nickName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(String friendUser) {
        this.friendUser = friendUser;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
