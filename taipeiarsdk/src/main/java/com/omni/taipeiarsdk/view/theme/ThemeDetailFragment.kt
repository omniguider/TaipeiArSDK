package com.omni.taipeiarsdk.view.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.hardware.*
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.omni.taipeiarsdk.R
import com.omni.taipeiarsdk.TaipeiArSDKActivity
import com.omni.taipeiarsdk.adapter.ThemeDetailAdapter
import com.omni.taipeiarsdk.model.OmniEvent
import com.omni.taipeiarsdk.model.mission.GridData
import com.omni.taipeiarsdk.model.mission.MissionGridFeedback
import com.omni.taipeiarsdk.model.tpe_location.IndexPoi
import com.omni.taipeiarsdk.model.tpe_location.ThemeData
import com.omni.taipeiarsdk.network.NetworkManager
import com.omni.taipeiarsdk.network.TpeArApi
import com.omni.taipeiarsdk.tool.TaipeiArSDKText
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ThemeDetailFragment : Fragment(), OnMapReadyCallback {
    private var mContext: Context? = null
    private var themeDetailRV: RecyclerView? = null
    private var mAdapter: ThemeDetailAdapter? = null
    private var mEventBus: EventBus? = null

    private var mMapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var mIsMapInit = false
    private var mLastLocation: Location? = null
    private var mUserMarker: Marker? = null
    private var azimuth = 0f
    private val mRotationMatrix = FloatArray(16)
    private var mDeclination = 0f
    private var mUserAccuracyCircle: Circle? = null
    private var viewMode = "list"
    private var viewModeIV: ImageView? = null
    private var viewModeTV: TextView? = null
    private var viewModeLL: LinearLayout? = null
    private var mapFL: FrameLayout? = null
    private var mSensorManager: SensorManager? = null
    private val mHeadingHandler: Handler? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OmniEvent) {
        when (event.type) {
            OmniEvent.TYPE_USER_AR_LOCATION -> {
                mLastLocation = event.obj as Location

                val field = GeomagneticField(
                    mLastLocation!!.latitude.toFloat(),
                    mLastLocation!!.longitude.toFloat(),
                    mLastLocation!!.altitude.toFloat(),
                    System.currentTimeMillis()
                )
                mDeclination = field.declination
                showUserPosition()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mEventBus == null) {
            mEventBus = EventBus.getDefault()
        }
        mEventBus!!.register(this)

        mSensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        mSensorManager!!.registerListener(
            mSensorEventListener,
            sensor,
            TaipeiArSDKText.ROTATION_SENSOR_RATE,
            mHeadingHandler
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mEventBus != null) {
            mEventBus!!.unregister(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_theme_detail, container, false)
        view.findViewById<View>(R.id.back_fl)
            .setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        themeDetailRV = view.findViewById(R.id.theme_detail_recycler_view)
        themeDetailRV!!.layoutManager = LinearLayoutManager(mContext)
        if (mAdapter == null) {
            mAdapter = ThemeDetailAdapter(
                mContext,
                mThemeData.poi.toMutableList()
            )
        }
        themeDetailRV!!.adapter = mAdapter

        view.findViewById<TextView>(R.id.theme_detail_navi).setOnClickListener {
            EventBus.getDefault().post(OmniEvent(OmniEvent.TYPE_OPEN_AR_GUIDE, mThemeData.poi));
        }

        NetworkManager.getInstance().setNetworkImage(
            mContext,
            view.findViewById(R.id.theme_detail_img), mThemeData.image
        )

        view.findViewById<TextView>(R.id.theme_title).text = mThemeData.name
        view.findViewById<TextView>(R.id.theme_number).text =
            mThemeData.poi.size.toString() + " 個地點"

        mMapFragment =
            childFragmentManager.findFragmentById(R.id.theme_detail_map) as SupportMapFragment?

        mapFL = view.findViewById(R.id.theme_detail_map_fl)
        viewModeIV = view.findViewById(R.id.view_mode_iv)
        viewModeTV = view.findViewById(R.id.view_mode_tv)
        viewModeLL = view.findViewById(R.id.view_mode)
        viewModeLL!!.setOnClickListener {
            if (viewMode == "list") {
                themeDetailRV!!.visibility = View.GONE
                mapFL!!.visibility = View.VISIBLE
                viewMode = "map"
                viewModeIV!!.setImageResource(R.mipmap.icon_view_list)
                viewModeTV!!.setText(R.string.view_list)
            } else {
                themeDetailRV!!.visibility = View.VISIBLE
                mapFL!!.visibility = View.GONE
                viewMode = "list"
                viewModeIV!!.setImageResource(R.mipmap.icon_view_location)
                viewModeTV!!.setText(R.string.view_location)
            }
        }

        return view
    }

    companion object {
        const val TAG = "fragment_tag_theme_detail"
        lateinit var mThemeData: ThemeData
        fun newInstance(themeData: ThemeData): ThemeDetailFragment {
            mThemeData = themeData
            return ThemeDetailFragment()
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap!!.uiSettings.isZoomControlsEnabled = false
        mMap!!.uiSettings.isMapToolbarEnabled = false
        mMap!!.uiSettings.isCompassEnabled = true
        mMap!!.uiSettings.isZoomGesturesEnabled = true

        addPOIMarkers(mThemeData.poi)
        if (mThemeData.poi.isNotEmpty()) {
            mMap!!.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        mThemeData.poi[0].lat.toDouble(),
                        mThemeData.poi[0].lng.toDouble()
                    ),
                    TaipeiArSDKText.MAP_MIN_ZOOM_LEVEL.toFloat()
                )
            )
        }
    }

    private fun addPOIMarkers(indexPois: Array<IndexPoi>) {
        for (i in indexPois.indices) {
            val icon =
                BitmapFactory.decodeResource(requireActivity().resources, R.mipmap.bg_poi)
                    .copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(icon)
            val paint = Paint()
            paint.style = Paint.Style.FILL
            paint.color = requireActivity().resources.getColor(android.R.color.white) // Text Color
            paint.textSize =
                requireActivity().resources.getDimension(R.dimen.text_size_normal) //Text Size
            val x = (canvas.width / 2 - paint.measureText((i + 1).toString()) / 2).toInt()
            val y = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
            canvas.drawText((i + 1).toString(), x.toFloat(), (y - 6).toFloat(), paint)

            mMap!!.addMarker(
                MarkerOptions()
                    .flat(false)
                    .title(indexPois[i].name)
                    .icon(BitmapDescriptorFactory.fromBitmap(icon))
                    .position(
                        LatLng(
                            indexPois[i].lat.toDouble(),
                            indexPois[i].lng.toDouble()
                        )
                    )
                    .zIndex(TaipeiArSDKText.MARKER_Z_INDEX.toFloat())
            )
        }
    }

    private fun showUserPosition() {
        if (!mIsMapInit) {
            mIsMapInit = true
            mMapFragment!!.getMapAsync(this)
        }
        val current = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
        addUserMarker(current, mLastLocation!!)
    }

    private fun addUserMarker(position: LatLng, location: Location) {
        if (mMap == null) {
            return
        }

        if (mUserMarker == null) {
            mUserMarker = mMap!!.addMarker(
                MarkerOptions()
                    .flat(true)
                    .rotation(location.bearing)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location))
                    .anchor(0.5f, 0.5f)
                    .position(position)
                    .zIndex(TaipeiArSDKText.MARKER_Z_INDEX.toFloat())
            )
            mUserAccuracyCircle = mMap!!.addCircle(
                CircleOptions()
                    .center(position)
                    .radius((location.accuracy / 2).toDouble())
                    .strokeColor(ContextCompat.getColor(requireActivity(), R.color.blue_41))
                    .strokeWidth(5f)
                    .zIndex(TaipeiArSDKText.MARKER_Z_INDEX.toFloat())
            )
        } else {
            mUserMarker!!.position = position
            mUserMarker!!.rotation = azimuth
            mUserAccuracyCircle!!.center = position
            mUserAccuracyCircle!!.radius = (location.accuracy / 2).toDouble()
        }

        mUserAccuracyCircle!!.isVisible = false
    }

    private val mSensorEventListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values
                )
                val orientation = FloatArray(4)
                val orientationAngles = FloatArray(3)
                SensorManager.getOrientation(mRotationMatrix, orientationAngles)
                azimuth = (((Math.toDegrees(
                    SensorManager.getOrientation(mRotationMatrix, orientation)[0]
                        .toDouble()
                ) + 360) % 360 -
                        Math.toDegrees(
                            SensorManager.getOrientation(mRotationMatrix, orientation)[2]
                                .toDouble()
                        ) + 360) % 360).toInt() + mDeclination
                azimuth = (azimuth + 360) % 360
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
}