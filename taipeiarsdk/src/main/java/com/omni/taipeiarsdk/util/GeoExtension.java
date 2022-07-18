package com.omni.taipeiarsdk.util;

import android.app.Activity;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.wikitude.architect.ArchitectView;

import org.greenrobot.eventbus.EventBus;

import static com.omni.taipeiarsdk.tool.TaipeiArSDKText.LOG_TAG;

public class GeoExtension extends ArchitectViewExtension implements LocationListener, IARegion.Listener, IALocationListener {

    /**
     * Very basic location provider to enable location updates.
     * Please note that this approach is very minimal and we recommend to implement a more
     * advanced location provider for your app. (see https://developer.android.com/training/location/index.html)
     */
    private LocationProvider locationProvider;
    private boolean mIsIndoor = false;

    /**
     * Error callback of the LocationProvider, noProvidersEnabled is called when neither location over GPS nor
     * location over the network are enabled by the device.
     */
    private final LocationProvider.ErrorCallback errorCallback = new LocationProvider.ErrorCallback() {
        @Override
        public void noProvidersEnabled() {
            Toast.makeText(activity, "no_location_provider", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * The ArchitectView.SensorAccuracyChangeListener notifies of changes in the accuracy of the compass.
     * This can be used to notify the user that the sensors need to be recalibrated.
     * <p>
     * This listener has to be registered after onCreate and unregistered before onDestroy in the ArchitectView.
     */
    private final ArchitectView.SensorAccuracyChangeListener sensorAccuracyChangeListener = new ArchitectView.SensorAccuracyChangeListener() {
        @Override
        public void onCompassAccuracyChanged(int accuracy) {
            if (accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM) { // UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3
                Toast.makeText(activity, "compass_accuracy_low", Toast.LENGTH_LONG).show();
            }
        }
    };

    private LocationListener locationListenerExtension;

    public GeoExtension(Activity activity, ArchitectView architectView) {
        super(activity, architectView);
    }


    private IALocationManager mIALocationManager;

    @Override
    public void onCreate() {
        locationProvider = new LocationProvider(activity, this, errorCallback);

        initIALocationManager();
    }

    private void initIALocationManager() {
        if (mIALocationManager == null) {
            mIALocationManager = IALocationManager.create(activity.getApplication());
            mIALocationManager.lockIndoors(true);
        } else {
            mIALocationManager.unregisterRegionListener(this);
        }
        mIALocationManager.registerRegionListener(this);

        IALocationRequest request = IALocationRequest.create();
        request.setFastestInterval(500);
        request.setSmallestDisplacement(0.6f);

        mIALocationManager.removeLocationUpdates(this);
        mIALocationManager.requestLocationUpdates(request, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIALocationManager != null) {
            mIALocationManager.destroy();
            mIALocationManager = null;
        }
    }

    @Override
    public void onResume() {
        locationProvider.onResume();
        /*
         * The SensorAccuracyChangeListener has to be registered to the Architect view after ArchitectView.onCreate.
         * There may be more than one SensorAccuracyChangeListener.
         */
        architectView.registerSensorAccuracyChangeListener(sensorAccuracyChangeListener);
    }

    @Override
    public void onPause() {
        locationProvider.onPause();
        // The SensorAccuracyChangeListener has to be unregistered from the Architect view before ArchitectView.onDestroy.
        architectView.unregisterSensorAccuracyChangeListener(sensorAccuracyChangeListener);
    }

    /**
     * The ArchitectView has to be notified when the location of the device
     * changed in order to accurately display the Augmentations for Geo AR.
     * <p>
     * The ArchitectView has two methods which can be used to pass the Location,
     * it should be chosen by whether an altitude is available or not.
     */
    @Override
    public void onLocationChanged(Location location) {
        float accuracy = location.hasAccuracy() ? location.getAccuracy() : 1000;
        if (!mIsIndoor) {
//            if (location.hasAltitude()) {
//                architectView.setLocation(location.getLatitude(), location.getLongitude(), location.getAltitude(), accuracy);
//            } else {
//                architectView.setLocation(location.getLatitude(), location.getLongitude(), accuracy);
//            }
            architectView.setLocation(location.getLatitude(), location.getLongitude(), 10, accuracy);
            if (locationListenerExtension != null) {
                locationListenerExtension.onLocationChanged(location);
            }
        }
    }

    @Override
    public void onLocationChanged(IALocation iaLocation) {
        float accuracy = iaLocation.toLocation().hasAccuracy() ? iaLocation.getAccuracy() : 1000;
        if (mIsIndoor && iaLocation.getFloorCertainty() > 0.8) {
//            if (iaLocation.toLocation().hasAltitude()) {
//                architectView.setLocation(iaLocation.getLatitude(), iaLocation.getLongitude(), iaLocation.getAltitude(), accuracy);
//            } else {
//                architectView.setLocation(iaLocation.getLatitude(), iaLocation.getLongitude(), accuracy);
//            }

            architectView.setLocation(iaLocation.getLatitude(), iaLocation.getLongitude(), 10, accuracy);
            if (locationListenerExtension != null) {
                locationListenerExtension.onLocationChanged(iaLocation.toLocation());
            }
        }
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

    public void setLocationListenerExtension(LocationListener extension) {
        locationListenerExtension = extension;
    }

    @Override
    public void onEnterRegion(IARegion iaRegion) {
        Log.e(LOG_TAG, "GeoExtension onEnterRegion");
        if (iaRegion.getType() == IARegion.TYPE_UNKNOWN) {
            mIsIndoor = false;
        } else if (iaRegion.getType() == IARegion.TYPE_VENUE) {
            mIsIndoor = false;
        } else if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN) {
            mIsIndoor = true;
        }
    }

    @Override
    public void onExitRegion(IARegion iaRegion) {
        if (iaRegion.getType() == IARegion.TYPE_UNKNOWN) {
            mIsIndoor = false;
        } else if (iaRegion.getType() == IARegion.TYPE_VENUE) {
            mIsIndoor = false;
        } else if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN) {
            mIsIndoor = false;
        }
    }
}
