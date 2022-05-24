package com.omni.taipeiarsdk.model.mission;

import com.omni.taipeiarsdk.model.tpe_location.IndexPoi;

import java.io.Serializable;

public class GridData implements Serializable {

    private String id;
    private String title;
    private String description;
    private String trigger;
    private String notice;
    private IndexPoi poi;
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

    public String getTrigger() {
        return trigger;
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

}
