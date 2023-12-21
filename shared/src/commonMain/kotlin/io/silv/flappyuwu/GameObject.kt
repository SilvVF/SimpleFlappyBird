package io.silv.flappyuwu

interface GameObject {

    val type: Int
        get() = 0

    fun onTick(interp: Float)

    fun shouldDestroy(): Boolean
}
