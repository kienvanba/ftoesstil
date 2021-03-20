package vn.kien.logger

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

    @RunWith(RobolectricTestRunner::class)
    @Config(manifest = Config.NONE)
class BnazTest {

    @Before
    fun setup() {
        Bnaz.setup(
            BnazConfig(
            traceEnabled = true,
                dateTimePattern = "yyyy-MM-dd",
                showThreadId = true,
                logEnabled = true
        ))
    }

    @Test
    fun logFile() {
        val file = File("a/b/c/d/test.txt")
        val log = Bnaz.generateLogMsg(file)
        assertEquals("[${Thread.currentThread().id}] File:\n- path: a/b/c/d/test.txt\n- exist: false\nat 2021-03-18\nat vn.kien.logger.BnazTest.logFile(BnazTest.kt:31)", log)
    }

    @Test
    fun logThrowable() {
        val t = Throwable("test")
        Bnaz.setup(BnazConfig(false, null, showThreadId = false, logEnabled = false))
        val log = Bnaz.generateLogMsg(t)
        assertEquals(t.stackTraceToString(), log)
    }

    @Test
    fun logDatePattern() {
        Bnaz.setup(BnazConfig(false, null, showThreadId = false, logEnabled = false))
        val log = Bnaz.datePattern("HH:mm").generateLogMsg("test pattern")
        assertEquals("test pattern\nat ${SimpleDateFormat("HH:mm").format(Date())}", log)
    }

    @Test
    fun logArray() {
        Bnaz.setup(BnazConfig(false, null, showThreadId = false, logEnabled = false))
        val log = Bnaz.generateLogMsg(arrayOf(1,2,3,4,5))
        assertEquals("[1, 2, 3, 4, 5]", log)
    }
}