package com.dokar.arcmotiondemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.transition.ArcMotion
import kotlin.math.max
import kotlin.math.min

class ArcPathView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val density = context.resources.displayMetrics.density

    private val pointRadius = density * 8
    private val pointPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.point_color)
        strokeWidth = pointRadius * 2
        strokeCap = Paint.Cap.ROUND
    }

    private val pathPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.path_color)
        strokeWidth = density * 4
        style = Paint.Style.STROKE
        val eightDp = density * 8
        pathEffect = DashPathEffect(floatArrayOf(eightDp, eightDp), 0f)
    }

    private val arcMotion = ArcMotion()

    var path: Path? = null
        private set

    private  var moved = false
    private var isStartPointMoving = false
    private var isEndPointMoving = false

    private var startX = 0f
        set(value) {
            field = value
            updatePath()
        }
    private var startY = 0f
        set(value) {
            field = value
            updatePath()
        }
    private var endX = 0f
        set(value) {
            field = value
            updatePath()
        }
    private var endY = 0f
        set(value) {
            field = value
            updatePath()
        }

    var maxAngle = arcMotion.maximumAngle
        set(value) {
            field = value
            updatePath()
        }
    var minHorizontalAngle = arcMotion.maximumAngle
        set(value) {
            field = value
            updatePath()
        }
    var minVerticalAngle = arcMotion.maximumAngle
        set(value) {
            field = value
            updatePath()
        }

    private fun updatePath() {
        arcMotion.maximumAngle = maxAngle
        arcMotion.minimumHorizontalAngle = minHorizontalAngle
        arcMotion.minimumVerticalAngle = minVerticalAngle
        path = arcMotion.getPath(startX, startY, endX, endY)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (moved) {
            return
        }
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val margin = 48 * density
        startX = margin
        startY = h - margin
        endX = w - margin
        endY = margin
        updatePath()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (path != null) {
            canvas!!.drawPath(path!!, pathPaint)
        }

        canvas!!.drawPoint(startX, startY, pointPaint)
        canvas.drawPoint(endX, endY, pointPaint)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                if (isOnStartPoint(x, y)) {
                    isStartPointMoving = true
                } else if (isOnEndPoint(x, y)) {
                    isEndPointMoving = true
                }
                if (isStartPointMoving || isEndPointMoving) {
                    moved = true
                    performHapticFeedback(0)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val x = max(pointRadius, min(event.x, width - pointRadius))
                val y = max(pointRadius, min(event.y, height - pointRadius))
                if (isStartPointMoving) {
                    startX = x
                    startY = y
                    updatePath()
                } else if (isEndPointMoving) {
                    endX = x
                    endY = y
                    updatePath()
                }
            }
            MotionEvent.ACTION_UP -> {
                isStartPointMoving = false
                isEndPointMoving = false
            }
        }
        return isStartPointMoving || isEndPointMoving
    }

    private fun isOnStartPoint(x: Float, y: Float): Boolean {
        val fourDp = density * 8
        val r = pointRadius + fourDp
        if (x < startX - r
            || x > startX + r
            || y < startY - r
            || y > startY + r
        ) {
            return false
        }
        return true
    }

    private fun isOnEndPoint(x: Float, y: Float): Boolean {
        val fourDp = density * 8
        val r = pointRadius + fourDp
        if (x < endX - r
            || x > endX + r
            || y < endY - r
            || y > endY + r
        ) {
            return false
        }
        return true
    }
}