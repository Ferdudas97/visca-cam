package multimedia.visca.protocol


enum class ViscaResponseMsg(vararg byte: Byte) {
    LENGTH_ERROR(0x60, 0x1),
    SYNTAX_ERROR(0x60, 0x2),
    BUFFER_FULL(0x60, 0x3),
    CANCELED(0x60, 0x4),
    NO_SOCKETS(0x60, 0x5),
    NOT_EXECUTABLE(0x60, 0x41),
    ACK,
    COMPLETION,
    UNKNOWN;

    val bytes: ByteArray = byte

    companion object {
        fun fromBytes(bytes: ByteArray): ViscaResponseMsg {
            val byteMessage = bytes.copyOfRange(1, bytes.lastIndex)
            return values().find { it.bytes.contentEquals(byteMessage) }
                ?: orFromDirectCamera(byteMessage)
        }

        private fun orFromDirectCamera(bytes: ByteArray) = when (bytes.first()) {
            in 0x40..0x4F -> ACK
            in 0x50..0x5F -> COMPLETION
            else -> UNKNOWN
        }

    }

}

sealed class Response {
    data class DirectResponse(val from: Byte, val msg: ViscaResponseMsg) : Response()
    data class NotDirectResponse(val msg: ViscaResponseMsg) : Response()
}