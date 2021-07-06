
class Main {
    companion object {
        private val log: Log = Log.of(Main::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val server = WebServer()

            server.handle(HttpMethod.GET, "/") { _, resp ->
                log.info("Received request")
                resp.outputStream.write("Hello, world".toByteArray())
            }

            server.start()
        }
    }
}

