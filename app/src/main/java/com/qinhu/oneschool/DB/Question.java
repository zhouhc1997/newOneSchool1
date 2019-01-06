package com.qinhu.oneschool.DB;

import cn.bmob.v3.BmobObject;

public class Question extends BmobObject {
    private String content;
    private Answer agree;
    private Integer likes;
    private Integer comments;
    private MyUser user;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Answer getAgree() {
        return agree;
    }

    public void setAgree(Answer agree) {
        this.agree = agree;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }
}
