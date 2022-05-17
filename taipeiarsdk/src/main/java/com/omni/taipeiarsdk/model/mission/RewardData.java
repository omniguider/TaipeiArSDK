package com.omni.taipeiarsdk.model.mission;

import java.io.Serializable;

public class RewardData implements Serializable {

    private String m_id;
    private String rw_id;
    private String dep_id;
    private String m_title;
    private String m_img;
    private String rw_title;
    private String rw_img;
    private String rw_describe;
    private String rw_num;
    private String rw_remainng;
    private String grid_now;
    private boolean is_finish;
    private String rws_enabled;
    private String verify_code;

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public String getRW_id() {
        return rw_id;
    }

    public void setRW_id(String rw_id) {
        this.rw_id = rw_id;
    }

    public String getM_title() {
        return m_title;
    }

    public void setM_title(String m_title) {
        this.m_title = m_title;
    }

    public String getRW_title() {
        return rw_title;
    }

    public void setRW_title(String rw_title) {
        this.rw_title = rw_title;
    }

    public String getRW_describe() {
        return rw_describe;
    }

    public void setRW_describe(String rw_describe) {
        this.rw_describe = rw_describe;
    }

    public String getM_img() {
        return m_img;
    }

    public void setM_img(String m_img) {
        this.m_img = m_img;
    }

    public String getRW_img() {
        return rw_img;
    }

    public void setRW_img(String rw_img) {
        this.rw_img = rw_img;
    }

    public String getRW_num() {
        return rw_num;
    }

    public void setRW_num(String rw_num) {
        this.rw_num = rw_num;
    }

    public String getRW_remainng() {
        return rw_remainng;
    }

    public void setRW_remainng(String rw_remainng) {
        this.rw_remainng = rw_remainng;
    }

    public String getGrid_now() {
        return grid_now;
    }

    public void setGrid_now(String grid_now) {
        this.grid_now = grid_now;
    }

    public boolean getIs_finish() {
        return is_finish;
    }

    public void setIs_finish(boolean is_finish) {
        this.is_finish = is_finish;
    }

    public String getRws_enabled() {
        return rws_enabled;
    }

    public void setRws_enabled(String rws_enabled) {
        this.rws_enabled = rws_enabled;
    }
}
