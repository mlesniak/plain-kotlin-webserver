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
                    writeOutput(os, response)
                }
            } ?: IllegalArgumentException("No handler for $request")
        }
    }

    private fun writeOutput(os: OutputStream, response: HttpResponse) {
        os.write("HTTP/1.1 ${response.status} OK\n".toByteArray())
        val bos = response.outputStream as ByteArrayOutputStream
        os.write("Content-Length: ${bos.size()}\n".toByteArray())
        os.write("\n".toByteArray())
        os.write(bos.toByteArray())
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
