package com.jc.easyscan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.Executors

class QRScannerActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_SCAN_RESULT = "app.jc.easy.scan.SCAN_RESULT"
        private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        private const val autoCenterFocusDuration = 2000L
        private const val PERMISSIONS_REQUEST_CAMERA = 0
    }

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    private var focusTimerActive = false

    private lateinit var overlayView: QROverlay
    private lateinit var camera: Camera
    lateinit var contentFrame: PreviewView
    private var snackBar: Snackbar? = null

    private val runnable = Runnable {
        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
            contentFrame.width.toFloat(), contentFrame.height.toFloat()
        )

        val autoFocusPoint = factory.createPoint(
            contentFrame.width / 2.0f,
            contentFrame.height / 2.0f, overlayView.size.toFloat()
        )

        camera.cameraControl.startFocusAndMetering(
            FocusMeteringAction.Builder(autoFocusPoint).disableAutoCancel().build()
        )

        startFocusTimer()
    }

    private fun startFocusTimer() {
        focusTimerActive = handler.postDelayed(runnable, autoCenterFocusDuration)
    }

    private fun cancelFocusTimer() {
        handler.removeCallbacks(runnable)
        focusTimerActive = false
    }

    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_qrscanner)

        contentFrame = findViewById(R.id.scan_content_frame)
        contentFrame.scaleType = PreviewView.ScaleType.FIT_CENTER

        overlayView = findViewById(R.id.scan_overlay)
        snackBar = Snackbar.make(findViewById(R.id.scan_frame_layout), "", Snackbar.LENGTH_LONG)
        overlayView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                overlayView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                checkCameraPermission()
            }
        })

        val cameraController = LifecycleCameraController(this)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = cameraSelector
        cameraController.setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
    }

    private fun checkCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                PERMISSIONS_REQUEST_CAMERA
            )
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                snackBar?.dismiss()
                startCamera()
            } else {
                snackBar?.setText(R.string.camera_permission_denied)?.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::camera.isInitialized && !focusTimerActive) {
            startFocusTimer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (focusTimerActive) {
            cancelFocusTimer()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    fun getOverlayView(): QROverlay {
        return overlayView
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(contentFrame.surfaceProvider)
                    }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(960, 960))
                    .build()

                imageAnalysis.setAnalyzer(
                    executor,
                    QRCodeImageAnalyzer(this) { response ->
                        if (response != null) {
                            handleResult(response)
                        }
                    }
                )

                cameraProvider.unbindAll()
                camera =
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
                startFocusTimer()
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun handleResult(rawResult: String) {
        val result = Intent()
        result.putExtra(EXTRA_SCAN_RESULT, rawResult)
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}
