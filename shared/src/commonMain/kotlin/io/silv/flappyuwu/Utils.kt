package io.silv.flappyuwu

import kotlinx.coroutines.delay

var TICK_RATE = 120L


suspend inline fun tickDelay(subractMillis: Long = 0L) = delay(timeMillis = (1000L / TICK_RATE) - subractMillis)
