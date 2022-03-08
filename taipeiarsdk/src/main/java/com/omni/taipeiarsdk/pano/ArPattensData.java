package com.omni.taipeiarsdk.pano;

import java.io.Serializable;

public class ArPattensData implements Serializable {

    private String ap_puid;
    private String Is_civil;

    private String ta_name;
    private String name;
    private String content_type;
    private String ext;

    private String image_path;
    private String content;
    private String url_image;
    private String angle;

    private String id;
    private String heigh;
    private String interactive_text;
    private String interactive_url;
    private String cover_idnetify_image;
    private String size;
    private String isTransparent;

    public String getId() {
        return id;
    }

    public String getHeigh() {
        return heigh;
    }
    public String getInteractive_text() {
        return interactive_text;
    }
    public String getInteractive_url() {
        return interactive_url;
    }
    public String getCover_idnetify_image() {
        return cover_idnetify_image;
    }
    public String getSize() {
        return size;
    }
    public String getIsTransparent() {
        return isTransparent;
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

    public String getTa_name() {
        return ta_name;
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

    public String getImage_path() {
        return image_path;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public String getAngle() {
        return angle;
    }
}
