package com.omni.taipeiarsdk.model.geo_fence;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GeoFenceTrigger implements Serializable {

    transient SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH");

    @SerializedName("id")
    private int id;
    @SerializedName("type")
    private String type;
    @SerializedName("title")
    private String title;
    @SerializedName("desc")
    private String desc;
    @SerializedName("image")
    private String imageUrl;
    @SerializedName("start")
    private String startTime;
    @SerializedName("end")
    private String endTime;
    @SerializedName("timelimit_push")
    private String timeLimitPush;

    private boolean isUnread = false;
    private long lastPushTimeInMillis;
    private String lastPushDate;

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getTimeLimitPush() {
        return timeLimitPush;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    public String getInfoKey() {
        return getId() + "";
    }

    public long getLastPushTimeInMillis() {
        return lastPushTimeInMillis;
    }

    public String getLastPushDate() {
        return lastPushDate;
    }

    public void setLastPushDate(String lastPushDate) {
        this.lastPushDate = lastPushDate;
    }

    public void initPushDate() {
        if (!TextUtils.isEmpty(lastPushDate)) {
            return;
        }

        Date now = Calendar.getInstance().getTime();
        lastPushTimeInMillis = now.getTime();

        lastPushDate = dateFormat.format(now);
    }

}
