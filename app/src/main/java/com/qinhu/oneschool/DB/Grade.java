package com.qinhu.oneschool.DB;

import cn.bmob.v3.BmobObject;

public class Grade extends BmobObject {
    private String projectname;
    private String grade;
    private MyUser user;

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }
}
