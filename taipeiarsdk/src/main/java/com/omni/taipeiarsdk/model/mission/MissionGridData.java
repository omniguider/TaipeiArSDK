package com.omni.taipeiarsdk.model.mission;

import java.io.Serializable;

public class MissionGridData implements Serializable {

    private int grid_now;
    private boolean is_finish;
    private GridData[] nine_grid;
    private String mission_title;
    private String mission_describe;
    private String mission_end_time;
    private String rws_enabled;
    private String reward_name;
    private String reward_remainng;

    public int getGrid_now() {
        return grid_now;
    }

    public void setGrid_now(int grid_now) {
        this.grid_now = grid_now;
    }

    public boolean getIs_finish() {
        return is_finish;
    }

    public void setIs_finish(boolean is_finish) {
        this.is_finish = is_finish;
    }

    public GridData[] getNine_grid() {
        return nine_grid;
    }

    public void setNine_grid(GridData[] nine_grid) {
        this.nine_grid = nine_grid;
    }

    public String getMission_title() {
        return mission_title;
    }

    public String getMission_describe() {
        return mission_describe;
    }

    public void setMission_title(String mission_title) {
        this.mission_title = mission_title;
    }

    public String getMission_end_time() {
        return mission_end_time;
    }

    public void setMission_end_time(String mission_end_time) {
        this.mission_end_time = mission_end_time;
    }

    public String getRws_enabled() {
        return rws_enabled;
    }

    public void setRws_enabled(String rws_enabled) {
        this.rws_enabled = rws_enabled;
    }

    public String getReward_name() {
        return reward_name;
    }

    public void setReward_name(String reward_name) {
        this.reward_name = reward_name;
    }

    public String getReward_remainng() {
        return reward_remainng;
    }

    public void setReward_remainng(String reward_remainng) {
        this.reward_remainng = reward_remainng;
    }
}
