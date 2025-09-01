package com.example.wallpaintestimator

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * SelectionView lets user tap to place dots (vertices) on an image.
 * Dots are connected into a polygon and the inside is highlighted.
 */
class SelectionView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val points = mutableListOf<PointF>()

    private val dotPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val linePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val fillPaint = Paint().apply {
        color = 0x40FF0000 // semi-transparent red fill
        style = Paint.Style.FILL
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // Add a new point where user tapped
            points.add(PointF(event.x, event.y))
            invalidate()
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw polygon if we have at least 2 points
        if (points.size > 1) {
            val path = Path()
            path.moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
            }
            // Close polygon if more than 2 points
            if (points.size > 2) {
                path.close()
                canvas.drawPath(path, fillPaint)
            }
            canvas.drawPath(path, linePaint)
        }

        // Draw dots
        for (point in points) {
            canvas.drawCircle(point.x, point.y, 10f, dotPaint)
        }
    }

    /** Clear selection */
    fun resetSelection() {
        points.clear()
        invalidate()
    }

    /** Get polygon points */
    fun getPolygonPoints(): List<PointF> {
        return points
    }

    /** Calculate polygon area (in pixels) using shoelace formula */
    fun getSelectedArea(): Double {
        if (points.size < 3) return 0.0
        var area = 0.0
        for (i in points.indices) {
            val j = (i + 1) % points.size
            area += (points[i].x * points[j].y - points[j].x * points[i].y)
        }
        return kotlin.math.abs(area / 2.0)
    }
}
