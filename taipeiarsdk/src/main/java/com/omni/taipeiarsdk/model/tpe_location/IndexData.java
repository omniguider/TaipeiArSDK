package com.omni.taipeiarsdk.model.tpe_location;

import java.io.Serializable;

public class IndexData implements Serializable {

    private Topic[] topic;
    private IndexPoi[] poi;

    public Topic[] getTopic() {
        return topic;
    }

    public void setTopic(Topic[] topic) {
        this.topic = topic;
    }

    public IndexPoi[] getPoi() {
        return poi;
    }

    public void setPoi(IndexPoi[] poi) {
        this.poi = poi;
    }
}
