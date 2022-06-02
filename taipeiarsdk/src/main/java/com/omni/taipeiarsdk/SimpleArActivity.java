package com.omni.taipeiarsdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.button.MaterialButton;
import com.omni.taipeiarsdk.model.ArScanInfo;
import com.omni.taipeiarsdk.model.OmniEvent;
import com.omni.taipeiarsdk.model.mission.GridData;
import com.omni.taipeiarsdk.model.mission.MissionCompleteFeedback;
import com.omni.taipeiarsdk.model.tpe_location.Ar;
import com.omni.taipeiarsdk.model.tpe_location.IndexFeedback;
import com.omni.taipeiarsdk.model.tpe_location.IndexPoi;
import com.omni.taipeiarsdk.network.NetworkManager;
import com.omni.taipeiarsdk.network.TpeArApi;
import com.omni.taipeiarsdk.pano.ArPattensFeedback;
import com.omni.taipeiarsdk.tool.TaipeiArSDKText;
import com.omni.taipeiarsdk.util.SampleCategory;
import com.omni.taipeiarsdk.util.SampleData;
import com.omni.taipeiarsdk.util.TestExtension;
import com.omni.taipeiarsdk.view.GridViewItem;
import com.wikitude.architect.ArchitectJavaScriptInterfaceListener;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;
import com.wikitude.common.permission.PermissionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.omni.taipeiarsdk.TaipeiArSDKActivity.isMission;
import static com.omni.taipeiarsdk.TaipeiArSDKActivity.missionId;
import static com.omni.taipeiarsdk.TaipeiArSDKActivity.ng_id;
import static com.omni.taipeiarsdk.TaipeiArSDKActivity.userId;
import static com.wikitude.architect.ArchitectView.CaptureScreenCallback.CAPTURE_MODE_CAM_AND_WEBVIEW;

/**
 * This Activity is (almost) the least amount of code required to use the
 * basic functionality for Image-/Instant- and Object Tracking.
 * <p>
 * This Activity needs Manifest.permission.CAMERA permissions because the
 * ArchitectView will try to start the camera.
 */
public class SimpleArActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String INTENT_EXTRAS_KEY_SAMPLE = "sampleData";
    public static final String INTENT_EXTRAS_KEY_THEME_DATA = "themeData";
    public static final String INTENT_EXTRAS_KEY_THEME_TITLE = "themeTitle";
    public static final String INTENT_EXTRAS_KEY_MISSION_DATA = "missionData";
    public static final String INTENT_EXTRAS_KEY_NINE_GRID_DATA = "nineGridData";
    public static final String INTENT_EXTRAS_KEY_MISSION_TITLE = "missionTitle";
    private static final String TAG = SimpleArActivity.class.getSimpleName();

    /**
     * Root directory of the sample AR-Experiences in the assets dir.
     */
    private static final String SAMPLES_ROOT = "samples/";

    /**
     * The ArchitectView is the core of the AR functionality, it is the main
     * interface to the Wikitude SDK.
     * The ArchitectView has its own lifecycle which is very similar to the
     * Activity lifecycle.
     * To ensure that the ArchitectView is functioning properly the following
     * methods have to be called:
     * - onCreate(ArchitectStartupConfiguration)
     * - onPostCreate()
     * - onResume()
     * - onPause()
     * - onDestroy()
     * Those methods are preferably called in the corresponding Activity lifecycle callbacks.
     */
    protected ArchitectView architectView;

    /**
     * The path to the AR-Experience. This is usually the path to its index.html.
     */
    private String arExperience;
    private LinearLayout interactive_ll;
    private TextView interactive_text;
    private TextView interactive_url;
    private TextView title;
    private FrameLayout back_fl;
    private ImageView intro;
    private NetworkImageView target_hint;
    private FrameLayout mask_top;
    private FrameLayout mask_bottom;
    private FrameLayout focus_fl;
    private ImageView focus_hint1;
    private ImageView focus_hint2;
    private ImageView focus_hint3;
    private ImageView focus_hint4;
    private ToggleButton move_3d_model;
    private ToggleButton rotate_3d_model;
    private LinearLayout interactive_fl;
    private ImageView interactive;
    private ImageView reset;
    private ImageView rescan;
    private ImageView rescanVideo;
    private ImageView takePhoto;
    private ImageView switch_cam;
    private FrameLayout scan_fl;
    private MaterialButton mission_ar_recognized;
    private ConstraintLayout introView;
    private boolean introIsShowing = false;
    private String wtc;
    private ArScanInfo arScanInfo;
    private String contentType;
    private String result;
    private TextView back_tv;
    private LinearLayout ar_img_ll;
    private TextView ar_img_btn_tv;
    private TextView ar_img_support_btn_tv;

    private EventBus mEventBus;
    private SupportMapFragment mMapFragment;
    private Location mLastLocation;
    private boolean mIsMapInited = false;
    private GoogleMap mMap;
    private Marker mUserMarker;
    private Circle mUserAccuracyCircle;
    private Polyline mPolyline = null;
    private boolean autoCamera = true;
    private ImageView position;

    private SensorManager mSensorManager;
    private Handler mHeadingHandler;
    private HandlerThread mHeadingHandlerThread;
    boolean initHeadingHandler = false;
    private float[] mRotationMatrix = new float[16];
    private float azimuth;
    private double heading;
    private float mDeclination;
    private String currentHint;
    private ArPattensFeedback arPattensFeedback;
    private ShareGridAdapter adapter;
    private GridView gridView;
    private String selectPOIId;
    private IndexPoi[] mIndexPOI;
    private GridData[] mGridData;
    private IndexPoi indexPoi = null;
    private boolean currentGridPass = false;

    private List<SampleCategory> categories;
    private static final String sampleDefinitionsPath = "samples/samples.json";
    private final PermissionManager permissionManager = ArchitectView.getPermissionManager();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OmniEvent event) {
        switch (event.getType()) {
            case OmniEvent.TYPE_USER_AR_LOCATION:
                mLastLocation = (Location) event.getObj();

                GeomagneticField field = new GeomagneticField(
                        (float) mLastLocation.getLatitude(),
                        (float) mLastLocation.getLongitude(),
                        (float) mLastLocation.getAltitude(),
                        System.currentTimeMillis()
                );
                // getDeclination returns degrees
                mDeclination = field.getDeclination();

                showUserPosition();
                if (indexPoi != null) {
                    if (isMission.equals("true") &&
                            indexPoi.getAr_trigger().getActive_method().equals("0") &&
                            !currentGridPass)
                        detectArMission();
                }
                break;
            case OmniEvent.TYPE_USER_AR_INTERACTIVE_TEXT:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        interactive_text.setText(TestExtension.interactive_text);
                    }
                });
                break;
            case OmniEvent.TYPE_SHOW_AR_PATTERNS:
                arPattensFeedback = (ArPattensFeedback) event.getObj();
                gridView.setVisibility(View.VISIBLE);
                hidePatternHint();
