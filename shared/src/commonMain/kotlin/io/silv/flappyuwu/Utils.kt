package io.silv.flappyuwu

import kotlinx.coroutines.delay

const val TICK_RATE = 60L


suspend inline fun tickDelay() = delay(timeMillis = 1000L / TICK_RATE)
