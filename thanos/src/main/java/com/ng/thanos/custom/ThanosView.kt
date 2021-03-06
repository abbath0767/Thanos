package com.ng.thanos.custom

import android.animation.Animator
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
    lateinit var interpolator: GaussianInterpolator
    var squareSize = 0
    private var movePointFactory = MovePointsFactory()
    private lateinit var originalBitmap: Bitmap
    private var needDrawOriginal = false
    private lateinit var finishCallback: () -> Unit

    private var bitmapTable: MutableList<MutableList<Bitmap>> = mutableListOf()
    private var moveTable: MutableList<MutableList<MovePoints>> = mutableListOf()
    private var positionTable: MutableList<MutableList<PointDiff>> = mutableListOf()

    val alphaPaint = Paint()
    private val utilRect = Rect()
    var leftBorder = 0
    var topBorder = 0
    private var tableWidth = 0
    private var tableHeight = 0

    override fun onDraw(canvas: Canvas) {
        if (needDrawOriginal) {
            val rect = Rect(leftBorder, topBorder, originalBitmap.width + leftBorder, originalBitmap.height + topBorder)
            canvas.drawBitmap(originalBitmap, null, rect, null)
        }

        if (bitmapTable.isNotEmpty()) {
            bitmapTable.forEachIndexed { row, mutableListTable ->
                mutableListTable.forEachIndexed { column, bitmapSquare ->
                    setupRect(row, column)
                    canvas.drawBitmap(bitmapSquare, null, utilRect, alphaPaint)
                }
            }
        }
    }

    private fun setupRect(row: Int, column: Int) {
        if (row != tableHeight && column != tableWidth) {
            utilRect.set(
                leftBorder + positionTable[row][column].x,
                topBorder + positionTable[row][column].y,
                leftBorder + squareSize + positionTable[row][column].x,
                topBorder + squareSize + positionTable[row][column].y
            )
        } else if (row == tableHeight && column != tableWidth) {
            utilRect.set(
                leftBorder + positionTable[row][column].x,
                topBorder + positionTable[row][column].y,
                leftBorder + squareSize + positionTable[row][column].x,
                topBorder + originalBitmap.height - squareSize * tableHeight + positionTable[row][column].y
            )
        } else if (row != tableHeight && column == tableWidth) {
            utilRect.set(
                leftBorder + positionTable[row][column].x,
                topBorder + positionTable[row][column].y,
                leftBorder + originalBitmap.width - squareSize * tableWidth + positionTable[row][column].x,
                topBorder + squareSize + positionTable[row][column].y
            )
        } else {
            utilRect.set(
                leftBorder + positionTable[row][column].x,
                topBorder + positionTable[row][column].y,
                leftBorder + originalBitmap.width - squareSize * tableWidth + positionTable[row][column].x,
                topBorder + originalBitmap.height - squareSize * tableHeight + positionTable[row][column].y
            )
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        needDrawOriginal = true
        originalBitmap = bitmap
        invalidate()
    }

    fun destroy(finishCallback: () -> Unit) {
        this.finishCallback = finishCallback
        divideToSquares()
    }

    private fun divideToSquares() {
        tableWidth = (originalBitmap.width) / squareSize
        tableHeight = (originalBitmap.height) / squareSize

        val squareRowMultipier =
            if (tableHeight * squareSize == originalBitmap.height) 0
            else 1
        val squareColumnMultipier =
            if (tableWidth * squareSize == originalBitmap.width) 0
            else 1

        for (row in 0 until tableHeight + squareRowMultipier) {
            bitmapTable.add(mutableListOf())
            moveTable.add(mutableListOf())
            positionTable.add(mutableListOf())
            for (column in 0 until tableWidth + squareColumnMultipier)
                setUpSquare(row, column)
        }

        invalidate()

        startDestroyAnimation()
    }

    private fun setUpSquare(row: Int, column: Int) {
        if (row != tableHeight && column != tableWidth) {
            val bitmapSquare = Bitmap.createBitmap(
                originalBitmap,
                column * squareSize,
                row * squareSize,
                squareSize,
                squareSize
            )
            bitmapTable[row].add(bitmapSquare)
            moveTable[row].add(movePointFactory.generateMovePoint(column * squareSize, row * squareSize))
            positionTable[row].add(
                PointDiff(
                    column * squareSize,
                    row * squareSize
                )
            )
        } else if (row == tableHeight && column != tableWidth) {
            val bitmapSquare = Bitmap.createBitmap(
                originalBitmap,
                column * squareSize,
                row * squareSize,
                squareSize,
                originalBitmap.height - squareSize * tableHeight
            )
            bitmapTable[row].add(bitmapSquare)
            moveTable[row].add(movePointFactory.generateMovePoint(column * squareSize, row * squareSize))
            positionTable[row].add(
                PointDiff(
                    column * squareSize,
                    row * squareSize
                )
            )
        } else if (row != tableHeight && column == tableWidth) {
            val bitmapSquare = Bitmap.createBitmap(
                originalBitmap,
                column * squareSize,
                row * squareSize,
                originalBitmap.width - squareSize * tableWidth,
                squareSize
            )
            bitmapTable[row].add(bitmapSquare)
            moveTable[row].add(movePointFactory.generateMovePoint(column * squareSize, row * squareSize))
            positionTable[row].add(
                PointDiff(
                    column * squareSize,
                    row * squareSize
                )
            )
        } else {
            val bitmapSquare = Bitmap.createBitmap(
                originalBitmap,
                column * squareSize,
                row * squareSize,
                originalBitmap.width - squareSize * tableWidth,
                originalBitmap.height - squareSize * tableHeight
            )
            bitmapTable[row].add(bitmapSquare)
            moveTable[row].add(movePointFactory.generateMovePoint(column * squareSize, row * squareSize))
            positionTable[row].add(
                PointDiff(
                    column * squareSize,
                    row * squareSize
                )
            )
        }
    }

    private fun startDestroyAnimation() {
        ValueAnimator.ofInt(infinityFist.realityStone.alphaStart, infinityFist.realityStone.alphaEnd).apply {
            duration = infinityFist.timeStone.duration
            interpolator = this@ThanosView.interpolator
            addUpdateListener { value ->
                alphaPaint.alpha = value.animatedValue as Int
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    finishCallback.invoke()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            needDrawOriginal = false
            start()
        }
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = infinityFist.timeStone.duration
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