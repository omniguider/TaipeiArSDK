package com.omni.taipeiarsdk

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.indooratlas.android.sdk.*
import com.omni.taipeiarsdk.manager.AnimationFragmentManager
import com.omni.taipeiarsdk.model.OmniEvent
import com.omni.taipeiarsdk.model.mission.MissionGridData
import com.omni.taipeiarsdk.model.tpe_location.Ar
import com.omni.taipeiarsdk.model.tpe_location.IndexPoi
import com.omni.taipeiarsdk.tool.TaipeiArSDKText
import com.omni.taipeiarsdk.tool.TaipeiArSDKText.LOG_TAG
import com.omni.taipeiarsdk.util.*
import com.omni.taipeiarsdk.view.mission.MissionFragment
import com.omni.taipeiarsdk.view.theme.ThemeGuideFragment
import com.wikitude.architect.ArchitectView
import com.wikitude.common.permission.PermissionManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

class TaipeiArSDKActivity : AppCompatActivity(), IARegion.Listener, IALocationListener,
    LocationListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    companion object {
        lateinit var userId: String
        lateinit var ar_info_support_Img: String
        lateinit var ar_info_Name: String
        lateinit var ar_info_Img: String
        lateinit var ar_info_support_tdar_id: String
        lateinit var arContent: Ar
        lateinit var active_method: String
        lateinit var ar_open_by_poi: String
        lateinit var mIndexPOI: Array<IndexPoi>
        lateinit var filterKeyword: ArrayList<String>
        lateinit var filterKeywordCopy: ArrayList<String>
        lateinit var themeTitle: String
        lateinit var currentNineGridData: MissionGridData
        lateinit var missionTitle: String
        lateinit var isMission: String
        lateinit var missionId: String
        lateinit var ng_id: String
        lateinit var indexPoi_id: String
        lateinit var ng_title: String
    }

    private val ARG_KEY_USERID = "arg_key_userid"
    private val sampleDefinitionsPath = "samples/samples.json"
    private val permissionManager: PermissionManager = ArchitectView.getPermissionManager()
    private var categories: List<SampleCategory>? = null
    var sampleData: SampleData? = null

    private var mLastLocation: Location? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null

    private var mIsIndoor = false
    private var mIALocationManager: IALocationManager? = null

    private var mEventBus: EventBus? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OmniEvent) {
        when (event.type) {
            OmniEvent.TYPE_OPEN_AR_RECOGNIZE -> {
                arContent = event.obj as Ar
                sampleData = categories!![6].samples[0] //ar_recognize
                val intent = Intent(this@TaipeiArSDKActivity, sampleData!!.activityClass)
                intent.putExtra(SimpleArActivity.INTENT_EXTRAS_KEY_SAMPLE, sampleData)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            OmniEvent.TYPE_OPEN_AR_GUIDE -> {
                isMission = "false"
                mIndexPOI = event.obj as Array<IndexPoi>
                sampleData = categories!![9].samples[1] //ar_guide
                val intent = Intent(this@TaipeiArSDKActivity, sampleData!!.activityClass)
                intent.putExtra(SimpleArActivity.INTENT_EXTRAS_KEY_SAMPLE, sampleData)
                intent.putExtra(
                    SimpleArActivity.INTENT_EXTRAS_KEY_THEME_DATA,
                    event.obj as Array<IndexPoi>
                )
                intent.putExtra(
                    SimpleArActivity.INTENT_EXTRAS_KEY_THEME_TITLE,
                    themeTitle
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            OmniEvent.TYPE_OPEN_AR_MISSION -> {
                isMission = "true"
                mIndexPOI = event.obj as Array<IndexPoi>
                sampleData = categories!![9].samples[1] //ar_guide
                val intent = Intent(this@TaipeiArSDKActivity, sampleData!!.activityClass)
                intent.putExtra(SimpleArActivity.INTENT_EXTRAS_KEY_SAMPLE, sampleData)
                intent.putExtra(
                    SimpleArActivity.INTENT_EXTRAS_KEY_MISSION_DATA,
                    event.obj as Array<IndexPoi>
                )
                intent.putExtra(
                    SimpleArActivity.INTENT_EXTRAS_KEY_NINE_GRID_DATA,
                    currentNineGridData.nine_grid
                )
                intent.putExtra(
                    SimpleArActivity.INTENT_EXTRAS_KEY_MISSION_TITLE,
                    missionTitle
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taipei_ar_sdk)

        if (mEventBus == null) {
            mEventBus = EventBus.getDefault()
        }
        mEventBus!!.register(this)

        userId = intent.getStringExtra(ARG_KEY_USERID).toString()
        isMission = "false"
        ar_open_by_poi = "false"
        filterKeyword = ArrayList()
        filterKeywordCopy = ArrayList()

        val json: String = SampleJsonParser.loadStringFromAssets(
            this,
            sampleDefinitionsPath
        )

        categories = SampleJsonParser.getCategoriesFromJsonString(json)
        sampleData = categories!![9].samples[1] //ar_guide

        findViewById<TextView>(R.id.modelAtGeoLocation).setOnClickListener {
            sampleData = categories!![6].samples[5]
            val intent = Intent(this@TaipeiArSDKActivity, sampleData!!.activityClass)
            intent.putExtra(SimpleArActivity.INTENT_EXTRAS_KEY_SAMPLE, sampleData)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.ar_guide).setOnClickListener {
            isMission = "false"
            mIndexPOI = emptyArray()
            sampleData = categories!![9].samples[1] //ar_guide
            val intent = Intent(this@TaipeiArSDKActivity, sampleData!!.activityClass)
            intent.putExtra(SimpleArActivity.INTENT_EXTRAS_KEY_SAMPLE, sampleData)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.ar_recognize).setOnClickListener {
            ar_open_by_poi = "false"
            isMission = "false"
            sampleData = categories!![6].samples[0]
            val intent = Intent(this@TaipeiArSDKActivity, sampleData!!.activityClass)
            intent.putExtra(SimpleArActivity.INTENT_EXTRAS_KEY_SAMPLE, sampleData)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.theme_guide).setOnClickListener {
            openFragmentPage(ThemeGuideFragment.newInstance(), ThemeGuideFragment.TAG)
        }

        findViewById<CardView>(R.id.mission).setOnClickListener {
            openFragmentPage(MissionFragment.newInstance(), MissionFragment.TAG)
        }

        findViewById<FrameLayout>(R.id.back_fl).setOnClickListener {
            finish()
        }

        val permissions: Array<String> =
            PermissionUtil.getPermissionsForArFeatures(sampleData!!.arFeatures)
        permissionManager.checkPermissions(
            this,
            permissions,
            PermissionManager.WIKITUDE_PERMISSION_REQUEST,
            object : PermissionManager.PermissionManagerCallback {
                override fun permissionsGranted(requestCode: Int) {
                    Log.w("LOG", "permissionsGranted: " + sampleData!!.activityClass.toString())
                }

                override fun permissionsDenied(deniedPermissions: Array<String?>) {
                    Toast.makeText(
                        this@TaipeiArSDKActivity,
                        getString(R.string.permissions_denied) + Arrays.toString(deniedPermissions),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun showPermissionRationale(requestCode: Int, strings: Array<String?>) {
                    val alertBuilder = AlertDialog.Builder(this@TaipeiArSDKActivity)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle(R.string.permission_rationale_title)
                    alertBuilder.setMessage(
                        getString(R.string.permission_rationale_text) + Arrays.toString(
                            permissions
                        )
                    )
                    alertBuilder.setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        permissionManager.positiveRationaleResult(
                            requestCode,
                            permissions
                        )
                    }
                    val alert = alertBuilder.create()
                    alert.show()
                }
            })
    }

    override fun onResume() {
        super.onResume()

        checkLocationService()

        if (ar_open_by_poi == "true" && isMission == "false") {
            ar_open_by_poi = "false"

            sampleData = categories!![9].samples[1] //ar_guide
            val intent = Intent(this@TaipeiArSDKActivity, sampleData!!.activityClass)
            intent.putExtra(SimpleArActivity.INTENT_EXTRAS_KEY_SAMPLE, sampleData)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mEventBus != null) {
            mEventBus!!.unregister(this)
        }

        if (mIALocationManager != null) {
            mIALocationManager!!.destroy()
            mIALocationManager = null
        }
    }

    private fun checkLocationService() {
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ensurePermissions()
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage("位置服務尚未開啟，請設定")
            dialog.setPositiveButton(
                "前往設定"
            ) { paramDialogInterface, paramInt -> // TODO Auto-generated method stub
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            }
            dialog.setNegativeButton(
                "取消"
            ) { paramDialogInterface, paramInt ->
                // TODO Auto-generated method stub
            }
            dialog.show()
        }
    }

    private fun ensurePermissions() {
        Log.e("LOG", "ensurePermissions")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_WIFI_STATE
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CHANGE_WIFI_STATE
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN
                ),
                TaipeiArSDKText.REQUEST_PERMISSIONS
            )
        } else {
            if (mGoogleApiClient == null) {
                Log.e("LOG", "mGoogleApiClient init")
                mGoogleApiClient = GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(Places.GEO_DATA_API)
                    .build()
            }
            getLocationFromGoogle()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    TaipeiArSDKText.REQUEST_PERMISSIONS
                )
            }
        }
    }

    private fun getLocationFromGoogle() {
        if (mGoogleApiClient != null && !mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.connect()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                initIALocationManager()
            }
        } else {
            initIALocationManager()
        }
    }

    private fun initIALocationManager() {
        if (mGoogleApiClient != null && !mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.connect()
        }

        if (mIALocationManager == null) {
            mIALocationManager = IALocationManager.create(this)
            mIALocationManager?.lockIndoors(true)
        } else {
            mIALocationManager!!.unregisterRegionListener(this)
        }
        mIALocationManager!!.registerRegionListener(this)

        val request = IALocationRequest.create()
        request.fastestInterval = 1000
        request.smallestDisplacement = 0.6f
        mIALocationManager!!.removeLocationUpdates(this)
        mIALocationManager!!.requestLocationUpdates(request, this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionManager.WIKITUDE_PERMISSION_REQUEST &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
        }
    }

    private fun openFragmentPage(fragment: Fragment, tag: String) {
        AnimationFragmentManager.getInstance().addFragmentPage(
            this@TaipeiArSDKActivity,
            R.id.activity_main_fl, fragment, tag
        )
    }

    override fun onLocationChanged(location: Location) {
        Log.e(LOG_TAG, "mIsIndoor$mIsIndoor")
        if (!mIsIndoor) {
            mLastLocation = location
            EventBus.getDefault().post(OmniEvent(OmniEvent.TYPE_USER_AR_LOCATION, location))
        }
    }

    override fun onConnected(bundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                TaipeiArSDKText.REQUEST_FINE_LOCATION
            )
        } else {
            if (mLocationRequest == null) {
                mLocationRequest = LocationRequest()
                mLocationRequest!!.setInterval(1000)
                mLocationRequest!!.setFastestInterval(1000)
                mLocationRequest!!.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient!!,
                mLocationRequest!!,
                this
            )
        }
    }

    override fun onConnectionSuspended(i: Int) {
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(ConnectionResult: ConnectionResult) {
    }

    override fun onLocationChanged(iaLocation: IALocation?) {
        Log.e(LOG_TAG, "mIsIndoor$mIsIndoor")
        if (mIsIndoor) {
            if (iaLocation != null && iaLocation.floorCertainty > 0.8) {
                mLastLocation = iaLocation.toLocation()
                EventBus.getDefault()
                    .post(OmniEvent(OmniEvent.TYPE_USER_AR_LOCATION, mLastLocation))
            }
        }
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle?) {
    }

    override fun onEnterRegion(iaRegion: IARegion?) {
        Log.e(LOG_TAG, "onEnterRegion ")
        when {
            iaRegion!!.type == IARegion.TYPE_UNKNOWN -> {
                mIsIndoor = false
            }
            iaRegion.type == IARegion.TYPE_VENUE -> {
                mIsIndoor = false
            }
            iaRegion.type == IARegion.TYPE_FLOOR_PLAN -> {
                Log.e(LOG_TAG, "onEnterRegion floor plan : " + iaRegion.id)
                mIsIndoor = true
                EventBus.getDefault()
                    .post(OmniEvent(OmniEvent.TYPE_FLOOR_PLAN_CHANGED, iaRegion.id))
            }
        }
    }

    override fun onExitRegion(iaRegion: IARegion?) {
        when {
            iaRegion!!.type == IARegion.TYPE_UNKNOWN -> {
                mIsIndoor = false
            }
            iaRegion.type == IARegion.TYPE_VENUE -> {
                mIsIndoor = false
            }
            iaRegion.type == IARegion.TYPE_FLOOR_PLAN -> {
                mIsIndoor = false
            }
        }
    }
}