//                setPatternGrid(arPattensFeedback);
                showArModel(0);
                break;
            case OmniEvent.TYPE_SHOW_AR_MODEL:
//                showArModel(TaipeiArSDKActivity.arContent);
                break;
        }
    }

    private void setPatternGrid(ArPattensFeedback feedback) {
        String[] title = new String[feedback.getData().length];
        String[] ta_name = new String[feedback.getData().length];
        String[] imageUrl = new String[feedback.getData().length];
        for (int i = 0; i < feedback.getData().length; i++) {
            title[i] = feedback.getData()[i].getName();
            ta_name[i] = feedback.getData()[i].getTa_name();
            imageUrl[i] = feedback.getData()[i].getImage_path();
        }
        adapter = new ShareGridAdapter(SimpleArActivity.this, title, ta_name, imageUrl);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                showArModel(position);
                gridView.setVisibility(View.GONE);
            }
        });
    }

    private void registerEventBus() {
        if (mEventBus == null) {
            mEventBus = EventBus.getDefault();
        }
        mEventBus.register(this);
    }

    public SimpleArActivity() {
    }

    private Handler mTimeHandler;
    private final Runnable mTimeRunner = new Runnable() {
        @Override
        public void run() {
            autoCamera = true;
        }
    };

    private GestureDetector mGestureDetector;
    private SVCGestureListener mGestureListener = new SVCGestureListener();
    private MyOnTouchListener myOnTouchListener;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        TpeArApi.getInstance().getSpecificPoi(this, "poi", mLastLocation,
                new NetworkManager.NetworkManagerListener<IndexFeedback>() {
                    @Override
                    public void onSucceed(IndexFeedback feedback) {
                        if (mIndexPOI == null)
                            mIndexPOI = feedback.getPoi();
                        addPOIMarkers(mIndexPOI);
                    }

                    @Override
                    public void onFail(VolleyError error, boolean shouldRetry) {
                    }
                });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (IndexPoi poi : mIndexPOI) {
                    if (poi.getName().equals(marker.getTitle())) {
                        showPOIInfo(poi.getId());
                        break;
                    }
                }
                marker.showInfoWindow();
                return true;
            }
        });

        LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        addUserMarker(current, mLastLocation);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                TaipeiArSDKText.MAP_MIN_ZOOM_LEVEL));
    }

    private void addPOIMarkers(IndexPoi[] poiData) {
        for (IndexPoi indexPoi : poiData) {
            mMap.addMarker(new MarkerOptions()
                    .flat(false)
                    .title(indexPoi.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.poi_marker))
                    .position(new LatLng(Double.parseDouble(indexPoi.getLat()),
                            Double.parseDouble(indexPoi.getLng())))
                    .zIndex(TaipeiArSDKText.MARKER_Z_INDEX));
        }
    }

    private void showUserPosition() {
        if (!mIsMapInited) {
            mIsMapInited = true;
            mMapFragment.getMapAsync(this);
        }
        LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        addUserMarker(current, mLastLocation);
    }

    private void addUserMarker(LatLng position, Location location) {
        if (mMap == null) {
            return;
        }
        Log.e("LOG", "azimuth" + azimuth);
        if (mUserMarker == null) {
            mUserMarker = mMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .rotation(location.getBearing())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location))
                    .anchor(0.5f, 0.5f)
                    .position(position)
                    .zIndex(TaipeiArSDKText.MARKER_Z_INDEX));

            mUserAccuracyCircle = mMap.addCircle(new CircleOptions()
                    .center(position)
                    .radius(location.getAccuracy() / 2)
                    .strokeColor(ContextCompat.getColor(this, R.color.blue_41))
                    .strokeWidth(5)
                    .zIndex(TaipeiArSDKText.MARKER_Z_INDEX));
        } else {
            mUserMarker.setPosition(position);
            mUserMarker.setRotation(azimuth);
            mUserAccuracyCircle.setCenter(position);
            mUserAccuracyCircle.setRadius(location.getAccuracy() / 2);
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(TaipeiArSDKText.MAP_MIN_ZOOM_LEVEL)
                .bearing(azimuth)
                .build();
        if (autoCamera) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

//        mUserMarker.setVisible(false);
        mUserAccuracyCircle.setVisible(false);
    }

    public interface MyOnTouchListener {
        public boolean onTouch(MotionEvent ev);
    }

    private ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyOnTouchListener listener : onTouchListeners) {
            listener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.add(myOnTouchListener);
    }

    public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.remove(myOnTouchListener);
    }

    public class SVCGestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (autoCamera) {
                autoCamera = false;
//                mTimeHandler.postDelayed(mTimeRunner, 3000);
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerEventBus();
        setContentView(R.layout.activity_simple_ar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(mSensorEventListener, sensor, TaipeiArSDKText.ROTATION_SENSOR_RATE, mHeadingHandler);

        mGestureDetector = new GestureDetector(this, mGestureListener);
        mGestureDetector.setIsLongpressEnabled(true);
        mGestureDetector.setOnDoubleTapListener(mGestureListener);

        myOnTouchListener = new MyOnTouchListener() {
            @Override
            public boolean onTouch(MotionEvent ev) {
                return mGestureDetector.onTouchEvent(ev);
            }
        };
        this.registerMyOnTouchListener(myOnTouchListener);

        if (mTimeHandler == null) {
            mTimeHandler = new Handler();
        }

        // Used to enabled remote debugging of the ArExperience with google chrome https://developers.google.com/web/tools/chrome-devtools/remote-debugging
        WebView.setWebContentsDebuggingEnabled(true);
        /*
         * The following code is used to run different configurations of the SimpleArActivity,
         * it is not required to use the ArchitectView but is used to simplify the Sample App.
         *
         * Because of this the Activity has to be started with correct intent extras.
         * e.g.:
         *  SampleData sampleData = new SampleData.Builder("SAMPLE_NAME", "PATH_TO_AR_EXPERIENCE")
         *              .arFeatures(ArchitectStartupConfiguration.Features.ImageTracking)
         *              .cameraFocusMode(CameraSettings.CameraFocusMode.CONTINUOUS)
         *              .cameraPosition(CameraSettings.CameraPosition.BACK)
         *              .cameraResolution(CameraSettings.CameraResolution.HD_1280x720)
         *              .camera2Enabled(false)
         *              .build();
         *
         * Intent intent = new Intent(this, SimpleArActivity.class);
         * intent.putExtra(UrlLauncherStorageActivity.URL_LAUNCHER_SAMPLE_CATEGORY, category);
         * startActivity(intent);
         */
        final Intent intent = getIntent();
        if (!intent.hasExtra(INTENT_EXTRAS_KEY_SAMPLE)) {
            throw new IllegalStateException(getClass().getSimpleName() +
                    " can not be created without valid SampleData as intent extra for key " + INTENT_EXTRAS_KEY_SAMPLE + ".");
        }

        final SampleData sampleData = (SampleData) intent.getSerializableExtra(INTENT_EXTRAS_KEY_SAMPLE);
        arExperience = sampleData.getPath();

        Log.w("LOG", "Simple AR onCreate: " + arExperience);

        if (intent.hasExtra(INTENT_EXTRAS_KEY_THEME_DATA)) {
            mIndexPOI = (IndexPoi[]) intent.getSerializableExtra(INTENT_EXTRAS_KEY_THEME_DATA);
            Log.e("LOG", "mIndexPOI" + mIndexPOI.length);
        }

        if (intent.hasExtra(INTENT_EXTRAS_KEY_MISSION_DATA)) {
            mIndexPOI = (IndexPoi[]) intent.getSerializableExtra(INTENT_EXTRAS_KEY_MISSION_DATA);
            Log.e("LOG", "mIndexPOI" + mIndexPOI.length);
        }

        if (intent.hasExtra(INTENT_EXTRAS_KEY_NINE_GRID_DATA)) {
            mGridData = (GridData[]) intent.getSerializableExtra(INTENT_EXTRAS_KEY_NINE_GRID_DATA);
            Log.e("LOG", "mGridData" + mGridData.length);
        }

        /*
         * The ArchitectStartupConfiguration is required to call architectView.onCreate.
         * It controls the startup of the ArchitectView which includes camera settings,
         * the required device features to run the ArchitectView and the LicenseKey which
         * has to be set to enable an AR-Experience.
         */
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration(); // Creates a config with its default values.
        config.setLicenseKey(getString(R.string.wikitude_license_key)); // Has to be set, to get a trial license key visit http://www.wikitude.com/developer/licenses.
        config.setCameraPosition(sampleData.getCameraPosition());       // The default camera is the first camera available for the system.
        config.setCameraResolution(sampleData.getCameraResolution());   // The default resolution is 640x480.
        config.setCameraFocusMode(sampleData.getCameraFocusMode());     // The default focus mode is continuous focusing.
        config.setCamera2Enabled(sampleData.isCamera2Enabled());        // The camera2 api is disabled by default (old camera api is used).
        config.setFeatures(sampleData.getArFeatures());                 // This tells the ArchitectView which AR-features it is going to use, the default is all of them.

//        architectView = new ArchitectView(this);
        architectView = findViewById(R.id.activity_simple_ar_atv);
        architectView.onCreate(config); // create ArchitectView with configuration
        architectView.addArchitectJavaScriptInterfaceListener(new ArchitectJavaScriptInterfaceListener() {
            @Override
            public void onJSONObjectReceived(JSONObject jsonObject) {
                try {
                    final String info = jsonObject.getString("action");
                    Log.e("LOG", "info" + info);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                            switch (info) {
                                case "showPatternHint":
                                    showPatternHint();
                                    break;
                                case "hidePatternHint":
                                    hidePatternHint();
                                    break;
                                case "showToolButtons":
                                    showToolButtons();
                                    break;
                                case "hideToolButtons":
                                    hideToolButtons();
                                    break;
                                case "showRescanButton":
                                    showRescanButton();
                                    break;
                                case "hideRescanButton":
                                    hideRescanButton();
                                    break;
//                                case "showLoadingHint":
//                                    showLoadingHint();
//                                    break;
                                case "everyIsReady":
//                                    hideLoadingHint();
                                    showPatternHint();
                                    break;
//                                case "showMissionARRecognized":
//                                    showMissionARRecognized();
//                                    break;
                                case "showPOIInfo":
                                    try {
                                        selectPOIId = jsonObject.getString("id");
                                        showPOIInfo(selectPOIId);
                                        Log.e("LOG", "onJSONObjectReceived: " + selectPOIId);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                        }
                    });
                } catch (JSONException e) {

                }
            }
        });

        title = findViewById(R.id.title);
        back_fl = findViewById(R.id.back_fl);
        interactive_ll = findViewById(R.id.activity_simple_ar_interactive_ll);
        interactive_text = findViewById(R.id.activity_simple_ar_interactive_text);
        interactive_url = findViewById(R.id.activity_simple_ar_interactive_url);
        intro = findViewById(R.id.intro);
        target_hint = findViewById(R.id.target_img);
        mask_top = findViewById(R.id.activity_simple_ar_mask_top);
        mask_bottom = findViewById(R.id.activity_simple_ar_mask_bottom);
        focus_fl = findViewById(R.id.focus_fl);
        focus_hint1 = findViewById(R.id.focus_img1);
        focus_hint2 = findViewById(R.id.focus_img2);
        focus_hint3 = findViewById(R.id.focus_img3);
        focus_hint4 = findViewById(R.id.focus_img4);
        move_3d_model = findViewById(R.id.move_3d_model);
        rotate_3d_model = findViewById(R.id.rotate_3d_model);
        interactive_fl = findViewById(R.id.interactive_fl);
        interactive = findViewById(R.id.interactive_3d_model);
        reset = findViewById(R.id.reset_3d_model);
        rescan = findViewById(R.id.rescan);
        rescanVideo = findViewById(R.id.rescan_video);
        takePhoto = findViewById(R.id.take_photo);
        switch_cam = findViewById(R.id.switch_cam);
        scan_fl = findViewById(R.id.scan_fl);
        introView = findViewById(R.id.intro_view);
        back_tv = findViewById(R.id.back_tv);
        back_tv.setOnClickListener(mOnClickListener);
        ar_img_ll = findViewById(R.id.activity_simple_ar_img_ll);
        gridView = findViewById(R.id.activity_simple_ar_pattern_gv);
        ar_img_btn_tv = findViewById(R.id.activity_simple_ar_img_btn);
        ar_img_support_btn_tv = findViewById(R.id.activity_simple_ar_img_support_btn);
        position = findViewById(R.id.activity_simple_position);

        back_fl.setOnClickListener(mOnClickListener);
        intro.setOnClickListener(mOnClickListener);

        if (intent.hasExtra(INTENT_EXTRAS_KEY_THEME_TITLE)) {
            title.setText((CharSequence) intent.getSerializableExtra(INTENT_EXTRAS_KEY_THEME_TITLE));
        }
        if (intent.hasExtra(INTENT_EXTRAS_KEY_MISSION_TITLE)) {
            title.setText((CharSequence) intent.getSerializableExtra(INTENT_EXTRAS_KEY_MISSION_TITLE));
        }

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_simple_ar_map);
        if (!arExperience.contains("10_BrowsingPois_2_AddingRadar")) {
            mMapFragment.getView().setVisibility(View.GONE);
            position.setVisibility(View.GONE);
            intro.setVisibility(View.GONE);
            if (isMission.equals("true"))
                title.setText(getString(R.string.mission));
            else
                title.setText(getString(R.string.ar_scan));
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCamera = true;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                        TaipeiArSDKText.MAP_MIN_ZOOM_LEVEL));
            }
        });

        interactive_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SimpleArActivity.this, WebViewActivity.class);
                intent.putExtra(TaipeiArSDKText.KEY_WEB_LINK, TestExtension.interactive_url);
                intent.putExtra(TaipeiArSDKText.KEY_WEB_TITLE, TestExtension.interactive_text);
                startActivity(intent);
            }
        });

        ar_img_btn_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkManager.getInstance().setNetworkImage(SimpleArActivity.this, target_hint,
                        TaipeiArSDKActivity.ar_info_Img);
                ar_img_support_btn_tv.setBackgroundResource(R.drawable.round_cornor_white_without_stroke);
                ar_img_support_btn_tv.setTextColor(getResources().getColor(R.color.prime_green));
                ar_img_btn_tv.setBackgroundResource(R.drawable.round_cornor_primary_green);
                ar_img_btn_tv.setTextColor(getResources().getColor(android.R.color.white));
            }
        });

        ar_img_support_btn_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkManager.getInstance().setNetworkImage(SimpleArActivity.this, target_hint,
                        TaipeiArSDKActivity.ar_info_support_Img);
                ar_img_btn_tv.setBackgroundResource(R.drawable.round_cornor_white_without_stroke);
                ar_img_btn_tv.setTextColor(getResources().getColor(R.color.prime_green));
                ar_img_support_btn_tv.setBackgroundResource(R.drawable.round_cornor_primary_green);
                ar_img_support_btn_tv.setTextColor(getResources().getColor(android.R.color.white));
            }
        });

        if (TaipeiArSDKActivity.active_method != null &&
                TaipeiArSDKActivity.active_method.equals("1")) {
//            showArModel(TaipeiArSDKActivity.arContent);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();

        try {
            /*
             * Loads the AR-Experience, it may be a relative path from assets,
             * an absolute path (file://) or a server url.
             *
             * To get notified once the AR-Experience is fully loaded,
             * an ArchitectWorldLoadedListener can be registered.
             */
            architectView.load(SAMPLES_ROOT + arExperience);

        } catch (IOException e) {
            Toast.makeText(this, "Could not load AR experience.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception while loading arExperience " + arExperience + ".", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        architectView.onResume(); // Mandatory ArchitectView lifecycle call
    }

    @Override
    protected void onPause() {
        super.onPause();
        architectView.onPause(); // Mandatory ArchitectView lifecycle call
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
         * Deletes all cached files of this instance of the ArchitectView.
         * This guarantees that internal storage for this instance of the ArchitectView
         * is cleaned and app-memory does not grow each session.
         *
         * This should be called before architectView.onDestroy
         */
        architectView.clearCache();
        architectView.onDestroy(); // Mandatory ArchitectView lifecycle call

        if (mEventBus != null) {
            mEventBus.unregister(this);
        }

        if (mTimeHandler != null && mTimeRunner != null) {
            mTimeHandler.removeCallbacks(mTimeRunner);
        }

        mSensorManager.unregisterListener(mSensorEventListener);
    }

    public void showToolButtons() {
        Log.e("LOG", "showToolButtons");
        move_3d_model.setOnCheckedChangeListener(mOnCheckedChangeListener);
        rotate_3d_model.setOnCheckedChangeListener(mOnCheckedChangeListener);
        interactive.setOnClickListener(mOnClickListener);
//        reset.setOnClickListener(mOnClickListener);
        rescan.setOnClickListener(mOnClickListener);
        takePhoto.setOnClickListener(mOnClickListener);
        switch_cam.setOnClickListener(mOnClickListener);
//        move_3d_model.setAlpha(1f);
//        rotate_3d_model.setAlpha(1f);
        if (TestExtension.interactive_url.length() != 0)
            interactive.setAlpha(1f);
//        reset.setAlpha(1f);
//        rescan.setAlpha(1f);
//        takePhoto.setAlpha(1f);
//        switch_cam.setAlpha(1f);

        interactive_fl.setVisibility(View.VISIBLE);
        scan_fl.setVisibility(View.VISIBLE);
    }

    public void hideToolButtons() {
        move_3d_model.setOnCheckedChangeListener(null);
        rotate_3d_model.setOnCheckedChangeListener(null);
//        reset.setOnClickListener(null);
        rescan.setOnClickListener(null);
        takePhoto.setOnClickListener(null);
        switch_cam.setOnClickListener(null);
//        move_3d_model.setAlpha(0f);
//        rotate_3d_model.setAlpha(0f);
        interactive.setAlpha(0f);
//        reset.setAlpha(0f);
//        rescan.setAlpha(0f);
//        takePhoto.setAlpha(0f);
//        switch_cam.setAlpha(0f);

        interactive_fl.setVisibility(View.GONE);
        scan_fl.setVisibility(View.GONE);
    }

    public void showRescanButton() {
        rescanVideo.setOnClickListener(mOnClickListener);
//        rescanVideo.setAlpha(1f);

        takePhoto.setOnClickListener(mOnClickListener);
        switch_cam.setOnClickListener(mOnClickListener);
//        takePhoto.setAlpha(1f);
//        switch_cam.setAlpha(1f);

        scan_fl.setVisibility(View.VISIBLE);
        rescanVideo.setVisibility(View.VISIBLE);
        rescan.setVisibility(View.GONE);
    }

    public void hideRescanButton() {
//        rescanVideo.setAlpha(0f);

        takePhoto.setOnClickListener(null);
        switch_cam.setOnClickListener(null);
//        takePhoto.setAlpha(0f);
//        switch_cam.setAlpha(0f);

        scan_fl.setVisibility(View.GONE);
        rescanVideo.setVisibility(View.GONE);
        rescan.setVisibility(View.VISIBLE);
    }

    public void showPatternHint() {
        target_hint.setAlpha(0.65f);
        focus_hint1.setAlpha(1f);
        focus_hint2.setAlpha(1f);
        focus_hint3.setAlpha(1f);
        focus_hint4.setAlpha(1f);
        mask_top.setVisibility(View.VISIBLE);
        mask_bottom.setVisibility(View.VISIBLE);
    }

    public void hidePatternHint() {
        target_hint.setAlpha(0f);
        focus_hint1.setAlpha(0f);
        focus_hint2.setAlpha(0f);
        focus_hint3.setAlpha(0f);
        focus_hint4.setAlpha(0f);
        mask_top.setVisibility(View.GONE);
        mask_bottom.setVisibility(View.GONE);
    }

    public void showHintMessage() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_message, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
        final AlertDialog messageDialog = builder.create();
        messageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view.findViewById(R.id.dialog_message_pos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageDialog.dismiss();
            }
        });
        ((TextView) view.findViewById(R.id.dialog_message_title)).setText(R.string.ar_hint);
        messageDialog.show();
    }

    public void showCompleteMessage() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_mission_complete, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
        final AlertDialog completeDialog = builder.create();
        completeDialog.setCancelable(false);
        completeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view.findViewById(R.id.dialog_mission_complete_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeDialog.dismiss();
//                finish();
            }
        });
        completeDialog.show();
    }

    public void showPOIInfo(String id) {
        for (IndexPoi poi : mIndexPOI) {
            if (poi.getId().equals(id)) {
                indexPoi = poi;
                break;
            }
        }
        if (isMission.equals("true")) {
            currentGridPass = false;
            for (GridData data : mGridData) {
                if (data.getPoi().getId().equals(id)) {
                    ng_id = data.getId();
                    break;
                }
            }
        }

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_poi_info, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        String url = indexPoi.getImage();
        NetworkManager.getInstance().setNetworkImage(this,
                ((NetworkImageView) view.findViewById(R.id.dialog_poi_info_img)),
                url);
        ((TextView) view.findViewById(R.id.dialog_poi_info_title)).setText(indexPoi.getName());
        ((TextView) view.findViewById(R.id.dialog_poi_info_desc)).setText(indexPoi.getDesc());
        if (indexPoi.getHyperlink_url().length() == 0) {
            view.findViewById(R.id.dialog_poi_info_pano).setVisibility(View.GONE);
            view.findViewById(R.id.dialog_poi_info_divider).setVisibility(View.GONE);
        } else {
            ((TextView) view.findViewById(R.id.dialog_poi_info_pano)).setText(indexPoi.getHyperlink_text());
        }
        if (indexPoi.getAr_trigger().getActive_method().length() == 0 ||
                indexPoi.getAr_trigger().getActive_method().equals("0")) {
            view.findViewById(R.id.dialog_poi_info_ar).setVisibility(View.GONE);
            view.findViewById(R.id.dialog_poi_info_divider).setVisibility(View.GONE);
        }
        if (indexPoi.getHyperlink_url().length() == 0 &&
                (indexPoi.getAr_trigger().getActive_method().length() == 0 ||
                        indexPoi.getAr_trigger().getActive_method().equals("0"))) {
            view.findViewById(R.id.dialog_poi_info_btn_ll).setVisibility(View.GONE);
        }

        view.findViewById(R.id.dialog_poi_info_close).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.dialog_poi_info_pano).setOnClickListener(view1 -> {
            dialog.dismiss();
            Intent intent = new Intent(SimpleArActivity.this, WebViewActivity.class);
            intent.putExtra(TaipeiArSDKText.KEY_WEB_LINK, indexPoi.getHyperlink_url());
            intent.putExtra(TaipeiArSDKText.KEY_WEB_TITLE, indexPoi.getName());
            startActivity(intent);
        });
        view.findViewById(R.id.dialog_poi_info_ar).setOnClickListener(view1 -> {
            dialog.dismiss();
            TaipeiArSDKActivity.active_method = indexPoi.getAr_trigger().getActive_method();
            switch (indexPoi.getAr_trigger().getActive_method()) {
                case "0":
                    break;
                case "1":
                    double distance = getDistance(
                            mLastLocation.getLatitude(),
                            mLastLocation.getLongitude(),
                            Double.parseDouble(indexPoi.getLat()),
                            Double.parseDouble(indexPoi.getLng()));
                    double deltaX = (Double.parseDouble(indexPoi.getLat()) - mLastLocation.getLatitude());
                    double deltaY = (Double.parseDouble(indexPoi.getLng()) - mLastLocation.getLongitude());
                    double lat = mLastLocation.getLatitude() + deltaX / distance * 20;
                    double lng = mLastLocation.getLongitude() + deltaY / distance * 20;
                    double degree = findDegree((float) deltaX, (float) deltaY);

                    Log.e("LOG", "distance" + distance);
                    Log.e("LOG", "createModelAtLocation lat" + lat);
                    Log.e("LOG", "createModelAtLocation lng" + lng);
                    Log.e("LOG", "isMission" + isMission);
                    if (isMission.equals("false")) {
                        if (indexPoi.getAr().getContent_type().equals("3d_model")) {
                            architectView.callJavascript("World.createModelAtLocation(" +
                                    lat + "," + lng +
                                    "," + 0.45 + "," + 0 + "," + degree + ",'" + indexPoi.getAr().getContent() + "')");
                        } else if (indexPoi.getAr().getContent_type().equals("image")) {
                            architectView.callJavascript("World.createImageAtLocation(" +
                                    lat + "," + lng + ",'" + indexPoi.getAr().getContent() + "')");
                        }
                    } else {    //mission
                        if (distance <= Integer.parseInt(indexPoi.getAr_trigger().getDistance())) {
                            if (indexPoi.getAr().getContent_type().equals("3d_model")) {
                                architectView.callJavascript("World.createModelAtLocation(" +
                                        lat + "," + lng +
                                        "," + 0.45 + "," + 0 + "," + degree + ",'" + indexPoi.getAr().getContent() + "')");
                            } else if (indexPoi.getAr().getContent_type().equals("image")) {
                                architectView.callJavascript("World.createImageAtLocation(" +
                                        lat + "," + lng + ",'" + indexPoi.getAr().getContent() + "')");
                            }
                            showCompleteMessage();
                            TpeArApi.getInstance().getMissionComplete(this,
                                    missionId, ng_id, userId,
                                    new NetworkManager.NetworkManagerListener<MissionCompleteFeedback>() {
                                        @Override
                                        public void onSucceed(MissionCompleteFeedback feedback) {
                                            EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_MISSION_COMPLETE, ""));
                                        }

                                        @Override
                                        public void onFail(VolleyError error, boolean shouldRetry) {

                                        }
                                    });
                        } else {
                            showHintMessage();
                        }
                    }
                    break;
                case "2":
                    TaipeiArSDKActivity.ar_open_by_poi = "true";
                    mEventBus.post(new OmniEvent(OmniEvent.TYPE_OPEN_AR_RECOGNIZE, indexPoi.getAr()));
                    break;
            }
        });
    }

    private void detectArMission() {
        double distance = getDistance(
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude(),
                Double.parseDouble(indexPoi.getLat()),
                Double.parseDouble(indexPoi.getLng()));
        double deltaX = (Double.parseDouble(indexPoi.getLat()) - mLastLocation.getLatitude());
        double deltaY = (Double.parseDouble(indexPoi.getLng()) - mLastLocation.getLongitude());
        double lat = mLastLocation.getLatitude() + deltaX / distance * 20;
        double lng = mLastLocation.getLongitude() + deltaY / distance * 20;
        double degree = findDegree((float) deltaX, (float) deltaY);

        Log.e(TAG, "detectArMission distance" + distance);
        if (distance <= Integer.parseInt(indexPoi.getAr_trigger().getDistance())) {
            if (indexPoi.getAr().getContent_type().equals("3d_model")) {
                architectView.callJavascript("World.createModelAtLocation(" +
                        lat + "," + lng +
                        "," + 0.45 + "," + 0 + "," + degree + ",'" + indexPoi.getAr().getContent() + "')");
            } else if (indexPoi.getAr().getContent_type().equals("image")) {
                architectView.callJavascript("World.createImageAtLocation(" +
                        lat + "," + lng + ",'" + indexPoi.getAr().getContent() + "')");
            }
            showCompleteMessage();
            TpeArApi.getInstance().getMissionComplete(this,
                    missionId, ng_id, userId,
                    new NetworkManager.NetworkManagerListener<MissionCompleteFeedback>() {
                        @Override
                        public void onSucceed(MissionCompleteFeedback feedback) {
                            currentGridPass = true;
                            EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_MISSION_COMPLETE, ""));
                        }

                        @Override
                        public void onFail(VolleyError error, boolean shouldRetry) {

                        }
                    });
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == back_fl.getId() || view.getId() == back_tv.getId()) {
                if (introIsShowing) {
                    intro.setVisibility(View.VISIBLE);
                    introView.setVisibility(View.GONE);
                    title.setText("AR導引");
                    introIsShowing = false;
                } else {
                    finish();
                }
            } else if (view.getId() == intro.getId()) {
                if (!introIsShowing) {
                    intro.setVisibility(View.GONE);
                    introView.setVisibility(View.VISIBLE);
                    title.setText(R.string.ar_intro_title);
                    introIsShowing = true;
                } else {

                }
            }
