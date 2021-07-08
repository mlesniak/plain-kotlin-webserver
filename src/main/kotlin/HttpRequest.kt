import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

data class HttpRequest(
    val method: HttpMethod,
    val path: String,
) {
    var headers: MutableMap<String, String> = mutableMapOf()
    var inputStream: InputStream = InputStream.nullInputStream()

    companion object {
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
                sb.append("\n")
            }
            sb.deleteCharAt(sb.length - 1)

            val lines = sb.toString().split("\n")
            val command = lines[0]
            val requestParts = command.split(" ")

            val plainHeaders = mutableMapOf<String, String>()
            for (i in 1 until lines.size) {
                val header = lines[i]
                val parts = header.split(":")
                plainHeaders[parts[0].trim()] = parts[1].trim()
            }

            try {
                val method = HttpMethod.valueOf(requestParts[0])
                val resource = requestParts[1]

                return HttpRequest(method, resource).apply {
                    headers = plainHeaders
                    inputStream = ist
                }
            } catch (e: Exception) {
                throw IllegalArgumentException("Unable to parse HTTP request: $command")
            }
        }
    }
}

