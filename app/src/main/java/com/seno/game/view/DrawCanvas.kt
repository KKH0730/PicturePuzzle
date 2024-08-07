package com.seno.game.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import com.seno.game.R
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * jhChoi - 201124
 * 그림이 그려질 canvas view
 */
class DrawCanvas : View {
    private val penSize = 50 //펜 사이즈
    private var drawCommandList: ArrayList<Pen> = ArrayList() //그리기 경로가 기록된 리스트
    private var paint: Paint = Paint()
    private var color = 0 //현재 펜 색상

    private var greenLength = 0

    init {
        init()
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        drawCommandList = ArrayList()
        color = Color.BLACK
    }

    val currentCanvas: Bitmap
        get() {
            val bitmap: Bitmap =
                Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            this.draw(canvas)
            return bitmap
        }

    fun changeColor(color: Int) {
        this.color = color
        paint.color = color
    }

    fun reset() {
        paint.reset()
        drawCommandList.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        greenLength = 0

        drawCommandList.forEachIndexed { index, pen ->
            val p: Pen = pen
            paint.apply {
                color = p.color
                style = Paint.Style.STROKE
                strokeWidth = p.size.toFloat()
            }

            if (p.isMove) {
                val prevP: Pen = drawCommandList[index - 1]
                canvas.drawLine(prevP.x, prevP.y, p.x, p.y, paint)
                if(pen.color == context.getColor(R.color.area_green)) {
                    greenLength += (penSize * getLineLength(prevP.x, prevP.y, p.x, p.y)).toInt()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        val action: Int = e.action
        val state: Int = if (action == MotionEvent.ACTION_DOWN) Pen.STATE_START else Pen.STATE_MOVE
        drawCommandList.add(Pen(e.x, e.y, state, color, penSize))
        invalidate()
        return true
    }

    private fun getLineLength(prevX: Float, prevY: Float, x: Float, y: Float): Float {
        return sqrt((x - prevX).pow(2) + (y - prevY).pow(2))
    }
}

class Pen(
    var x: Float, //펜의 좌표
    var y: Float, //현재 움직임 여부
    private var moveStatus: Int, //펜 색
    var color: Int, //펜 두께
    var size: Int,
) {

    val isMove: Boolean
        get() = moveStatus == STATE_MOVE

    companion object {
        const val STATE_START = 0 //펜의 상태(움직임 시작)
        const val STATE_MOVE = 1 //펜의 상태(움직이는 중)
    }
}