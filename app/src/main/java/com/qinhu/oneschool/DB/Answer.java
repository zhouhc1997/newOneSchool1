package com.qinhu.oneschool.DB;

import cn.bmob.v3.BmobObject;

public class Answer extends BmobObject {
    private String content;
    private String question;
    private MyUser user;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
