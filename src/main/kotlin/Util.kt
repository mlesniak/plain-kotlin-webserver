object Util {
    private val log: Log = Log.of(Util::class.java)

    fun ignoreException(function: () -> Unit) {
        try {
            function()
        } catch (e: Exception) {
            log.error("Unhandled exception ${e.message}")
        }
    }
}