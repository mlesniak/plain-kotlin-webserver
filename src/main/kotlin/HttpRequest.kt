data class HttpRequest(
    val method: HttpMethod,
    val path: String,
) {
    var headers: MutableMap<String, String> = mutableMapOf()

    companion object {
        fun of(input: String): HttpRequest {
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
    }
}

