package com.qinhu.oneschool.DB;

import java.io.File;

import cn.bmob.v3.BmobObject;

public class VersionUpdate extends BmobObject{
    private Boolean isMust;
    private Integer version_n;
    private String version;
    private String path;
    private String update_log;
    private float size;

    public Boolean getMust() {
        return isMust;
    }

    public void setMust(Boolean must) {
        isMust = must;
    }

    public Integer getVersion_n() {
        return version_n;
    }

    public void setVersion_n(Integer version_n) {
        this.version_n = version_n;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getUpdate_log() {
        return update_log;
    }

    public void setUpdate_log(String update_log) {
        this.update_log = update_log;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
