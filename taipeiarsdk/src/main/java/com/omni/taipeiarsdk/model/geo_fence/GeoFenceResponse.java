package com.omni.taipeiarsdk.model.geo_fence;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeoFenceResponse implements Serializable {

    @SerializedName("geofence")
    private GeoFenceInfo[] geoFenceInfos;

    public GeoFenceInfo[] getGeoFenceInfos() {
        return geoFenceInfos;
    }
}
