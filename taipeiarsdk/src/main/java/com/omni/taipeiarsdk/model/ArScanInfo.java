package com.omni.taipeiarsdk.model;

import java.io.Serializable;

public class ArScanInfo implements Serializable {

    private String ar_id;
    private String c_id;
    private String e_id;
    private String ar_name;
    private String ar_image_path;
    private String ar_content_type;
    private String ar_content_path;
    private String ar_content_photo;
    private String ar_content_rotate;
    private String ar_axbundle;
    private String ar_axbundle_android_path;
    private String ar_axbundle_ios_path;
    private String ar_description;
    private String ar_characteristic;
    private String ar_characteristic2;
    private String ar_modle_size = "0.5";

    public String getArId() {
        return ar_id;
    }

    public String getCId() {
        return c_id;
    }

    public String getEId() {
        return e_id;
    }

    public String getArName() {
        return ar_name;
    }

    public String getArImagePath() {
        return ar_image_path;
    }

    public String getArContentType() {
        return ar_content_type;
    }

    public String getArContentPath() {
        return ar_content_path;
    }

    public String getArContentPhoto() {
        return ar_content_photo;
    }

    public String getArContentRotate() {
        return ar_content_rotate;
    }

    public String getArAxbundle() {
        return ar_axbundle;
    }

    public String getArModleSize() { return ar_modle_size; }
}
