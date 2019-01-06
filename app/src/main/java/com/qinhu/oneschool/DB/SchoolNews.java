package com.qinhu.oneschool.DB;

import cn.bmob.v3.BmobObject;

public class SchoolNews extends BmobObject {
    private String url;
    private String title;
    private String time;


    public SchoolNews(String title,String time,String url){
        this.title = title;
        this.time = time;
        this.url = url;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
