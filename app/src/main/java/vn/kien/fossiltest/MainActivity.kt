package vn.kien.fossiltest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import vn.kien.logger.Bnaz
import vn.kien.logger.BnazConfig
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Bnaz.setup(BnazConfig())
        Bnaz.tag("aaa").i("bbbbbbb")
        Bnaz.i(File("a/b/c/d.txt"))
        Bnaz.i(arrayOf(1,2,3,4))
        Bnaz.e(Throwable("con con con"))
        Bnaz.tag("abcde")
        Bnaz.setup(BnazConfig(showThreadId = false))
        Thread {
            Bnaz.datePattern("hh:mm").i("ddddddd")
            Bnaz.tag("hh:mm").i("ddddddd")
        }.start()
    }
}

data class TestModel(val n: Int, val s: String)