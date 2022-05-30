package com.omni.taipeiarsdk

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
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
import com.omni.taipeiarsdk.manager.AnimationFragmentManager
import com.omni.taipeiarsdk.model.OmniEvent
import com.omni.taipeiarsdk.model.mission.MissionGridData
import com.omni.taipeiarsdk.model.tpe_location.Ar
import com.omni.taipeiarsdk.model.tpe_location.IndexPoi
import com.omni.taipeiarsdk.tool.TaipeiArSDKText
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

class TaipeiArSDKActivity : AppCompatActivity(), LocationListener,
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
    }

    private val sampleDefinitionsPath = "samples/samples.json"
    private val permissionManager: PermissionManager = ArchitectView.getPermissionManager()
    private var categories: List<SampleCategory>? = null
    var sampleData: SampleData? = null

    private var mLastLocation: Location? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null

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

        isMission = "false"
        userId = "Hf1242aaa6"
        ar_open_by_poi = "false"
        filterKeyword = ArrayList()
        filterKeywordCopy = ArrayList()

        val json: String = SampleJsonParser.loadStringFromAssets(
            this,
            sampleDefinitionsPath
        )

        categories = SampleJsonParser.getCategoriesFromJsonString(json)
        sampleData = categories!![9].samples[1] //ar_guide

        findViewById<CardView>(R.id.ar_guide).setOnClickListener {
            mIndexPOI = emptyArray()
            sampleData = categories!![9].samples[1] //ar_guide
            val intent = Intent(this@TaipeiArSDKActivity, sampleData!!.activityClass)
            intent.putExtra(SimpleArActivity.INTENT_EXTRAS_KEY_SAMPLE, sampleData)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.ar_recognize).setOnClickListener {
            ar_open_by_poi = "false"
            sampleData = categories!![6].samples[0] //ar_guide
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
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                TaipeiArSDKText.REQUEST_COARSE_LOCATION
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
    }

    private fun getLocationFromGoogle() {
        if (mGoogleApiClient != null && !mGoogleApiClient!!.isConnected()) {
            mGoogleApiClient!!.connect()
        }
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

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        EventBus.getDefault().post(OmniEvent(OmniEvent.TYPE_USER_AR_LOCATION, location))
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
                mLocationRequest = com.google.android.gms.location.LocationRequest()
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
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(ConnectionResult: ConnectionResult) {
        TODO("Not yet implemented")
    }

    private fun openFragmentPage(fragment: Fragment, tag: String) {
        AnimationFragmentManager.getInstance().addFragmentPage(
            this@TaipeiArSDKActivity,
            R.id.activity_main_fl, fragment, tag
        )
    }
}