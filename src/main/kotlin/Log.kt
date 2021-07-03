import java.text.SimpleDateFormat
import java.util.*

class Log(val clazz: Class<out Any?>) {
    enum class Level {
        INFO
    }

    private fun defaultHandler(level: Level, msg: String) {
        val sdf = SimpleDateFormat("HH:MM:ss")
        val timestamp = sdf.format(Date())
        println("[$level] $timestamp $msg")
    }

    fun info(msg: String) {
        defaultHandler(Level.INFO, msg)
    }

    companion object {
        fun of(clazz: Class<out Any?>): Log = Log(clazz)
    }
}
