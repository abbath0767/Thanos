package com.ng.thanoslayout.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class ThanosView : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    lateinit var infinityFist: InfinityFist
    private var movePointFactory = MovePointsFactory()
    private val interpolator = GaussianInterpolator(
        infinityFist.soulStone.sigma,
        infinityFist.soulStone.mu,
        infinityFist.soulStone.multipier
    )
    private lateinit var originalBitmap: Bitmap

    private var bitmapTable: MutableList<MutableList<Bitmap>> = mutableListOf()
    private var moveTable: MutableList<MutableList<MovePoints>> = mutableListOf()
    private var positionTable: MutableList<MutableList<PointDiff>> = mutableListOf()

    private val alphaPaint = Paint().apply {
        alpha = infinityFist.realityStone.alphaStart
    }
    private val utilRect = Rect()
    private var leftBorder = 0
    private var topBorder = 0
    private var tableWidth = 0
    private var tableHeight = 0

    private val SQUARE_SIZE = infinityFist.spaceStone.squareSize

    override fun onDraw(canvas: Canvas) {
        //todo simplify this
        //todo use bitmap square size, not calculate in place, for what?
        if (bitmapTable.isNotEmpty()) {
            bitmapTable.forEachIndexed { row, mutableListTable ->
                mutableListTable.forEachIndexed { column, bitmapSquare ->
                    if (row != tableHeight && column != tableWidth) {
                        utilRect.set(
                            leftBorder + positionTable[row][column].x,
                            topBorder + positionTable[row][column].y,
                            leftBorder + SQUARE_SIZE + positionTable[row][column].x,
                            topBorder + SQUARE_SIZE + positionTable[row][column].y
                        )
                    } else if (row == tableHeight && column != tableWidth) {
                        utilRect.set(
                            leftBorder + positionTable[row][column].x,
                            topBorder + positionTable[row][column].y,
                            leftBorder + SQUARE_SIZE + positionTable[row][column].x,
                            topBorder + originalBitmap.height - SQUARE_SIZE * tableHeight + positionTable[row][column].y
                        )
                    } else if (row != tableHeight && column == tableWidth) {
                        utilRect.set(
                            leftBorder + positionTable[row][column].x,
                            topBorder + positionTable[row][column].y,
                            leftBorder + originalBitmap.width - SQUARE_SIZE * tableWidth + positionTable[row][column].x,
                            topBorder + SQUARE_SIZE + positionTable[row][column].y
                        )
                    } else {
                        utilRect.set(
                            leftBorder + positionTable[row][column].x,
                            topBorder + positionTable[row][column].y,
                            leftBorder + originalBitmap.width - SQUARE_SIZE * tableWidth + positionTable[row][column].x,
                            topBorder + originalBitmap.height - SQUARE_SIZE * tableHeight + positionTable[row][column].y
                        )
                    }
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measureHeight = MeasureSpec.getSize(heightMeasureSpec)
        leftBorder = measureWidth / 4
        topBorder = measureHeight / 4
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    //todo simplify this
    private fun divideToSquares() {
        tableWidth = (originalBitmap.width) / SQUARE_SIZE
        tableHeight = (originalBitmap.height) / SQUARE_SIZE

        val squareRowMultipier =
            if (tableHeight * SQUARE_SIZE == originalBitmap.height) 0
            else 1
        val squareColumnMultipier =
            if (tableWidth * SQUARE_SIZE == originalBitmap.width) 0
            else 1

        for (row in 0 until tableHeight + squareRowMultipier) {
            bitmapTable.add(mutableListOf())
            moveTable.add(mutableListOf())
            positionTable.add(mutableListOf())
            for (column in 0 until tableWidth + squareColumnMultipier) {
                if (row != tableHeight && column != tableWidth) {
                    val bitmapSquare = Bitmap.createBitmap(
                        originalBitmap,
                        column * SQUARE_SIZE,
                        row * SQUARE_SIZE,
                        SQUARE_SIZE,
                        SQUARE_SIZE
                    )
                    bitmapTable[row].add(bitmapSquare)
                    moveTable[row].add(movePointFactory.generateMovePoint(column * SQUARE_SIZE, row * SQUARE_SIZE))
                    positionTable[row].add(
                        PointDiff(
                            column * SQUARE_SIZE,
                            row * SQUARE_SIZE
                        )
                    )
                } else if (row == tableHeight && column != tableWidth) {
                    val bitmapSquare = Bitmap.createBitmap(
                        originalBitmap,
                        column * SQUARE_SIZE,
                        row * SQUARE_SIZE,
                        SQUARE_SIZE,
                        originalBitmap.height - SQUARE_SIZE * tableHeight
                    )
                    bitmapTable[row].add(bitmapSquare)
                    moveTable[row].add(movePointFactory.generateMovePoint(column * SQUARE_SIZE, row * SQUARE_SIZE))
                    positionTable[row].add(
                        PointDiff(
                            column * SQUARE_SIZE,
                            row * SQUARE_SIZE
                        )
                    )
                } else if (row != tableHeight && column == tableWidth) {
                    val bitmapSquare = Bitmap.createBitmap(
                        originalBitmap,
                        column * SQUARE_SIZE,
                        row * SQUARE_SIZE,
                        originalBitmap.width - SQUARE_SIZE * tableWidth,
                        SQUARE_SIZE
                    )
                    bitmapTable[row].add(bitmapSquare)
                    moveTable[row].add(movePointFactory.generateMovePoint(column * SQUARE_SIZE, row * SQUARE_SIZE))
                    positionTable[row].add(
                        PointDiff(
                            column * SQUARE_SIZE,
                            row * SQUARE_SIZE
                        )
                    )
                } else {
                    val bitmapSquare = Bitmap.createBitmap(
                        originalBitmap,
                        column * SQUARE_SIZE,
                        row * SQUARE_SIZE,
                        originalBitmap.width - SQUARE_SIZE * tableWidth,
                        originalBitmap.height - SQUARE_SIZE * tableHeight
                    )
                    bitmapTable[row].add(bitmapSquare)
                    moveTable[row].add(movePointFactory.generateMovePoint(column * SQUARE_SIZE, row * SQUARE_SIZE))
                    positionTable[row].add(
                        PointDiff(
                            column * SQUARE_SIZE,
                            row * SQUARE_SIZE
                        )
                    )
                }
            }
        }

        invalidate()

        startDestroyAnimation()
    }

    private fun startDestroyAnimation() {
        ValueAnimator.ofInt(infinityFist.realityStone.alphaStart, infinityFist.realityStone.alphaEnd).apply {
            duration = infinityFist.timeStone.duration
            interpolator = this@ThanosView.interpolator
            addUpdateListener { value ->
                alphaPaint.alpha = value.animatedValue as Int
            }
            start()
        }
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = infinityFist.timeStone.duration
            //todo change to non-chaotic value
            interpolator = this@ThanosView.interpolator
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