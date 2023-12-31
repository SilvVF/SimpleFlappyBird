expect fun nanoTime(): Long

private const val MS_TO_NS = 1_000_000L
private const val MAX_MS = Long.MAX_VALUE / MS_TO_NS

/**
 * First-line overflow protection -- limit maximal delay.
 * Delays longer than this one (~146 years) are considered to be delayed "forever".
 */
private const val MAX_DELAY_NS = Long.MAX_VALUE / 2

internal fun delayToNanos(timeMillis: Long): Long = when {
    timeMillis <= 0 -> 0L
    timeMillis >= MAX_MS -> Long.MAX_VALUE
    else -> timeMillis * MS_TO_NS
}

internal fun delayNanosToMillis(timeNanos: Long): Long =
    timeNanos / MS_TO_NS

