package com.omni.taipeiarsdk.model.tpe_location;

import java.io.Serializable;

public class ThemeData implements Serializable {

    private String id;
    private String name;
    private String description;
    private String image;
    private Category category;
    private IndexPoi[] pois;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public IndexPoi[] getPoi() {
        return pois;
    }
}
