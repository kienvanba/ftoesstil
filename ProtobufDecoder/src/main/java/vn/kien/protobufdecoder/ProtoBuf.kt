package vn.kien.protobufdecoder

import java.lang.StringBuilder

object ProtoBuf {
    private const val VARINT = 0
    private const val BIT64 = 1
    private const val LENGTH_DELIMITED = 2

    /**
     * Check if a byte is a string character
     * @param hex: byte as hex string
     * @return whether this byte is a character
     */
    fun isCharacter(hex: String): Boolean {
        return (hex.toInt(16) in 32 .. 126)
    }

    /**
     * Convert a hex string to binary string
     * @param hex: byte as hex string
     * @return binary string with length of 8 (ex: 0000 0001 instead of 1, or 0000 1000 instead of 1000)
     */
    fun hexToBin(hex: String): String {
        val bStr = hex.toInt(16).toString(2)
        val missingB = 8 - bStr.length
        if (missingB > 0) {
            val a = "0".repeat(missingB)
            return a+bStr
        }
        return bStr
    }

    /**
     * drop the most significant bit from bits
     * @param hex: byte as hex string (this will be convert to binary string)
     * @return binary string without the msb
     */
    fun dropMSB(hex: String): String {
        val bStr = hexToBin(hex)
        return bStr.substring(1)
    }

    /**
     * Change the hex to binary then get the most significant bit (msb) to see if there are further bytes to come
     * if msb == 0 then this is the last byte
     *
     * @param hex - the hex string
     * @return whether this is the last byte
     */
    fun shouldIncludeNextHex(hex: String): Boolean {
        val bStr = hexToBin(hex)
        return (bStr[0] != '0')
    }

    /**
     * Get wire type from byte, the last three bits of the number store the wire type.
     * @see (https://developers.google.com/protocol-buffers/docs/encoding#structure)
     * @param hex - the hex string
     * @return wire type (0-5)
     */
    fun getWireType(hex: String): Int {
        val bStr = dropMSB(hex)
        val wStr = bStr.substring(bStr.length - 3)
        return wStr.toInt(2)
    }

    /**
     * Get the field number by right shift by three bits
     * @param hex - the hex string
     * @return the field bumber
     */
    fun getFieldNumber(hex: String): Int {
        val bStr = dropMSB(hex)
        return (bStr.toInt(2) shr 3)
    }

    /**
     * decode varint type from an array of hex by reversing the group and concatenating all binary string (had drop msb)
     *
     * TODO: classify varint types (int32, int64, uint32, uint64, sint32, sint64, bool, enum) to decode
     *
     * @param listHex - array of hex string
     * @return decoded value
     */
    fun decodeVarint(listHex: List<String>): Int {
        val binString = StringBuilder()
        for (i in listHex.indices.reversed()) {
            binString.append(dropMSB(listHex[i]))
        }
        return binString.toString().toInt(2)
    }

    /**
     * decode 64bit from hex
     */
    fun decode64Bit(hex: String): Double {
        // TODO: implementation
        return 0.0
    }

    /**
     * decode length-delimited type from hex string
     *
     * TODO: classify length-delimited types (string, bytes, embedded messages, packed repeated fields) to decode
     *
     * @param hex - hex string to decode
     * @return decoded value
     */
    fun decodeLengthDelimited(hex: String): Any {
        val list = mutableListOf<String>()
        val listChar = mutableListOf<Char>()
        for (i in 0 until hex.length - 1 step 2) {
            val byte = hex.substring(i, i+2)
            if (isCharacter(byte)) {
                listChar.add(byte.toInt(16).toChar())
            }
            list.add(byte)
        }
        return if (listChar.size == list.size) { // return string if decoded value is string
            listChar.toCharArray().concatToString()
        } else {
            // call decode in case embedded messages
            decode(hex)
            // TODO: handle other case such as bytes / packed repeated fields
        }
    }

    /**
     * Decode a hex string
     * @param hex - the hex string
     * @return map contains pair of [field number, decoded value]
     */
    fun decode(hex: String): Map<Int, Any> {
        var i = 0
        val data = mutableMapOf<Int, Any>()
        while (i < hex.length - 1) {
            val byte = hex.substring(i, i+2)
            val wireType = getWireType(byte)
            val fieldNumber = getFieldNumber(byte)
            if (data[fieldNumber] != null) {
                throw Throwable("Duplicate Field Number (${fieldNumber})")
            }
            when (wireType) {
                VARINT -> {
                    val hexStr = hex.substring(i+2)
                    var j = 0
                    val list = mutableListOf<String>()
                    while (j < hex.length - 1) {
                        val b = hexStr.substring(j, j+2)
                        list.add(b)
                        if (shouldIncludeNextHex(b)) {
                            j += 2
                        } else {
                            break
                        }
                    }
                    data[fieldNumber] = decodeVarint(list)
                    i = (i+2) + (j+2)
                }
                BIT64 -> { data[fieldNumber] = "64Bit type is decode is not implemented yet ${decode64Bit(hex)}" }
                LENGTH_DELIMITED -> {
                    val length = hex.substring(i+2, i+4).toInt(16)
                    val hexStr = hex.substring(i+4, i+4+length*2)
                    data[fieldNumber] = decodeLengthDelimited(hexStr)
                    i = (i+4) + (length*2)
                }
                else -> { println("unknown byte"); i += 2 }
            }
        }
        return data
    }
}

fun main() {
    println(ProtoBuf.decode("0a0d08f92712024f4b188a8c06204e120774657374696e67"))
    println(ProtoBuf.decode("089601"))
    println(ProtoBuf.decode("120774657374696e6777"))
    println(ProtoBuf.decode("08ac02"))
}