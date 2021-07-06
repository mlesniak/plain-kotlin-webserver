import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

typealias HttpHandler = (HttpRequest, HttpResponse) -> Unit

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
            log.info("Accepting connection from=${client.remoteSocketAddress}")
            val request = parseRequest(client)

            handlers[request]?.let { handler ->
                log.info("Handling request $request")
                val response = HttpResponse()
                client.getOutputStream().use { os ->
                    handler(request, response)

                    os.write("HTTP/1.1 ${response.status} OK\n".toByteArray())
                    val bos = response.outputStream as ByteArrayOutputStream
                    os.write("Content-Size: ${bos.size()}\n".toByteArray())
                    os.write("\n".toByteArray())
                    os.write(bos.toByteArray())
                }
            } ?: IllegalArgumentException("No handler for $request")
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

    fun handle(method: HttpMethod, path: String, handler: (HttpRequest, HttpResponse) -> Unit) {
        handlers[HttpRequest(method, path)] = handler
    }
}
