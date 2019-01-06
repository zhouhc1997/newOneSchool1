package com.qinhu.oneschool.Litepal;

import org.litepal.crud.DataSupport;

public class testLitepal extends DataSupport {
    private int id;

    private String testId;//题目所属题库编号

    private String objectId;//题目编号;

    private String trueAnswer;//正确答案代号；(多选有多个)

    private String flag;//标志单选还是多选;//多选需要提交才能验证通过;

    private String text;//题目内容;

    private String textA;//A选项解释;

    private String textB;//B

    private String textC;//C

    private String textD;//D

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTrueAnswer() {
        return trueAnswer;
    }

    public void setTrueAnswer(String trueAnswer) {
        this.trueAnswer = trueAnswer;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextA() {
        return textA;
    }

    public void setTextA(String textA) {
        this.textA = textA;
    }

    public String getTextB() {
        return textB;
    }

    public void setTextB(String textB) {
        this.textB = textB;
    }

    public String getTextC() {
        return textC;
    }

    public void setTextC(String textC) {
        this.textC = textC;
    }

    public String getTextD() {
        return textD;
    }

    public void setTextD(String textD) {
        this.textD = textD;
    }
}
