package com.omni.taipeiarsdk.model.mission;

import java.io.Serializable;

public class MissionCompleteData implements Serializable {

    private String num_of_pois;
    private String status;
    private String message;
    private String status_code;
    private String mission_status;

    public String getNum_of_pois() {
        return num_of_pois;
    }

    public void setNum_of_pois(String num_of_pois) {
        this.num_of_pois = num_of_pois;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getMission_status() {
        return mission_status;
    }

    public void setMission_status(String mission_status) {
        this.mission_status = mission_status;
    }
}
