package com.qinhu.oneschool.DB;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

public class Grades extends BmobObject{
    private String projectname;
    private String grade;
    private String xuefen;
    private String jidian;
    private Boolean jige;
    private String xueiq;
    private MyUser user;

    public Grades(){
    }

    public Grades(String projectname, String grade, String xuefen, String jidian, Boolean jige){
        this.projectname = projectname;
        this.grade = grade;
        this.xuefen = xuefen;
        this.jidian = jidian;
        this.jige = jige;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getXuefen() {
        return xuefen;
    }

    public void setXuefen(String xuefen) {
        this.xuefen = xuefen;
    }

    public String getJidian() {
        return jidian;
    }

    public void setJidian(String jidian) {
        this.jidian = jidian;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }


    public Boolean getJige() {
        return jige;
    }

    public void setJige(Boolean jige) {
        this.jige = jige;
    }

    public String getXueiq() {
        return xueiq;
    }

    public void setXueiq(String xueiq) {
        this.xueiq = xueiq;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }
}
