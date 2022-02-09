package com.omni.taipeiarsdk.pano;

import java.io.Serializable;

public class ArPattenData implements Serializable {

    private String tdar_id;
    private String ap_puid;
    private String Is_civil;

    private String name;
    private String content_type;
    private String ext;

    private String content;
    private String url_image;
    private String angle;

    private String tdar_interactive_text;
    private String tdar_interactive_url;

    private String tdar_view_size;

    public String getTdar_interactive_text() {
        return tdar_interactive_text;
    }

    public String getTdar_interactive_url() {
        return tdar_interactive_url;
    }

    public String getTdar_id() {
        return tdar_id;
    }


    public String getAp_puid() {
        return ap_puid;
    }

    public void setAp_puid(String ap_puid) {
        this.ap_puid = ap_puid;
    }

    public String getIs_civil() {
        return Is_civil;
    }

    public void setIs_civil(String is_civil) {
        Is_civil = is_civil;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public String getAngle() {
        return angle;
    }

    public String getTdar_view_size() {
        return tdar_view_size;
    }
}
