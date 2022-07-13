package com.omni.taipeiarsdk.model.tpe_location;

import java.io.Serializable;

public class ArTrigger implements Serializable {
    private String active_method;
    private String distance;
    private String identify_image_path;

    public String getActive_method() {
        return active_method;
    }

    public String getDistance() {
        return distance;
    }

    public String getIdentify_image_path() {
        return identify_image_path;
    }
}
