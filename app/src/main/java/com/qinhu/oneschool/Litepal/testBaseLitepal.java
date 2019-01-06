package com.qinhu.oneschool.Litepal;



import org.litepal.crud.DataSupport;

public class testBaseLitepal extends DataSupport {
    private int id;

    private String baseId;//题库编号；

    private String name;//题库名称;

    private String account;//题库所属账号;

    private int number;//目前所做的题数;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaseId() {
        return baseId;
    }

    public void setBaseId(String baseId) {
        this.baseId = baseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
