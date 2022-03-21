package com.omni.taipeiarsdk.tool;

import android.hardware.SensorManager;

public class TaipeiArSDKText {

    public static final String LOG_TAG = "TY_LOG";

    public static final String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    public static final String PREHISTORY_1F_FLOOR_PLAN_ID = "17617545-0351-495e-96d9-141b4c9e7a88";
    public static final String PREHISTORY_2F_FLOOR_PLAN_ID = "bff5929c-1ca8-4da5-acfa-6ed7cea525b2";
    public static final String PREHISTORY_3F_FLOOR_PLAN_ID = "df117f15-14bf-430c-b714-e75e7708dce7";

    public static final String KEY_NOTIFICATION_GEOFENCE_CONTENT = "key_notification_geofence_content";

    public static final String KEY_MISSION_DESCRIPTION = "key_mission_description";

    public static final String USER_OUTDOOR = "outdoor";

    public static final String TAB_NAME_HOME = "tab_name_home";
    public static final String TAB_NAME_INFORMATION = "tab_name_information";
    public static final String TAB_NAME_LOCATED_NAVIGATION = "tab_name_located_navigation";
    public static final String TAB_NAME_MULTI_NAVIGATION = "tab_name_multi_navigation";
    public static final String TAB_NAME_PANO = "tab_name_pano";
    public static final String TAB_NAME_SETTINGS = "tab_name_settings";

    public static final String EMERGENCY_TYPE_AED_ROUTE = "First Aid/AED";
    public static final String EMERGENCY_TYPE_INFO_DESK_ROUTE = "";
    public static final String EMERGENCY_TYPE_ENTRANCE_ROUTE = "Entrance";
    public static final String EMERGENCY_TYPE_FIRE_EXTINGUISHER_ROUTE = "Fire Extinguisher";

    public static final String KEY_DEVICE_ID = "device_id";

    public static final int TILE_WIDTH = 256;
    public static final int TILE_HEIGHT = 256;
    public static final int REQUEST_COARSE_LOCATION = 9;
    public static final int REQUEST_FINE_LOCATION = 10;
    public static final int REQUEST_CAMERA = 11;

    public static int MAP_CURRENT_POS_ZOOM = 19;
    public static final int MAP_ZOOM_LEVEL = 18;
    public static final int MAP_ZOOM_LEVEL_WEBSITE = 19;
    public static final int MAP_ZOOM_LEVEL_MISSION = 20;
    public static final int MARKER_Z_INDEX = 150;
    public static final int MAP_EXT_MIN_ZOOM_LEVEL = 10;
    public static final int MAP_MIN_ZOOM_LEVEL = 15;
    public static final int MAP_MAX_ZOOM_LEVEL = 22;
    public static final float POLYLINE_WIDTH = 50.0f;
    public static final int POLYLINE_Z_INDEX = 100;
    public static final int NAVIMARKER_Z_INDEX = 200;

    public static final String KEY_WEB_LINK = "arg_webview_activity_link";
    public static final String KEY_WEB_TITLE = "arg_webview_activity_title";

    public static int ROTATION_SENSOR_RATE = SensorManager.SENSOR_DELAY_GAME;

}
