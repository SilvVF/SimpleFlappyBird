package io.silv.flappyuwu

import kotlinx.coroutines.delay

const val TICK_RATE = 90L


suspend inline fun tickDelay(subractMillis: Long = 0L) = delay(timeMillis = (1000L / TICK_RATE) - subractMillis)
