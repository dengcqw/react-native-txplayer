package com.txplayer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
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

    val charList = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")

    var hBlank: Int = 0
    var vBlank: Int = 0

    fun updateVideoSize(width: Int, height: Int, viewW: Int, viewH: Int) {
        hBlank = viewW - width
        vBlank = viewH - height
        invalidate()
    }

    var themeColor: Int = Color.YELLOW
        set(value) {
            field = value
            borderPaint.color = value
        }
    // 数据源：每个元素包含 x, y, w, h（double 类型）
    var areas:  List<AreaObj>? = null
        set(value) {
            field = value
            invalidate() // 数据变化时重绘
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

            val left = x * (width - hBlank/2) + hBlank/2
            val top = y * (height - vBlank/2) + vBlank/2
            val right = left + w * (width - hBlank)
            val bottom = top + h * (height - vBlank)

            // 绘制矩形边框
            canvas.drawRect(left, top, right, bottom, borderPaint)

            // 准备文字：显示序号（从1开始）
            val text = charList[index]

            // 计算文字尺寸，用于垂直居中
            textPaint.getTextBounds(text, 0, text.length, textBounds)
            val textHeight = textBounds.height()
            val centerX = left + w * width / 2
            val centerY = top + h* height / 2

            // 计算基线的 y 坐标（使文字垂直居中）
            val baseline = centerY + (textPaint.descent() - textPaint.ascent()) / 2 - textPaint.descent()

            // 绘制文字
            canvas.drawText(text, centerX, baseline, textPaint)
        }
    }
}