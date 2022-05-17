package com.omni.taipeiarsdk.model.mission;

import java.io.Serializable;

public class MissionData implements Serializable {

    private String m_id;
    private String dep_id;
    private String m_title;
    private String m_describe;
    private String m_img;
    private String m_start_time;
    private String m_end_time;
    private String m_enabled;
    private String grid_now;
    private boolean is_finish;
    private String rws_enabled;
    private String verify_code;

    public String getVerify_code() {
        return verify_code;
    }

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public String getM_title() {
        return m_title;
    }

    public void setM_title(String m_title) {
        this.m_title = m_title;
    }

    public String getM_describe() {
        return m_describe;
    }

    public void setM_describe(String m_describe) {
        this.m_describe = m_describe;
    }

    public String getM_img() {
        return m_img;
    }

    public void setM_img(String m_img) {
        this.m_img = m_img;
    }

    public String getM_start_time() {
        return m_start_time;
    }

    public void setM_start_time(String m_start_time) {
        this.m_start_time = m_start_time;
    }

    public String getM_end_time() {
        return m_end_time;
    }

    public void setM_end_time(String m_end_time) {
        this.m_end_time = m_end_time;
    }

    public String getM_enabled() {
        return m_enabled;
    }

    public void setM_enabled(String m_enabled) {
        this.m_enabled = m_enabled;
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
