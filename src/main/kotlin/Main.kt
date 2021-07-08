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

                // TODO(mlesniak) allow to send headers as well

                val hs = req.headers.map { it.toString()}.joinToString { it }
                resp.writeln("Hello, world")
                resp.writeln("Headers: $hs")
            }

            server.start()
        }
    }
}

