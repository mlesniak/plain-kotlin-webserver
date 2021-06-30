import java.net.ServerSocket

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val port = 8080

            println("Listening on port $port")
            val ss = ServerSocket(port)
            val client = ss.accept()

            val istream = client.getInputStream()
            val bs = istream.readAllBytes()
            val content = String(bs)
            println(content)

            val ostream = client.getOutputStream()
            ostream.write("Bye".encodeToByteArray())
        }
    }
}

