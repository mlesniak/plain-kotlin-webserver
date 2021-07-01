import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val port = 8080

            println("Listening on port $port")
            val ss = ServerSocket(port)
            val client = ss.accept()

            val sb = StringBuilder()
            val istream = BufferedReader(InputStreamReader(client.getInputStream()))

            var newlines = 0
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
            ostream.write("Bye".encodeToByteArray())
        }
    }
}

