package com.ng.thanos.custom

import java.util.*

//todo add mormal javadoc
class MovePointsFactory {

    //to right from left...
//    X--\       --/X
//        \  /--/
//         --
    private val rand = Random()

    companion object {
        //todo change to stone
        //        private const val MOVE_DIFF_VALUE = 25
        private const val MOVE_DIFF_VALUE = 50
    }

    //test and expirent with best parameters
    fun generateMovePoint(startX: Int, startY: Int): MovePoints {
        return MovePoints(
            startX = startX,
            startY = startY,
            midX = startX + rand.nextInt(MOVE_DIFF_VALUE),
            midY = startY + rand.nextInt(MOVE_DIFF_VALUE) - MOVE_DIFF_VALUE,
            endX = startX + rand.nextInt(MOVE_DIFF_VALUE * 2),
            endY = startY + rand.nextInt(MOVE_DIFF_VALUE * 4) - MOVE_DIFF_VALUE
//            midX = startX + rand.nextInt(MOVE_DIFF_VALUE),
//            midY = startY + rand.nextInt(MOVE_DIFF_VALUE * 2) - MOVE_DIFF_VALUE,
//            endX = startX + rand.nextInt(MOVE_DIFF_VALUE * 4),
//            endY = startY + rand.nextInt(MOVE_DIFF_VALUE * 8) - MOVE_DIFF_VALUE * 4
        )
    }
}