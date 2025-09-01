package com.example.wallpaintestimator

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnCapture: Button
    private lateinit var btnEstimateArea: Button
    private lateinit var btnReset: Button
    private lateinit var btnManual: Button // NEW
    private lateinit var imageView: ImageView
    private lateinit var selectionView: SelectionView
    private lateinit var tvEstimate: TextView

    private val CAMERA_REQUEST_CODE = 100
    private val MANUAL_REQUEST_CODE = 101 // NEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCapture = findViewById(R.id.btnCapture)
        btnEstimateArea = findViewById(R.id.btnEstimateArea)
        btnReset = findViewById(R.id.btnReset)
        btnManual = findViewById(R.id.btnManual) // NEW
        imageView = findViewById(R.id.imageView)
        selectionView = findViewById(R.id.selectionView)
        tvEstimate = findViewById(R.id.tvEstimate)

        // Open Camera
        btnCapture.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }

        // Estimate paint from polygon selection
        btnEstimateArea.setOnClickListener {
            val areaPx = selectionView.getSelectedArea()
            if (areaPx > 0) {
                // NOTE: This is a simple assumption; calibrate as needed.
                // 100 px = 1 meter -> px² to m² conversion: divide by (100*100)
                val areaMeters = areaPx / (100.0 * 100.0)
                val paintRequired = areaMeters / 10.0 // default: 1 liter per 10 m²
                tvEstimate.text = "Estimated Paint (selection): %.2f L (Area: %.2f m²)".format(
                    paintRequired, areaMeters
                )
            } else {
                tvEstimate.text = "Please select area by tapping points."
            }
        }

        // Reset polygon
        btnReset.setOnClickListener {
            selectionView.resetSelection()
            tvEstimate.text = "Estimated Paint: "
        }

        // NEW: Open manual entry screen
        btnManual.setOnClickListener {
            val intent = Intent(this, ManualEntryActivity::class.java)
            startActivityForResult(intent, MANUAL_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val photo = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(photo)
        }

        // NEW: Handle result from manual screen
        if (requestCode == MANUAL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val liters = data?.getDoubleExtra("paintLiters", -1.0) ?: -1.0
            val area = data?.getDoubleExtra("areaSqM", -1.0) ?: -1.0
            if (liters >= 0.0 && area >= 0.0) {
                tvEstimate.text = "Estimated Paint (manual): %.2f L (Area: %.2f m²)".format(
                    liters, area
                )
            }
        }
    }
}
