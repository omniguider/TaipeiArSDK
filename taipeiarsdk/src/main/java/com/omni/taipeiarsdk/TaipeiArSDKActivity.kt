package com.omni.taipeiarsdk

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.omni.taipeiarsdk.util.PermissionUtil
import com.omni.taipeiarsdk.util.SampleCategory
import com.omni.taipeiarsdk.util.SampleData
import com.omni.taipeiarsdk.util.SampleJsonParser
import com.wikitude.architect.ArchitectView
import com.wikitude.common.permission.PermissionManager
import java.util.*

class TaipeiArSDKActivity : AppCompatActivity() {

    companion object {
        lateinit var userId: String
        lateinit var ar_info_support_Img: String
        lateinit var ar_info_Name: String
        lateinit var ar_info_Img: String
        lateinit var ar_info_support_tdar_id: String
    }

    private val sampleDefinitionsPath = "samples/samples.json"
    private val permissionManager: PermissionManager = ArchitectView.getPermissionManager()
    private var categories: List<SampleCategory>? = null
    var isStartAR: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taipei_ar_sdk)

        val json: String = SampleJsonParser.loadStringFromAssets(
            this,
            sampleDefinitionsPath
        )

        categories = SampleJsonParser.getCategoriesFromJsonString(json)
        val sampleData: SampleData = categories!![6].samples[0]
        val permissions: Array<String> =
            PermissionUtil.getPermissionsForArFeatures(sampleData.arFeatures)

        permissionManager.checkPermissions(
            this,
            permissions,
            PermissionManager.WIKITUDE_PERMISSION_REQUEST,
            object : PermissionManager.PermissionManagerCallback {
                override fun permissionsGranted(requestCode: Int) {
                    Log.w("LOG", "permissionsGranted: " + sampleData.activityClass.toString())
                    isStartAR = true
                    val intent = Intent(this@TaipeiArSDKActivity, sampleData.activityClass)
                    intent.putExtra(SimpleArActivity.INTENT_EXTRAS_KEY_SAMPLE, sampleData)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
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
        if (isStartAR)
            finish()
    }
}