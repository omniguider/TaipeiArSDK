package com.omni.taipeiarsdk.util;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;
import com.omni.taipeiarsdk.SimpleArActivity;
import com.omni.taipeiarsdk.TaipeiArSDKActivity;
import com.omni.taipeiarsdk.model.tpe_location.IndexData;
import com.omni.taipeiarsdk.model.tpe_location.IndexFeedback;
import com.omni.taipeiarsdk.model.tpe_location.IndexPoi;
import com.omni.taipeiarsdk.model.tpe_location.Topic;
import com.omni.taipeiarsdk.network.NetworkManager;
import com.omni.taipeiarsdk.network.TpeArApi;
import com.wikitude.architect.ArchitectView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static com.omni.taipeiarsdk.TaipeiArSDKActivity.indexPoi_id;
import static com.omni.taipeiarsdk.TaipeiArSDKActivity.isMission;

public class PoiDataFromApplicationModelExtension extends ArchitectViewExtension implements LocationListener {

    private static Topic[] topics;
    private static IndexPoi[] indexPois;
    private static boolean refresh = false;

    public PoiDataFromApplicationModelExtension(Activity activity, ArchitectView architectView) {
        super(activity, architectView);
    }

    /**
     * If the POIs were already generated and sent to JavaScript.
     */
    private boolean injectedPois = false;

    /**
     * When the first location was received the POIs are generated and sent to the JavaScript code,
     * by using architectView.callJavascript.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.e("LOG", "onLocationChanged: " + "poiDataFromApplicationMode");
//        EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_AR_LOCATION, location));
        // radius in km ; 100 = 100km
        makeArPointsReq(location);
    }

    /**
     * The very basic LocationProvider setup of this sample app does not handle the following callbacks
     * to keep the sample app as small as possible. They should be used to handle changes in a production app.
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    private static double[] getRandomLatLonNearby(final double lat, final double lon) {
        return new double[]{lat + Math.random() / 5 - 0.1, lon + Math.random() / 5 - 0.1};
    }

    private static JSONArray generateTopicPoiInformation(final IndexFeedback indexFeedback) {

        final JSONArray pois = new JSONArray();
        // ensure these attributes are also used in JavaScript when extracting POI data
        final String ATTR_ID = "id";
        final String ATTR_NAME = "name";
        final String ATTR_DESCRIPTION = "description";
        final String ATTR_LATITUDE = "latitude";
        final String ATTR_LONGITUDE = "longitude";
        final String ATTR_ALTITUDE = "altitude";
        final String ATTR_SELECTED = "selected";
        final String ATTR_CATEGORY = "category";
        final String ATTR_FINISHED = "gridFinished";

//        topics = indexFeedback.getData().getTopic();
        if (TaipeiArSDKActivity.mIndexPOI.length == 0) {
            indexPois = indexFeedback.getPoi();
            refresh = false;
        } else {
            indexPois = TaipeiArSDKActivity.mIndexPOI;
            refresh = true;
        }

        // generates dataSize POIs
//        for (Topic topic : topics) {
//            final HashMap<String, String> poiInformation = new HashMap<String, String>();
//            poiInformation.put(ATTR_ID, topic.getId());
//            poiInformation.put(ATTR_NAME, topic.getName());
//            poiInformation.put(ATTR_DESCRIPTION, "");
//            poiInformation.put(ATTR_CATEGORY, topic.getName());
//
//            poiInformation.put(ATTR_LATITUDE, String.valueOf(topic.getLat()));
//            poiInformation.put(ATTR_LONGITUDE, String.valueOf(topic.getLng()));
//
//            final float UNKNOWN_ALTITUDE = -32768f;  // equals "AR.CONST.UNKNOWN_ALTITUDE" in JavaScript (compare AR.GeoLocation specification)
//            // Use "AR.CONST.UNKNOWN_ALTITUDE" to tell ARchitect that altitude of places should be on user level. Be aware to handle altitude properly in locationManager in case you use valid POI altitude value (e.g. pass altitude only if GPS accuracy is <7m).
//
//            poiInformation.put(ATTR_ALTITUDE, String.valueOf(UNKNOWN_ALTITUDE));
//            poiInformation.put(ATTR_SELECTED, "false");
//
//            pois.put(new JSONObject(poiInformation));
//        }

        for (IndexPoi indexPoi : indexPois) {
            final HashMap<String, String> poiInformation = new HashMap<String, String>();
            poiInformation.put(ATTR_ID, indexPoi.getId());
            poiInformation.put(ATTR_NAME, indexPoi.getName());
            poiInformation.put(ATTR_DESCRIPTION, "");
            poiInformation.put(ATTR_CATEGORY, indexPoi.getCategory().getTitle());
            poiInformation.put(ATTR_FINISHED, indexPoi.getGridFinished());

            poiInformation.put(ATTR_LATITUDE, String.valueOf(indexPoi.getLat()));
            poiInformation.put(ATTR_LONGITUDE, String.valueOf(indexPoi.getLng()));

            final float UNKNOWN_ALTITUDE = -32768f;  // equals "AR.CONST.UNKNOWN_ALTITUDE" in JavaScript (compare AR.GeoLocation specification)
            // Use "AR.CONST.UNKNOWN_ALTITUDE" to tell ARchitect that altitude of places should be on user level. Be aware to handle altitude properly in locationManager in case you use valid POI altitude value (e.g. pass altitude only if GPS accuracy is <7m).

            poiInformation.put(ATTR_ALTITUDE, String.valueOf(UNKNOWN_ALTITUDE));
            if (isMission.equals("true")) {
                if (indexPoi_id.equals(indexPoi.getId())) {
                    poiInformation.put(ATTR_SELECTED, "true");
                }
            } else
                poiInformation.put(ATTR_SELECTED, "false");

            pois.put(new JSONObject(poiInformation));
        }

        return pois;
    }

    private void makeArPointsReq(Location location) {
        if (!injectedPois) {
            TpeArApi.getInstance().getSpecificPoi(activity, "poi", location,
                    new NetworkManager.NetworkManagerListener<IndexFeedback>() {
                        @Override
                        public void onSucceed(IndexFeedback feedback) {
                            final JSONArray jsonArray = generateTopicPoiInformation(feedback);
                            architectView.callJavascript("World.loadPoisFromJsonData(" +
                                    jsonArray.toString() +
                                    "," + refresh + ")"); // Triggers the loadPoisFromJsonData function
                            injectedPois = true; // avoiding loading pois again
                        }

                        @Override
                        public void onFail(VolleyError error, boolean shouldRetry) {
                            //DialogTools.getInstance().showErrorMessage(activity, "API Error", "get news data error");
                        }
                    });
        }
    }
}
