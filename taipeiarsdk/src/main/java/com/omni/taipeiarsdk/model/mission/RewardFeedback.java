package com.omni.taipeiarsdk.model.mission;

import java.io.Serializable;

public class RewardFeedback implements Serializable {

    private String result;
    private String error_message;
    private RewardData[] data;

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

    public RewardData[] getData() {
        return data;
    }

    public void setData(RewardData[] data) {
        this.data = data;
    }
}
