package com.txplayer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class HighlightAreaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 直接消费所有事件
        return true
    }

    val charList = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O")

    var hBlank: Int = 0
    var vBlank: Int = 0

    data class Size(val width: Float, val height: Float)

    data class BlankResult(
        val horizontal: Float,
        val vertical: Float
    )

    fun calculateBlank(
        viewSize: Size,
        videoSize: Size
    ): BlankResult {

        val viewRatio = viewSize.width / viewSize.height
        val videoRatio = videoSize.width / videoSize.height

        return if (videoRatio > viewRatio) {
            // 👉 视频更“宽”（横向填满）
            val scale = viewSize.width / videoSize.width
            val scaledHeight = videoSize.height * scale

            val blank = (viewSize.height - scaledHeight).coerceAtLeast(0f)
            BlankResult(horizontal = 0f, vertical = blank)

        } else {
            // 👉 视频更“高”（竖向填满）
            val scale = viewSize.height / videoSize.height
            val scaledWidth = videoSize.width * scale

            val blank = (viewSize.width - scaledWidth).coerceAtLeast(0f)
            BlankResult(horizontal = blank, vertical = 0f)
        }
    }

    fun updateVideoSize(width: Int, height: Int, viewW: Int, viewH: Int) {
        Log.d("AreaView", "updateVideoSize: ${this.width} selfh=${this.height}")
        val view = Size(viewW.toFloat(), viewH.toFloat())
        val video = Size(width.toFloat(), height.toFloat())
        val result = calculateBlank(view, video)
        hBlank = result.horizontal.toInt()
        vBlank = result.vertical.toInt()
        invalidate()
    }

    var bgColor =  Color.parseColor("#0dff9800")
    var showText = false
    var themeColor: Int = Color.YELLOW
        set(value) {
            field = value
            borderPaint.color = value
        }
    // 数据源：每个元素包含 x, y, w, h（double 类型）
    var areas:  List<AreaObj>? = null
        set(value) {
            field = value
            showText = if (value != null && value.size > 1) {
                true
            } else {
                false
            }
            invalidate() // 数据变化时重绘
        }

    // 背景画笔
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // 边框画笔
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        val scale = resources.displayMetrics.density
        strokeWidth = (1 * scale + 0.5f)
    }

    // 文字画笔
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK // 文字颜色可自定义
        val scale = resources.displayMetrics.density
        textSize = 18 * scale
        textAlign = Paint.Align.CENTER // 水平居中
    }

    // 用于测量文字尺寸的临时 Rect
    private val textBounds = Rect()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        areas?.forEachIndexed { index, area ->
            val x = area.x
            val y = area.y
            val w = area.w
            val h = area.h

            // 如果宽高为0，跳过绘制
            if ((w <= 0.toFloat()) || (h <= 0.toFloat())) {
                return@forEachIndexed
            }

            val left = x * (width - hBlank) + hBlank/2
            val top = y * (height - vBlank) + vBlank/2
            val right = left + w * (width - hBlank)
            val bottom = top + h * (height - vBlank)

            // 绘制矩形背景
            bgPaint.color = bgColor
            canvas.drawRect(left, top, right, bottom, bgPaint)

            // 绘制矩形边框
            canvas.drawRect(left, top, right, bottom, borderPaint)

            if (showText) {
                // 准备文字：显示序号（从1开始）
                val text = charList[index]

                // 计算文字尺寸，用于垂直居中
                textPaint.getTextBounds(text, 0, text.length, textBounds)
                val textHeight = textBounds.height()
                val centerX = left + w * (width - hBlank) / 2
                val centerY = top + h * (height - vBlank) / 2

                // 计算基线的 y 坐标（使文字垂直居中）
                val baseline = centerY + (textPaint.descent() - textPaint.ascent()) / 2 - textPaint.descent()

                // 绘制文字
                canvas.drawText(text, centerX, baseline, textPaint)
            }
        }
    }
}