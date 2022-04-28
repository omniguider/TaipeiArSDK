package com.omni.taipeiarsdk.model.tpe_location;

import java.io.Serializable;

public class ThemeFeedback implements Serializable {

    private String result;
    private String code;
    private String error_message;
    private ThemeData[] data;

    public ThemeData[] getData() {
        return data;
    }

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

}
