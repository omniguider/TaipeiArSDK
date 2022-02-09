package com.omni.taipeiarsdk.util;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.omni.taipeiarsdk.model.OmniEvent;
import com.wikitude.architect.ArchitectView;

import org.greenrobot.eventbus.EventBus;

public class PoiDataFromApplicationModelExtension extends ArchitectViewExtension implements LocationListener {

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
        EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_AR_LOCATION, location));
        // radius in km ; 100 = 100km
        makeArPointsReq();

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


    private void makeArPointsReq() {

    }
}
