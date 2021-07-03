import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

class WebServer(val port: Int = 8080) {
    private val log: Log = Log.of(WebServer::class.java)
    private val socket: ServerSocket

    init {
        val port = 8080
        log.info("Listening on port $port")
        socket = ServerSocket(port)
    }

    fun start() {
        while (true) {
            val client = socket.accept()
            log.info("Handling request")

            val request = parseRequest(client)
            // TODO(mlesniak) Handle request
            val ostream = client.getOutputStream()
            val resp = """
                HTTP/1.1 200 OK
                Content-Size: 3
                
                :-)
                """.trimIndent()
            ostream.write(resp.encodeToByteArray())

            ostream.close()
            // istream.close() // TODO(mlesniak) how to do this?
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
}

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val server = WebServer()
            server.start()
        }
    }
}

