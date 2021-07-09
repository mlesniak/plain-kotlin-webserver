import java.util.*

/**
 * Entry point.
 **/
class Main {
    companion object {
        private val log: Log = Log.of(Main::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val server = WebServer()

            server.handle(HttpMethod.GET, "/") { req, resp ->
                log.info("Received request")
                val hs = req.headers.map { it.toString()}.joinToString { it }
                resp.writeln("Response.")
                resp.writeln("Received Headers: $hs")
                resp.addHeader("Timestamp", Date().toString())
            }

            server.start()
        }
    }
}

