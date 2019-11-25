package multimedia.visca.protocol

interface Pan {
    val p: Byte
}

interface Tilt {
    val t: Byte
}

sealed class CommandMsg(vararg bytes: Byte) {
    val byteArray = bytes

    class RIGHT(override val p: Byte, override val t: Byte) : CommandMsg(0x1, 0x6, 0x1, p, t, 0x2, 0x3), Pan,
        Tilt

    class LEFT(override val p: Byte, override val t: Byte) : CommandMsg(0x1, 0x6, 0x1, p, t, 0x1, 0x3), Pan,
        Tilt

    class UP(override val p: Byte, override val t: Byte) : CommandMsg(0x1, 0x6, 1, p, t, 0x3, 0x1),
        Pan, Tilt

    class DOWN(override val p: Byte, override val t: Byte) : CommandMsg(0x1, 0x6, 0x1, p, t, 0x3, 0x2),
        Pan, Tilt

    class ZOOM_WIDE(override val p: Byte) : CommandMsg(0x1, 0x4, 0x7, "3$p".toByte(radix = 16)),
        Pan

    class ZOOM_TELE(override val p: Byte) : CommandMsg(0x1, 0x4, 0x7, "2$p".toByte(radix = 16)),
        Pan

    class POWER_ON : CommandMsg(0x1, 0x4, 0x33, 0x2)
    class POWER_OFF : CommandMsg(0x1, 0x4, 0x33, 0x3)
    class CANCEL(socket: Byte) : CommandMsg("2$socket".toByte(16))
    class CLEAR : CommandMsg(0x1, 0x0, 0x1)
    class HOME : CommandMsg(0x1, 0x6, 0x4)
    class GET_MAX_SPEED : CommandMsg(0x9, 0x6, 17)
    class ADDRESS : CommandMsg(0x30, 0x1)
    class ADDRESS_SET(override val p: Byte) : CommandMsg(0x30, "0$p".toByte()), Pan
}

