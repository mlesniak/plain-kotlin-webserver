import java.text.SimpleDateFormat
import java.util.*

/**
 * Poor man's logging class to prevent additional dependencies.
 **/
class Log(val clazz: Class<out Any?>) {
    enum class Level {
        INFO,
        ERROR
    }

    private fun defaultHandler(level: Level, msg: String) {
        val sdf = SimpleDateFormat("HH:MM:ss")
        val timestamp = sdf.format(Date())
        val l = level.toString().padEnd(5, ' ')
        println("[$l] $timestamp $msg")
    }

    fun info(msg: String) {
        defaultHandler(Level.INFO, msg)
    }

    fun error(msg: String) {
        defaultHandler(Level.ERROR, msg)
    }

    companion object {
        fun of(clazz: Class<out Any?>): Log = Log(clazz)
    }
}
