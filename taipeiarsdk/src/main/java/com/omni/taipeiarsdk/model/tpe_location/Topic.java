package com.omni.taipeiarsdk.model.tpe_location;


import java.io.Serializable;

public class Topic implements Serializable {

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

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public Category getCategory() {
        return category;
    }

    public IndexPoi[] getPois() {
        return pois;
    }

}
