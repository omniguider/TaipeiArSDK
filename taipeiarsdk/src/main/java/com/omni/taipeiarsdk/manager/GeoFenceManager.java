package com.omni.taipeiarsdk.manager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.android.volley.VolleyError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.omni.taipeiarsdk.TaipeiArSDKActivity;
import com.omni.taipeiarsdk.model.geo_fence.GeoFenceInfo;
import com.omni.taipeiarsdk.model.geo_fence.GeoFenceResponse;
import com.omni.taipeiarsdk.model.geo_fence.GeoFenceTrigger;
import com.omni.taipeiarsdk.network.NetworkManager;
import com.omni.taipeiarsdk.network.TpeArApi;
import com.omni.taipeiarsdk.tool.PreferencesTools;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.omni.taipeiarsdk.tool.TaipeiArSDKText.LOG_TAG;

public class GeoFenceManager implements ResultCallback {

    private static GeoFenceManager sGeoFenceManager;

    private GeoFenceResponse mGeoFenceResponses;
    private PendingIntent mGeoFencePendingIntent;
    private int GEOFENCE_REQ_CODE = 33;
    private Gson mGson = new Gson();
    Type type = new TypeToken<Map<String, GeoFenceTrigger>>() {
    }.getType();

    public static GeoFenceManager getInstance() {
        if (sGeoFenceManager == null) {
            sGeoFenceManager = new GeoFenceManager();
        }
        return sGeoFenceManager;
    }

    public Geofence createGeofence(GeoFenceInfo geoFenceInfo) {
        return new Geofence.Builder()
                .setRequestId(geoFenceInfo.getId() + "")
                .setCircularRegion(geoFenceInfo.getLat(), geoFenceInfo.getLng(), geoFenceInfo.getRange())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
//                        | Geofence.GEOFENCE_TRANSITION_DWELL
//                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(7 * 24 * 60 * 60 * 1000)
                .setLoiteringDelay(10 * 1000)
                .build();
    }

    public GeofencingRequest createGeofenceRequest(List<Geofence> geofenceList) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER
//                        | GeofencingRequest.INITIAL_TRIGGER_DWELL
                        | GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofences(geofenceList)
                .build();
    }

