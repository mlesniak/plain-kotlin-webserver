class HttpRequestBuilder {
    companion object {

        fun parse(input: String): HttpRequest {
            // GET /wiki/Spezial:Search?search=Katzen&go=Artikel HTTP/1.1
            val lines = input.split("\r\n")
            val command = lines[0]
            // headers are the rest
            val commands = command.split(" ")

            try {
                val method = HttpMethod.valueOf(commands[0])
                val resource = commands[1]
                val protocol = commands[2]

                return HttpRequest(protocol, method, resource)
            } catch (e: Exception) {
                TODO(commands[0])
            }
        }
    }
}

enum class HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS,

}

data class HttpRequest(
    val protocol: String,
    val method: HttpMethod,
    val path: String,
)