//            else if (view.getId() == reset.getId()) {
//                architectView.callJavascript("World.resetModel();");
//                architectView.callJavascript("World.startAnimation();");
//            }
            else if (view.getId() == interactive.getId()) {
                if (interactive_ll.getVisibility() == View.VISIBLE) {
                    interactive_ll.setVisibility(View.GONE);
                } else {
                    interactive_ll.setVisibility(View.VISIBLE);
                }
            } else if (view.getId() == rescan.getId()) {
//                if (contentType != null) {
                showPatternHint();
                hideToolButtons();
                architectView.callJavascript("World.switchCamToBack();");
                architectView.callJavascript("World.createOverlays();");

                interactive_ll.setVisibility(View.GONE);
            } else if (view.getId() == rescanVideo.getId()) {
                Log.e("LOG", "rescanVideo");
                showPatternHint();
                hideRescanButton();
                architectView.callJavascript("World.switchCamToBack();");
                architectView.callJavascript("World.createOverlays();");
            } else if (view.getId() == takePhoto.getId()) {
                Log.e(TAG, "takePhoto was clicked");
                architectView.captureScreen(CAPTURE_MODE_CAM_AND_WEBVIEW, new ArchitectView.CaptureScreenCallback() {
                    @Override
                    public void onScreenCaptured(final Bitmap screenCapture) {
                        PermissionManager permissionManager = ArchitectView.getPermissionManager();
                        permissionManager.checkPermissions(SimpleArActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123, new PermissionManager.PermissionManagerCallback() {
                            @Override
                            public void permissionsGranted(int i) {
                                saveScreenCaptureToExternalStorage(screenCapture);
                            }

                            @Override
                            public void permissionsDenied(@NonNull String[] strings) {

                            }

                            @Override
                            public void showPermissionRationale(int i, @NonNull String[] strings) {

                            }
                        });
                    }
                });
            } else if (view.getId() == switch_cam.getId()) {
                architectView.callJavascript("World.switchCam();");
            } else if (view.getId() == mission_ar_recognized.getId()) {
                Intent intent = new Intent();
                intent.putExtra("message_return", "This data is  when user click back menu in target activity.");
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    private void saveScreenCaptureToExternalStorage(Bitmap screenCapture) {
        if (screenCapture != null) {
            // store screenCapture into external cache directory
            final File screenCaptureFile = new File(Environment.getExternalStorageDirectory().toString(), "screenCapture_" + System.currentTimeMillis() + ".jpg");
            // 1. Save bitmap to file & compress to jpeg. You may use PNG too
            try {
                File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "TaipeiAR");
                filePath.mkdirs();
                final FileOutputStream out;

                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    ContentResolver resolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "screenCapture_" + System.currentTimeMillis() + ".jpg");
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "TaipeiAR");
                    Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    out = (FileOutputStream) resolver.openOutputStream(imageUri);

                } else {
                    out = new FileOutputStream(filePath + File.separator + "screenCapture_" + System.currentTimeMillis() + ".jpg");

                }
                screenCapture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                Toast.makeText(getApplicationContext(), getString(R.string.save_picture), Toast.LENGTH_LONG).show();
                // 2. create send intent
