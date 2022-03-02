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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getCategory() {
        return category.getTitle();
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
}

class Category implements Serializable {
    private String id;
    private String title;
    private String image;

    public String getTitle() {
        return title;
    }
}

class ArTrigger implements Serializable {
    private String active_method;
    private String distance;
    private String identify_image_path;
}

class Ar implements Serializable {
    private String text;
    private String url;
    private String heigh;
    private String content_type;
    private String interactive_text;
    private String interactive_url;
}