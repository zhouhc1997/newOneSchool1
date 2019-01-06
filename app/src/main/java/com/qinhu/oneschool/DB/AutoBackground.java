package com.qinhu.oneschool.DB;

import java.util.Date;

import cn.bmob.v3.BmobObject;

public class AutoBackground extends BmobObject {
    private String imgPath;
    private String type;
    private Date update;

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
