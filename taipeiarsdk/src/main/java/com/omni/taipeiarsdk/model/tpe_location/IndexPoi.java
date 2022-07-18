package com.omni.taipeiarsdk.model.tpe_location;

import java.io.Serializable;

public class IndexPoi implements Serializable {
    private String id;
    private String name;
    private String desc;
    private String locid;
    private String enabled;
    private String lat;
    private String lng;
    private String hyperlink_text;
    private String hyperlink_url;
    private String video_hyperlink;
    private String audio_path;
    private String image;
    private Category category;
    private ArTrigger ar_trigger;
    private Ar ar;
    private Beacon beacon;
    private String gridFinished;

    private float distance;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Category getCategory() {
        return category;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getImage() {
        return image;
    }

    public String getHyperlink_text() {
        return hyperlink_text;
    }

    public String getHyperlink_url() {
        return hyperlink_url;
    }

    public ArTrigger getAr_trigger() {
        return ar_trigger;
    }

    public Ar getAr() {
        return ar;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public String getGridFinished() {
        return gridFinished;
    }

    public void setGridFinished(String gridFinished) {
        this.gridFinished = gridFinished;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}