//    public void getGeoFenceDataAndRegister(final Activity activity, final GoogleApiClient googleApiClient) {
//        TpeArApi.getInstance().getGeoFenceData(activity, new NetworkManager.NetworkManagerListener<GeoFenceResponse>() {
//            @Override
//            public void onSucceed(GeoFenceResponse responses) {
//                Log.e(LOG_TAG, "getGeoFenceDataAndRegister");
//                if (responses != null && responses.getGeoFenceInfos() != null) {
//                    mGeoFenceResponses = responses;
//
//                    List<Geofence> geofenceList = new ArrayList<>();
//                    Map<String, GeoFenceInfo> map = new ArrayMap<>();
//
//                    for (GeoFenceInfo info : responses.getGeoFenceInfos()) {
//                        Geofence geofence = createGeofence(info);
//                        geofenceList.add(geofence);
//                        map.put(info.getInfoKey(), info);
//                        TaipeiArSDKActivity.geofenceList.add(info);
//                    }
////                    GeofencingRequest geofenceRequest = createGeofenceRequest(geofenceList);
////                    addGeoFence(activity, googleApiClient, geofenceRequest);
//                }
//            }
//
//            @Override
//            public void onFail(VolleyError error, boolean shouldRetry) {
//
//            }
//
//        });
//    }

    public void getGeoFenceData(Activity activity, Location location) {
        TpeArApi.getInstance().getGeoFenceData(activity,
                location.getLatitude(),
                location.getLongitude(),
                new NetworkManager.NetworkManagerListener<GeoFenceResponse>() {
                    @Override
                    public void onSucceed(GeoFenceResponse responses) {
                        Log.e(LOG_TAG, "getGeoFenceData");
                        if (responses != null && responses.getGeoFenceInfos() != null) {
                            mGeoFenceResponses = responses;

                            List<Geofence> geofenceList = new ArrayList<>();
                            Map<String, GeoFenceInfo> map = new ArrayMap<>();

                            for (GeoFenceInfo info : responses.getGeoFenceInfos()) {
                                Geofence geofence = createGeofence(info);
                                geofenceList.add(geofence);
                                map.put(info.getInfoKey(), info);
                                TaipeiArSDKActivity.geofenceList.add(info);
                            }
                        }
                    }

                    @Override
                    public void onFail(VolleyError error, boolean shouldRetry) {

                    }

                });
    }

    public Map<String, GeoFenceTrigger> getAllGeoFenceInfoAll(Context context) {
        return PreferencesTools.getInstance().getProperties(context,
                PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY_ALL,
                type, String.class, GeoFenceTrigger.class);
    }

    public Map<String, GeoFenceTrigger> getAllGeoFenceInfo(Context context) {
        return PreferencesTools.getInstance().getProperties(context,
                PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY,
                type, String.class, GeoFenceTrigger.class);
    }

    public Map<String, GeoFenceTrigger> getAllGeoFenceInfo(Activity activity) {
        return PreferencesTools.getInstance().getProperties(activity,
                PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY,
                type, String.class, GeoFenceTrigger.class);
    }

    public Map<String, GeoFenceTrigger> getUnreadGeoFenceInfo(Activity activity) {
        return PreferencesTools.getInstance().getProperties(activity,
                PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY_UNREAD,
                type, String.class, GeoFenceTrigger.class);
    }

    public void addGeoFenceToPreference(Context context, Map<String, GeoFenceTrigger> newMap) {
        String valueStr = PreferencesTools.getInstance().getProperty(context, PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY);
        Map<String, GeoFenceTrigger> allGeoFenceInfoMap;
        if (TextUtils.isEmpty(valueStr)) {
            allGeoFenceInfoMap = new ArrayMap<>();
        } else {
            allGeoFenceInfoMap = mGson.fromJson(valueStr, type);
        }

        /**
         * Filter out unread geo fence info.
         * */
        Map<String, GeoFenceTrigger> unreadMap = null;

        for (String key : newMap.keySet()) {
            if (!allGeoFenceInfoMap.containsKey(key)) {

                GeoFenceTrigger data = newMap.get(key);
                data.setUnread(true);
//                data.initPushDate();

                if (unreadMap == null) {
                    unreadMap = new ArrayMap<>();
                    unreadMap.put(key, data);
                }
            }
        }

        if (unreadMap != null) {
            addUnreadGeoFenceInfo(context, unreadMap);
        }

        newMap.keySet().removeAll(allGeoFenceInfoMap.keySet());
        allGeoFenceInfoMap.putAll(newMap);

        PreferencesTools.getInstance().saveProperty(context,
                PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY,
                allGeoFenceInfoMap);

        if (newMap.size() != 0)
            PreferencesTools.getInstance().saveProperty(context,
                    PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY_ALL,
                    allGeoFenceInfoMap);
    }

    private void addUnreadGeoFenceInfo(Context context, Map<String, GeoFenceTrigger> newMap) {
        String valueStr = PreferencesTools.getInstance().getProperty(context, PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY_UNREAD);
        Map<String, GeoFenceTrigger> unreadMap;
        if (TextUtils.isEmpty(valueStr)) {
            unreadMap = new ArrayMap<>();
        } else {
            unreadMap = mGson.fromJson(valueStr, type);
        }

        newMap.keySet().removeAll(unreadMap.keySet());
        unreadMap.putAll(newMap);

        PreferencesTools.getInstance().saveProperty(context,
                PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY_UNREAD,
                unreadMap);
    }

    public void readGeoFenceData(Activity activity, String infoKey, int geoFenceInfoId) {
        String unreadGeoFenceMapStr = PreferencesTools.getInstance().getProperty(activity, PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY_UNREAD);
        if (TextUtils.isEmpty(unreadGeoFenceMapStr)) {
            return;
        }

        Map<String, GeoFenceTrigger> unreadGeoFenceInfoMap = mGson.fromJson(unreadGeoFenceMapStr, type);
        if (unreadGeoFenceInfoMap.containsKey(infoKey)) {
            GeoFenceTrigger unreadGeoFenceInfo = unreadGeoFenceInfoMap.get(infoKey);
            boolean isAllPushContentRead = true;

            if (unreadGeoFenceInfo.getId() == geoFenceInfoId) {
                unreadGeoFenceInfo.setUnread(false);
            }

            for (GeoFenceTrigger info : unreadGeoFenceInfoMap.values()) {
                if (info.isUnread()) {
                    isAllPushContentRead = false;
                }
            }

            if (isAllPushContentRead) {
                unreadGeoFenceInfoMap.remove(infoKey);
            }

            PreferencesTools.getInstance().saveProperty(activity,
                    PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY_UNREAD,
                    unreadGeoFenceInfoMap);
        }

        String allGeoFenceMapStr = PreferencesTools.getInstance().getProperty(activity, PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY);
        Map<String, GeoFenceTrigger> allGeoFenceMap = mGson.fromJson(allGeoFenceMapStr, type);
        GeoFenceTrigger data = allGeoFenceMap.get(infoKey);
        data.setUnread(false);

        PreferencesTools.getInstance().saveProperty(activity,
                PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY,
                allGeoFenceMap);
    }

    public void removeGeoFenceData(Activity activity, String id) {

        Map<String, GeoFenceTrigger> allGeoFenceInfoMap = getAllGeoFenceInfo(activity);
        if (allGeoFenceInfoMap.containsKey(id)) {
            allGeoFenceInfoMap.remove(id);

            PreferencesTools.getInstance().saveProperty(activity,
                    PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY,
                    allGeoFenceInfoMap);
        }

        Map<String, GeoFenceTrigger> unreadGeoFenceInfoMap = getUnreadGeoFenceInfo(activity);
        if (unreadGeoFenceInfoMap.containsKey(id)) {
            unreadGeoFenceInfoMap.remove(id);

            PreferencesTools.getInstance().saveProperty(activity,
                    PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY_UNREAD,
                    unreadGeoFenceInfoMap);
        }
    }

    public void removeAllGeoFenceData(Activity activity) {

        Map<String, GeoFenceTrigger> allGeoFenceInfoMap = getAllGeoFenceInfo(activity);
        allGeoFenceInfoMap.clear();

        PreferencesTools.getInstance().saveProperty(activity,
                PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY,
                allGeoFenceInfoMap);

        Map<String, GeoFenceTrigger> unreadGeoFenceInfoMap = getUnreadGeoFenceInfo(activity);
        unreadGeoFenceInfoMap.clear();

        PreferencesTools.getInstance().saveProperty(activity,
                PreferencesTools.KEY_GEO_FENCE_NOTIFICATION_HISTORY_UNREAD,
                unreadGeoFenceInfoMap);
    }

    @Override
    public void onResult(@NonNull Result result) {
    }
}
