package com.example.wallpaintestimator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ManualEntryActivity : AppCompatActivity() {

    private lateinit var rbByDimensions: RadioButton
    private lateinit var rbByArea: RadioButton
    private lateinit var etWidth: EditText
    private lateinit var etHeight: EditText
    private lateinit var etArea: EditText
    private lateinit var etOpenings: EditText
    private lateinit var etCoverage: EditText
    private lateinit var etCoats: EditText
    private lateinit var btnCalculateSend: Button
    private lateinit var tvPreview: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_entry)

        rbByDimensions = findViewById(R.id.rbByDimensions)
        rbByArea = findViewById(R.id.rbByArea)
        etWidth = findViewById(R.id.etWidth)
        etHeight = findViewById(R.id.etHeight)
        etArea = findViewById(R.id.etArea)
        etOpenings = findViewById(R.id.etOpenings)
        etCoverage = findViewById(R.id.etCoverage)
        etCoats = findViewById(R.id.etCoats)
        btnCalculateSend = findViewById(R.id.btnCalculateSend)
        tvPreview = findViewById(R.id.tvPreview)

        // Default: Dimensions mode ON
        rbByDimensions.isChecked = true
        updateMode()

        rbByDimensions.setOnCheckedChangeListener { _, _ -> updateMode() }
        rbByArea.setOnCheckedChangeListener { _, _ -> updateMode() }

        btnCalculateSend.setOnClickListener {
            val coats = etCoats.text.toString().toDoubleOrNull() ?: 1.0
            val coverage = etCoverage.text.toString().toDoubleOrNull() ?: 10.0 // m² per liter
            val openings = etOpenings.text.toString().toDoubleOrNull() ?: 0.0

            val area: Double? = if (rbByDimensions.isChecked) {
                val w = etWidth.text.toString().toDoubleOrNull()
                val h = etHeight.text.toString().toDoubleOrNull()
                if (w == null || h == null) {
                    Toast.makeText(this, "Enter width and height (meters).", Toast.LENGTH_SHORT).show()
                    null
                } else {
                    val a = w * h - openings
                    if (a <= 0) {
                        Toast.makeText(this, "Area must be > 0 after subtracting openings.", Toast.LENGTH_SHORT).show()
                        null
                    } else a
                }
            } else {
                val a = etArea.text.toString().toDoubleOrNull()
                if (a == null) {
                    Toast.makeText(this, "Enter area in m².", Toast.LENGTH_SHORT).show()
                    null
                } else {
                    val adjusted = a - openings
                    if (adjusted <= 0) {
                        Toast.makeText(this, "Area must be > 0 after subtracting openings.", Toast.LENGTH_SHORT).show()
                        null
                    } else adjusted
                }
            }

            if (area != null) {
                val liters = (area * coats) / coverage
                tvPreview.text = "Area: %.2f m²\nPaint needed: %.2f L".format(area, liters)

                // Send result back to MainActivity and close this screen
                val result = Intent().apply {
                    putExtra("paintLiters", liters)
                    putExtra("areaSqM", area)
                }
                setResult(Activity.RESULT_OK, result)
                finish()
            }
        }
    }

    private fun updateMode() {
        val dimensionsMode = rbByDimensions.isChecked
        etWidth.isEnabled = dimensionsMode
        etHeight.isEnabled = dimensionsMode
        etArea.isEnabled = !dimensionsMode
    }
}
