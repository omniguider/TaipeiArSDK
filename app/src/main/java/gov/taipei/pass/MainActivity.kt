package gov.taipei.pass

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.omni.taipeiarsdk.TaipeiArSDKActivity

class MainActivity : AppCompatActivity() {

    private val ARG_KEY_USERID = "arg_key_userid"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userIdEt = findViewById<EditText>(R.id.activity_main_user_id)

        findViewById<Button>(R.id.button).setOnClickListener {
            val intent = Intent(this@MainActivity, TaipeiArSDKActivity::class.java)
            intent.putExtra(ARG_KEY_USERID, userIdEt.text.toString())
            startActivity(intent)
        }
    }
}