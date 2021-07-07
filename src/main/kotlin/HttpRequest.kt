import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

data class HttpRequest(
    val method: HttpMethod,
    val path: String,
) {
    var headers: MutableMap<String, String> = mutableMapOf()

    companion object {
        private fun parseHTTPHeader(input: String): HttpRequest {
            val lines = input.split("\r\n")
            val command = lines[0]
            val commands = command.split(" ")

            val headers = mutableMapOf<String, String>()

            try {
                val method = HttpMethod.valueOf(commands[0])
                val resource = commands[1]

                val httpRequest = HttpRequest(method, resource)
                httpRequest.headers = headers
                return httpRequest
            } catch (e: Exception) {
                throw IllegalArgumentException("Unable to parse HTTP request: $command")
            }
        }

        fun parseHTTPRequest(ist: InputStream): HttpRequest {
            val bufIs = BufferedReader(InputStreamReader(ist))

            // Read HTTP header and leave the rest for the handler.
            val sb = StringBuilder()
            while (true) {
                val line = bufIs.readLine() ?: break
                if (line == "") {
                    break
                }
                sb.append(line)
                sb.append("\r\n")
            }
            val header = sb.toString()

            // TODO(mlesniak) Refactor this
            return parseHTTPHeader(header)
        }
    }
}

