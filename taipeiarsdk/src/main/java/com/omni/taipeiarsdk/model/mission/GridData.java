package com.omni.taipeiarsdk.model.mission;

import com.omni.taipeiarsdk.model.tpe_location.IndexPoi;

import java.io.Serializable;

public class GridData implements Serializable {

    private String id;
    private String title;
    private String description;
    private String notice;
    private String trigger_distance;
    private String pass_method;
    private IndexPoi poi;
    private Question question;
    private boolean is_complete;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTrigger_distance() {
        return trigger_distance;
    }

    public String getNotice() {
        return notice;
    }

    public IndexPoi getPoi() {
        return poi;
    }

    public boolean is_complete() {
        return is_complete;
    }

    public String getPass_method() {
        return pass_method;
    }

    public Question getQuestion() {
        return question;
    }
}