//                final Intent share = new Intent(Intent.ACTION_SEND);
//                share.setType("image/jpg");
//                Uri apkURI = FileProvider.getUriForFile(
//                        getApplicationContext(),
//                        getApplicationContext().getPackageName() + ".provider", screenCaptureFile);
//                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                share.putExtra(Intent.EXTRA_STREAM, apkURI);

//                TpeArApi.getInstance().uploadUserImage(this, screenCaptureFile.getAbsolutePath(), new NetworkManager.NetworkManagerListener<UserImageFeedback>() {
//                    @Override
//                    public void onSucceed(UserImageFeedback userImageFeedback) {
////                        Log.e(LOG_TAG, "userImageFeedback" + userImageFeedback.getData());
//                        final Intent share = new Intent(Intent.ACTION_SEND);
//                        share.setType("text/plain");
//                        share.putExtra(Intent.EXTRA_TEXT, userImageFeedback.getData() + "\n" +
//                                getResources().getString(R.string.share_download_hint) + "\n" +
//                                "https://play.google.com/store/apps/details?id=com.taoyuan.vrar&hl=zh-TW");
//                        final String chooserTitle = "Share Snapshot";
//                        startActivity(Intent.createChooser(share, chooserTitle));
//                    }
//
//                    @Override
//                    public void onFail(VolleyError error, boolean shouldRetry) {
////                        Log.e(LOG_TAG, "onFail" + error.toString());
//                    }
//                });


                // 3. launch intent-chooser
