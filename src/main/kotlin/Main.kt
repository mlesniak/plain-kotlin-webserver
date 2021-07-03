import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

typealias HttpHandler = (HttpRequest) -> Unit

class WebServer(port: Int = 8080) {
    private val log: Log = Log.of(WebServer::class.java)
    private val socket: ServerSocket
    private val handlers: MutableMap<HttpRequest, HttpHandler> = mutableMapOf()

    init {
        log.info("Listening on port $port")
        socket = ServerSocket(port)
    }

    fun start() {
        while (true) {
            val client = socket.accept()
            log.info("Handling request")

            val request = parseRequest(client)
            request.ostream = client.getOutputStream()

            val h = handlers[request]
            if (h == null) {
                request.ostream.close()
                continue
            }
            h(request)

            request.ostream.close()
        }
    }

    private fun parseRequest(client: Socket): HttpRequest {
        val istream = BufferedReader(InputStreamReader(client.getInputStream()))

        val sb = StringBuilder()
        while (true) {
            var line = istream.readLine() ?: break
            if (line == "") {
                break
            }
            sb.append(line)
            sb.append("\r\n")
        }
        val content = sb.toString()

        return HttpRequestBuilder.parse(content)
    }

    fun handle(method: HttpMethod, path: String, handler: (HttpRequest) -> Unit) {
        handlers[HttpRequest(method, path)] = handler
    }
}

class Main {
    companion object {
        private val log: Log = Log.of(Main::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val server = WebServer()
            server.handle(HttpMethod.GET, "/") {
                log.info("Handling it")

                val resp = """
                    HTTP/1.1 200 OK
                    Content-Size: 3

                    :-)
                    """.trimIndent()
                it.ostream.write(resp.encodeToByteArray())
            }
            server.start()
        }
    }
}

