package com.omni.taipeiarsdk.model.tpe_location;

import java.io.Serializable;

public class Beacon implements Serializable {
    private String id;
    private String description;
    private String major;
    private String minor;
    private String mac;
    private String lat;
    private String lng;
    private String range;

    public String getText() {
        return id;
    }

    public String getUrl() {
        return description;
    }

    public String getHeigh() {
        return major;
    }

    public String getContent_type() {
        return minor;
    }

    public String getInteractive_text() {
        return mac;
    }

    public String getInteractive_url() {
        return lat;
    }

    public String getContent() {
        return lng;
    }

    public String getSize() {
        return range;
    }
}
