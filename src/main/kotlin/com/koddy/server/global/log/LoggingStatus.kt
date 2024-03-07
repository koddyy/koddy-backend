package com.koddy.server.global.log

class LoggingStatus(
    private val startTimeMillis: Long = System.currentTimeMillis(),
    private var depthLevel: Int = 0,
) {
    fun increaseDepth() {
        depthLevel++
    }

    fun decreaseDepth() {
        depthLevel--
    }

    fun depthPrefix(prefixString: String): String {
        if (depthLevel == 1) {
            return "|$prefixString"
        }
        val bar: String = "|" + " ".repeat(prefixString.length)
        return bar.repeat(depthLevel - 1) + "|$prefixString"
    }

    fun calculateTakenTime(): Long = System.currentTimeMillis() - startTimeMillis
}
