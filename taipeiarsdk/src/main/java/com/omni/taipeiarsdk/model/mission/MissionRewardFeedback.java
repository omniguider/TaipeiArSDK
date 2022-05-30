package com.omni.taipeiarsdk.model.mission;

import java.io.Serializable;

public class MissionRewardFeedback implements Serializable {

    private String result;
    private String error_message;
    private String data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}