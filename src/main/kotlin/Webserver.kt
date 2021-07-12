import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.ServerSocket

typealias HttpHandler = (HttpRequest, HttpResponse) -> Unit

class WebServer(private val port: Int = 8080) {
    private val log: Log = Log.of(WebServer::class.java)
    private val handlers: MutableMap<HttpRequest, HttpHandler> = mutableMapOf()

    /**
     * Entry function to start listening for connections.
     **/
    fun start() {
        log.info("Listening on port $port")
        val socket = ServerSocket(port)

        while (true) {
            socket.accept().use { client ->
                log.info("Accepting connection from=${client.remoteSocketAddress}")
                val request = HttpRequest.parseHTTPRequest(client.getInputStream())

                handlers[request]?.let { handler ->
                    log.info("Handling request $request")
                    val response = HttpResponse()
                    client.getOutputStream().use { os ->
                        handler(request, response)
                        sendHTTPResponse(os, response)
                    }
                } ?: IllegalArgumentException("No handler found for $request")
            }
        }
    }

    private fun sendHTTPResponse(os: OutputStream, response: HttpResponse) {
        val bos = response.outputStream as ByteArrayOutputStream

        // HTTP response part.
        os.writeLine("HTTP/1.1 ${response.status} OK")
        os.writeLine("Content-Length: ${bos.size()}")
        response.headers.forEach { entry ->
            os.writeLine("${entry.key}:${entry.value}")
        }
        os.writeLine()

        // Payload.
        os.write(bos.toByteArray())
        bos.close()
    }

    private fun OutputStream.writeLine(s: String = "") {
        val snl = s + "\n"
        this.write(snl.toByteArray())
    }

    fun handle(method: HttpMethod, path: String, handler: HttpHandler) {
        handlers[HttpRequest(method, path)] = handler
    }
}
