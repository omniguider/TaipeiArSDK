package com.omni.taipeiarsdk.model.tpe_location;

import java.io.Serializable;

public class Ar implements Serializable {
    private String text;
    private String url;
    private String heigh;
    private String content_type;
    private String interactive_text;
    private String interactive_url;
    private String content;
    private String size;
    private String isTransparent;
    private String cover_idnetify_image;

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public String getHeigh() {
        return heigh;
    }

    public String getContent_type() {
        return content_type;
    }

    public String getInteractive_text() {
        return interactive_text;
    }

    public String getInteractive_url() {
        return interactive_url;
    }

    public String getContent() {
        return content;
    }

    public String getSize() {
        return size;
    }

    public String getIsTransparent() {
        return isTransparent;
    }

    public String getCover_idnetify_image() {
        return cover_idnetify_image;
    }
}
