package vn.kien.logger

data class BnazConfig(
    val traceEnabled: Boolean = true,
    val dateTimePattern: String? = "dd-MM-yyyy HH:mm:ss",
    val showThreadId: Boolean = true,
    val logEnabled: Boolean = true,
)