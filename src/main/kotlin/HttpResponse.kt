import java.io.ByteArrayOutputStream
import java.io.OutputStream

class HttpResponse {
    var status: Int = 200
    var outputStream: OutputStream = ByteArrayOutputStream()
    var headers: Map<String, String> = mutableMapOf()

    fun write(s: String) {
        outputStream.write(s.toByteArray())
    }
}
