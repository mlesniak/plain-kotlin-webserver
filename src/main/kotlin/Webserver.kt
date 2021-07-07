import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

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
            val client = socket.accept()
            log.info("Accepting connection from=${client.remoteSocketAddress}")
            val request = parseRequest(client)

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

    private fun sendHTTPResponse(os: OutputStream, response: HttpResponse) {
        val bos = response.outputStream as ByteArrayOutputStream

        os.writeLine("HTTP/1.1 ${response.status} OK")
        os.writeLine("Content-Length: ${bos.size()}")
        os.writeLine()

        os.write(bos.toByteArray())
        bos.close()
    }

    private fun OutputStream.writeLine(s: String = "") {
        val snl = s + "\n"
        this.write(snl.toByteArray())
    }

    fun handle(method: HttpMethod, path: String, handler: (HttpRequest, HttpResponse) -> Unit) {
        handlers[HttpRequest(method, path)] = handler
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

        return HttpRequest.of(content)
    }
}
