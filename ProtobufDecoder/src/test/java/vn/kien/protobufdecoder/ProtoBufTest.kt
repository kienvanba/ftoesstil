package vn.kien.protobufdecoder

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ProtoBufTest {

    @Test
    fun isCharacter_true() {
        assertEquals(true, ProtoBuf.isCharacter("74"))
    }

    @Test
    fun isCharacter_false() {
        assertEquals(false, ProtoBuf.isCharacter("1"))
    }

    @Test
    fun hexToBin() {
        assertEquals("00001000", ProtoBuf.hexToBin("08"))
    }

    @Test
    fun dropMSB() {
        assertEquals("0001000", ProtoBuf.dropMSB("08"))
    }

    @Test
    fun shouldIncludeNextHex() {
        assertEquals(false, ProtoBuf.shouldIncludeNextHex("08"))
    }

    @Test
    fun getWireType() {
        assertEquals(2, ProtoBuf.getWireType("12"))
    }

    fun getFieldNumber() {
        assertEquals(1, ProtoBuf.getFieldNumber("08"))
    }

    fun decodeVarint() {
        assertEquals(150, ProtoBuf.decodeVarint(listOf("96", "01")))
    }

    fun decodeLengthDelimited() {
        assertEquals("testing", ProtoBuf.decodeLengthDelimited("0774657374696e67"))
    }

    fun decodeHex() {
        val decodedResult = ProtoBuf.decode("0a0d08f92712024f4b188a8c06204e120774657374696e67")
        val expectedResult = mapOf(
            Pair(1, mapOf(Pair(1, 5113), Pair(2, "OK"), Pair(3, 99850), Pair(4, 78))),
            Pair(2, "testing")
        )
        assertEquals(expectedResult, decodedResult)
    }
}