package com.omni.taipeiarsdk.model;

import java.io.Serializable;

public class GetWtcFeedback implements Serializable {

    private String result;
    private String code;
    private String error_message;
    private WtcData data;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public WtcData getData() {
        return data;
    }

    public void setData(WtcData data) {
        this.data = data;
    }
}
