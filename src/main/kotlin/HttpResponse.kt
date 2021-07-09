import java.io.ByteArrayOutputStream
import java.io.OutputStream

class HttpResponse {
    var status: Int = 200
    var outputStream: OutputStream = ByteArrayOutputStream()
    val headers: MutableMap<String, String> = mutableMapOf()

    fun write(s: String) {
        outputStream.write(s.toByteArray())
    }

    fun writeln(s: String) {
        outputStream.write("$s\n".toByteArray())
    }

    fun addHeader(key: String, value: String) {
        headers[key] = value
    }
}
