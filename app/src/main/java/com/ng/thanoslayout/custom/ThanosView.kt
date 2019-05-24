package com.ng.thanoslayout.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.ng.thanoslayout.custom.stone.PointDiff

class ThanosView : View {

    companion object {
        //todo change to stone parameters
        private const val ALPHA_START = 255
        private const val ALPHA_END = 0
        private const val SQUARE_SIZE = 10
        private const val DURATION = 6000L
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private var movePointFactory = MovePointsFactory()
    private var originalBitmap: Bitmap? = null

    private var bitmapTable: MutableList<MutableList<Bitmap>> = mutableListOf()
    private var moveTable: MutableList<MutableList<MovePoints>> = mutableListOf()
    private var positionTable: MutableList<MutableList<PointDiff>> = mutableListOf()

    private val alphaPaint = Paint().apply {
        alpha = ALPHA_START
    }
    private val utilRect = Rect()
    private var leftBorder = 0
    private var topBorder = 0
    private var rightBorder = 0
    private var botBorder = 0

    override fun onDraw(canvas: Canvas) {
        originalBitmap?.let {
            val rect = Rect(width / 4, height / 4, width - width / 4, height - height / 4)
            canvas.drawBitmap(it, null, rect, null)
        }

        if (bitmapTable.isNotEmpty()) {
            bitmapTable.forEachIndexed { row, mutableListTable ->
                mutableListTable.forEachIndexed { column, bitmapSquare ->
                    utilRect.set(
                        leftBorder + positionTable[row][column].x,
                        topBorder + positionTable[row][column].y,
                        leftBorder + SQUARE_SIZE + positionTable[row][column].x,
                        topBorder + SQUARE_SIZE + positionTable[row][column].y
                    )
                    canvas.drawBitmap(bitmapSquare, null, utilRect, alphaPaint)
                }
            }
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        originalBitmap = bitmap
        invalidate()
    }

    fun destroy() {
        divideToSquares()
    }

    private fun divideToSquares() {
        //todo move to on measure
        leftBorder = width / 4
        topBorder = height / 4
        rightBorder = width - width / 4
        botBorder = height - height / 4
        val tableWidth = (originalBitmap?.width ?: 0) / SQUARE_SIZE
        val tableHeight = (originalBitmap?.height ?: 0) / SQUARE_SIZE

        for (row in 0 until tableWidth) {
            bitmapTable.add(mutableListOf())
            moveTable.add(mutableListOf())
            positionTable.add(mutableListOf())
            for (column in 0 until tableHeight) {
                val bitmapSquare = Bitmap.createBitmap(
                    originalBitmap,
                    row * SQUARE_SIZE,
                    column * SQUARE_SIZE,
                    SQUARE_SIZE,
                    SQUARE_SIZE
                )
                bitmapTable[row].add(bitmapSquare)
                moveTable[row].add(movePointFactory.generateMovePoint(row * SQUARE_SIZE, column * SQUARE_SIZE))
                positionTable[row].add(PointDiff(0, 0))
            }
        }

        originalBitmap = null
//        invalidate()

        startDestroyAnimation()
    }

    private fun startDestroyAnimation() {
        ValueAnimator.ofInt(ALPHA_START, ALPHA_END).apply {
            duration = DURATION
            interpolator = AccelerateInterpolator()
            addUpdateListener { value ->
                alphaPaint.alpha = value.animatedValue as Int
            }
            start()
        }
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = DURATION
            //add custom interpolator
            interpolator = AccelerateInterpolator()
            addUpdateListener { value ->
                for (row in 0 until positionTable.size) {
                    for (column in 0 until positionTable[row].size) {
                        positionTable[row][column] =
                                PointDiff(
                                    x = calcBezier(
                                        value.animatedValue as Float,
                                        moveTable[row][column].startX.toFloat(),
                                        moveTable[row][column].midX.toFloat(),
                                        moveTable[row][column].endX.toFloat()
                                    ).toInt(),
                                    y = calcBezier(
                                        value.animatedValue as Float,
                                        moveTable[row][column].startY.toFloat(),
                                        moveTable[row][column].midY.toFloat(),
                                        moveTable[row][column].endY.toFloat()
                                    ).toInt()
                                )
                    }
                }

                postInvalidateOnAnimation()
            }
            start()
        }
    }

    private fun calcBezier(time: Float, p0: Float, p1: Float, p2: Float): Float {
        return (Math.pow((1 - time).toDouble(), 2.0) * p0
                + (2f * (1 - time) * time * p1).toDouble()
                + Math.pow(time.toDouble(), 2.0) * p2).toFloat()
    }
}