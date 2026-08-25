package org.riza.iluvmusix.helper

fun formatDuration(durationMs: Long?): String {
    if (durationMs == null || durationMs <= 0L) return "0 Seconds"

    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return buildString {
        if (minutes > 0) {
            append("$minutes ${if (minutes == 1L) "Minute" else "Minutes"}")
        }
        if (seconds > 0) {
            if (isNotEmpty()) append(" ")
            append("$seconds ${if (seconds == 1L) "Second" else "Seconds"}")
        }
    }.ifEmpty { "0 Seconds" }
}