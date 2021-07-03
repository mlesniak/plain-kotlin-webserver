import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

class WebServer(val port: Int = 8080) {
    private val socket: ServerSocket

    init {
        val port = 8080
        println("Listening on port $port")
        socket = ServerSocket(port)
    }

    fun start() {
        while (true) {
            val client = socket.accept()

            val sb = StringBuilder()
            val istream = BufferedReader(InputStreamReader(client.getInputStream()))

            while (true) {
                var line = istream.readLine() ?: break
                println("Receiving '$line'")
                if (line == "") {
                    break
                }
                sb.append(line)
                sb.append("\r\n") // TODO(mlesniak) strange pattern, works for now...
            }

            val content = sb.toString()
            val httpRequest = HttpRequestBuilder.parse(content)
            println(httpRequest.toString())

            val ostream = client.getOutputStream()
            val resp = """
                HTTP/1.1 200 OK
                
                :-)
                """.trimIndent()
            ostream.write(resp.encodeToByteArray())

            ostream.close()
            istream.close()
        }
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

