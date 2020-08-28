package com.chen.free.readmodule

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller

class PageView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    //默认展示bBitmap,aBitmap在下面，直到页面开始切换
    //涉及到页面重复调用以最大程度的减少bitmap创建，复用的时候直接覆盖原来的像素
    private var aBitmap: Bitmap? = null
    private var bBitmap: Bitmap? = null

    private val defaultPath = Path()
    private val aPaint = Paint()
    private val bPaint = Paint()
    private var scroller = Scroller(context)

    init {
        aPaint.isAntiAlias = true//抗锯齿
        bPaint.isAntiAlias = true
        aPaint.textSize = 100f
        bPaint.textSize = 100f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        defaultPath.reset()
        defaultPath.moveTo(0f, 0f)
        defaultPath.lineTo(w.toFloat(), 0f)
        defaultPath.lineTo(w.toFloat(), h.toFloat())
        defaultPath.lineTo(0f, h.toFloat())
        defaultPath.close()
        aBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        bBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        aBitmap?.let {
            val canvas = Canvas(it)
            aPaint.color = Color.BLUE
            canvas.drawPath(defaultPath, aPaint)
            canvas.drawText("我是aBitmap", 100f, 100f, bPaint)
        }
        bBitmap?.let {
            val canvas = Canvas(it)
            bPaint.color = Color.YELLOW
            canvas.drawPath(defaultPath, bPaint)
            canvas.drawText("我是bBitmap", 100f, 100f, aPaint)
        }
    }

    private var touchX = 0f
    private var moveX = 0f
    private var moveDistance = 0f//触摸点到移动点的横向距离
    private var scrollStartX = 0f//开始滑动的x坐标
    private var scrollEndX = 0f//开始滑动的x坐标
    private var scrollCurX = 0f//开始滑动的x坐标


    private var isA = true//判断是否是aBitmap在上面

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!scroller.isFinished) {
                    scroller.abortAnimation()//如果动画没有结束，🖕中止动画
                }
                scrollCurX = 0f

                if (moveDistance!=0f){//在下一次触摸的时候上一次有没有左右滑动距离，有滑动距离则必然会翻页，翻页过则切换判断展示和未展示的bitmap是哪一个
                    isA=!isA
                    moveDistance = 0f
                }
                touchX = event.x
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                moveX = event.x
                moveDistance = moveX - touchX
                if (moveDistance > 0) {
                    scrollStartX = -measuredWidth + moveDistance
                } else if (moveDistance < 0) {
                    scrollStartX = moveDistance
                }
                scrollCurX = scrollStartX
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (moveDistance > 0) {
                    scrollEndX = measuredWidth - moveDistance
                } else if (moveDistance < 0) {
                    scrollEndX = -measuredWidth - moveDistance
                }else{

                    //没有滑动距离，代表是单纯的点击。
                    //上和左表示往前翻页，下和右往后翻页，点中间弹出菜单栏
                }
                scroller.startScroll(scrollStartX.toInt(), 0, scrollEndX.toInt(), 0, 200)

                touchX = 0f
                moveX = 0f
                scrollStartX = 0f//开始滑动的x坐标
                scrollEndX = 0f//开始滑动的x坐标
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            scrollCurX = scroller.currX.toFloat()
            invalidate()
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            when {
                moveDistance > 0 -> {
                    if (isA) {
                        aBitmap?.let { bm ->
                            it.drawBitmap(bm, 0f, 0f, aPaint)
                        }
                        bBitmap?.let { bm ->
                            it.drawBitmap(bm, scrollCurX, 0f, aPaint)
                        }
                    } else {
                        bBitmap?.let { bm ->
                            it.drawBitmap(bm, 0f, 0f, aPaint)
                        }
                        aBitmap?.let { bm ->
                            it.drawBitmap(bm, scrollCurX, 0f, aPaint)
                        }
                    }

                }
                moveDistance < 0 -> {
                    if (isA){
                        bBitmap?.let { bm ->
                            it.drawBitmap(bm, 0f, 0f, aPaint)
                        }
                        aBitmap?.let { bm ->
                            it.drawBitmap(bm, scrollCurX, 0f, aPaint)
                        }
                    }else{
                        aBitmap?.let { bm ->
                            it.drawBitmap(bm, 0f, 0f, aPaint)
                        }
                        bBitmap?.let { bm ->
                            it.drawBitmap(bm, scrollCurX, 0f, aPaint)
                        }
                    }

                }
                else -> {
                    if (isA){
                        aBitmap?.let { bm ->
                            it.drawBitmap(bm, 0f, 0f, aPaint)
                        }
                    }else{
                        bBitmap?.let { bm ->
                            it.drawBitmap(bm, 0f, 0f, aPaint)
                        }
                    }

                }
            }
        }
    }
}