//                final String chooserTitle = "Share Snaphot";
//                startActivity(Intent.createChooser(share, chooserTitle));

            } catch (final Exception e) {
                // should not occur when all permissions are set
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // show toast message in case something went wrong
                        Toast.makeText(getApplicationContext(), getString(R.string.unexpected_error) + e, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == reset.getId()) {
//                architectView.callJavascript("World.resetModel();");
            } else if (buttonView.getId() == move_3d_model.getId()) {
                rotate_3d_model.setOnCheckedChangeListener(null);
                rotate_3d_model.setChecked(!isChecked);
                rotate_3d_model.setOnCheckedChangeListener(this);
                architectView.callJavascript("World.modeChange();");
            } else if (buttonView.getId() == rotate_3d_model.getId()) {
                move_3d_model.setOnCheckedChangeListener(null);
                move_3d_model.setChecked(!isChecked);
                move_3d_model.setOnCheckedChangeListener(this);
                architectView.callJavascript("World.modeChange();");
            }
        }
    };

    private int lastDegree = 0;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(
                        mRotationMatrix, event.values);
                float[] orientation = new float[4];
                final float[] orientationAngles = new float[3];
                SensorManager.getOrientation(mRotationMatrix, orientationAngles);
                azimuth = (int) (((((Math.toDegrees(SensorManager.getOrientation(mRotationMatrix, orientation)[0]) + 360) % 360) -
                        (Math.toDegrees(SensorManager.getOrientation(mRotationMatrix, orientation)[2]))) + 360) % 360) + mDeclination;
                azimuth = (azimuth + 360) % 360;
                if (mLastLocation == null)
                    return;
                mLastLocation.setBearing(azimuth);
                if ((int) azimuth != lastDegree) {
                    lastDegree = (int) azimuth;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private class ViewHolder {
        private TextView title;
        private TextView points;
        private GridViewItem image;
    }

    public class ShareGridAdapter extends BaseAdapter {
        private Context context;
        private final String[] title;
        private final String[] ta_name;
        private final String[] imageUrl;

        public ShareGridAdapter(Context context, String[] title, String[] ta_name, String[] imageUrl) {
            this.context = context;
            this.title = title;
            this.ta_name = ta_name;
            this.imageUrl = imageUrl;
        }

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_ar_patterns, null);
                holder = new ViewHolder();
                holder.title = convertView.findViewById(R.id.grid_title);
                holder.points = convertView.findViewById(R.id.grid_points);
                holder.image = convertView.findViewById(R.id.grid_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(title[position]);
            holder.points.setText(ta_name[position]);
            NetworkManager.getInstance().setNetworkImage(SimpleArActivity.this,
                    holder.image, imageUrl[position]);

            return convertView;
        }
    }

    private void showArModel(int position) {
        TestExtension.isRightTarget = "true";
        TestExtension.interactive_text = arPattensFeedback.getData()[position].getInteractive_text();
        TestExtension.interactive_url = arPattensFeedback.getData()[position].getInteractive_url();
        EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_AR_INTERACTIVE_TEXT, ""));

        String contentType = arPattensFeedback.getData()[position].getContent_type();
        String content = arPattensFeedback.getData()[position].getContent();
