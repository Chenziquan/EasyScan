package com.pax.publicproject

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import com.pax.jc.easyscan.QRScannerActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_SCAN_CODE = 0
    }

    private var result: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        result = findViewById(R.id.main_scan_result_tv)
        findViewById<Button>(R.id.main_scan_btn)
            .setOnClickListener {
                goScannerActivity()
            }
    }

    private fun goScannerActivity() {
        startActivityForResult(
            Intent(this@MainActivity, QRScannerActivity::class.java),
            REQUEST_SCAN_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCAN_CODE) {
            if (Activity.RESULT_OK == resultCode) {
                if (data != null) {
                    val code =
                        data.extras?.get(QRScannerActivity.EXTRA_SCAN_RESULT).toString()
                    if (!TextUtils.isEmpty(code)) {
                        result?.text = code
                    }
                }
            }
        }
    }
}