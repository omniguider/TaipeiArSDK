package com.omni.taipeiarsdkdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.omni.taipeiarsdk.TaipeiArSDKActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            val intent = Intent(this@MainActivity, TaipeiArSDKActivity::class.java)
            startActivity(intent)
        }
    }
}