//        String angle = arPattensFeedback.getData()[position].getAngle();
        String angle = "270";
        String view_size = "";
        if (arPattensFeedback.getData()[position].getSize() != null)
            view_size = arPattensFeedback.getData()[position].getSize();
        switch (view_size) {
            case "S":
                view_size = "0.09";
                break;
            case "B":
                view_size = "0.18";
                break;
            default:
                view_size = "0.135";
                break;
        }
        String isTransparent = arPattensFeedback.getData()[position].getIsTransparent();

        if (contentType != null) {
            if (contentType.equals("video")) {
                Toast.makeText(SimpleArActivity.this, R.string.right_target_video_hint, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SimpleArActivity.this, R.string.right_target_hint, Toast.LENGTH_SHORT).show();
            }

            switch (contentType) {
                case "3d_model":
                    architectView.callJavascript("World.callback3DModel('" + content + "','" + angle + "','" + TestExtension.isRightTarget + "','" + view_size + "');");
                    break;
                case "image":
                    architectView.callJavascript("World.callbackImage('" + content + "','" + TestExtension.isRightTarget + "');");
                    break;
                case "video":
                    architectView.callJavascript("World.callbackVideo('" + content + "','" + TestExtension.isRightTarget + "','" + isTransparent + "');");
                    break;
                case "text":
                    String textImgUrl = arPattensFeedback.getData()[position].getUrl_image();
                    architectView.callJavascript("World.callbackText('" + textImgUrl + "','" + TestExtension.isRightTarget + "');");
                    break;
            }
        }
    }

    private void showArModel(Ar arModel) {
        Log.e("LOG", "showArModel");
        TestExtension.isRightTarget = "true";
        TestExtension.interactive_text = arModel.getInteractive_text();
        TestExtension.interactive_url = arModel.getInteractive_url();
        EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_AR_INTERACTIVE_TEXT, ""));

        String contentType = arModel.getContent_type();
        String content = arModel.getContent();
        String angle = "270";
        String view_size = "";
        if (arModel.getSize() != null)
            view_size = arModel.getSize();
        switch (view_size) {
            case "S":
                view_size = "0.09";
                break;
            case "B":
                view_size = "0.18";
                break;
            default:
                view_size = "0.135";
                break;
        }
        String isTransparent = arModel.getIsTransparent();

        if (contentType != null) {
            switch (contentType) {
                case "3d_model":
                    Log.e("LOG", "showArModel 3d_model");
                    architectView.callJavascript("World.callback3DModel('" + content + "','" + angle + "','" + TestExtension.isRightTarget + "','" + view_size + "');");
                    break;
                case "image":
                    architectView.callJavascript("World.callbackImage('" + content + "','" + TestExtension.isRightTarget + "');");
                    break;
                case "video":
                    architectView.callJavascript("World.callbackVideo('" + content + "','" + TestExtension.isRightTarget + "','" + isTransparent + "');");
                    break;
                case "text":
                    String textImgUrl = arModel.getUrl();
                    architectView.callJavascript("World.callbackText('" + textImgUrl + "','" + TestExtension.isRightTarget + "');");
                    break;
            }
        }
    }

    public float getDistance(double p1Lat, double p1Lon, double p2Lat, double p2Lon) {
        Location l1 = new Location("One");
        l1.setLatitude(p1Lat);
        l1.setLongitude(p1Lon);

        Location l2 = new Location("Two");
        l2.setLatitude(p2Lat);
        l2.setLongitude(p2Lon);

        return l1.distanceTo(l2);
    }

    public static float findDegree(float x, float y) {
        float value = (float) ((Math.atan2(x, y) / Math.PI) * 180f);
        if (value < 0) value += 360f;
        return value - 90f;
    }
}
