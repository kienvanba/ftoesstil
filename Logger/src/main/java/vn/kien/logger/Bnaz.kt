package vn.kien.logger

import android.os.Build
import android.util.Log
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object Bnaz {
    private const val MAX_TAG_LENGTH = 23
    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    private val forceTag = ThreadLocal<String>()
    private val forceDateFormat = ThreadLocal<String>()
    private var config: BnazConfig? = null
    private val ignoreClasses = arrayOf(Bnaz::class.java.name, BnazConfig::class.java.name)

    private val tag: String
        get() {
            val tag = forceTag.get()
            forceTag.remove()
            return tag ?: Throwable().stackTrace
                .first { it.className !in ignoreClasses }
                .let(::createStackElementTag)
        }

    private val dateFormat: DateFormat?
        get() {
            val pattern = forceDateFormat.get()
            forceDateFormat.remove()
            return (pattern ?: config!!.dateTimePattern).let {
                it ?: return@let null
                return@let SimpleDateFormat(it, Locale.getDefault())
            }
        }

    /**
     * Extract the tag which should be used for the message from the `element`. By default
     * this will use the class name without any anonymous class suffixes (e.g., `Foo$1`
     * becomes `Foo`).
     *
     * Note: This will not be called if a [manual tag][.tag] was specified.
     */
    private fun createStackElementTag(element: StackTraceElement): String {
        var tag = element.className.substringAfterLast('.')
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        // Tag length limit was removed in API 24.
        return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tag
        } else {
            tag.substring(0, MAX_TAG_LENGTH)
        }
    }

    fun generateLogMsg(obj: Any?) : String {
        if (config == null) {
            Log.wtf("FATAL ERROR", "Bnaz is not setup with config")
            return "invalidate Bnaz config"
        }
        val body = when (obj) {
            is File -> generateLogMsg(obj)
            is Array<*> -> obj.contentToString()
            is Throwable -> obj.stackTraceToString()
            else -> obj.toString()
        }

        return StringBuilder().apply {
            if (config!!.showThreadId) {
                append("[${Thread.currentThread().id}] ")
            }
            append(body)
            dateFormat?.also { append("\nat ${it.format(Date())}") }
            if (config!!.traceEnabled) {
                val msg = Throwable().stackTrace.first { it.className !in ignoreClasses }.toString()
                append("\nat $msg")
            }
        }.toString()
    }

    private fun generateLogMsg(file: File) : String {
        return StringBuilder("File:\n").apply {
            this.append("- path: ${file.path}\n")
            this.append("- exist: ${file.exists()}")
        }.toString()
    }

    fun setup(config: BnazConfig) {
        if (this.config != null) Log.w("WARNING", "Bnaz config overridden!")
        this.config = config
    }

    fun tag(tag: String): Bnaz {
        forceTag.set(tag)
        return this
    }

    fun datePattern(pattern: String): Bnaz {
        forceDateFormat.set(pattern)
        return this
    }

    fun i(obj: Any?) {
        Log.i(tag, generateLogMsg(obj))
    }

    fun e(obj: Any?) {
        Log.e(tag, generateLogMsg(obj))
    }

    fun d(obj: Any?) {
        Log.d(tag, generateLogMsg(obj))
    }

    fun w(obj: Any?) {
        Log.w(tag, generateLogMsg(obj))
    }

    fun wtf(obj: Any?) {
        Log.wtf(tag, generateLogMsg(obj))
    }
}