class HttpRequestBuilder {
    companion object {

        fun parse(input: String): HttpRequest {
            // GET /wiki/Spezial:Search?search=Katzen&go=Artikel HTTP/1.1
            val lines = input.split("\r")
            val command = lines[0]
            // headers are the rest
            val commands = command.split(" ")

            val method = HttpMethod.valueOf(commands[0])
            val resource = commands[1]
            val protocol = commands[2]

            return HttpRequest(protocol, method, resource)
        }
    }
}

enum class HttpMethod {
    GET
}

data class HttpRequest(
    val protocol: String,
    val method: HttpMethod,
    val path: String,
)