package com.omni.taipeiarsdk.model;

import java.io.Serializable;

public class WtcData implements Serializable {

    private String num;
    private String[] wtc;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String[] getWtc() {
        return wtc;
    }

    public void setWtc(String[] wtc) {
        this.wtc = wtc;
    }
}
