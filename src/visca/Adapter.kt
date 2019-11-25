package multimedia.visca

import jssc.SerialPort
import mu.KotlinLogging
import multimedia.visca.protocol.ViscaResponseMsg
import pl.edu.agh.kis.visca.ViscaResponseReader


interface ViscaAdapter {
    fun sendMessage(message: ByteArray)
    fun readResponse(): ByteArray
}

class ViscaAdapterImpl(portName: String) : ViscaAdapter {
    private val serialPort: SerialPort = SerialPort(portName)

    init {
        serialPort.apply {
            openPort()
            setParams(9600, 8, 1, 0)
        }
    }


    override fun sendMessage(message: ByteArray) {
        serialPort.writeBytes(message)
    }

    override fun readResponse(): ByteArray {
        return ViscaResponseReader.readResponse(serialPort)
    }
}

class DumbViscaAdapterImpl(portName: String) : ViscaAdapter {
    private val logger= KotlinLogging.logger {  }
    override fun sendMessage(message: ByteArray) {
        logger.info { message.toReadableString() }
    }

    override fun readResponse(): ByteArray {

        return ViscaResponseMsg.ACK.bytes.toMutableList().apply {
            add(0,0xF0.toByte())
            add(0xFF.toByte())
        }.toByteArray()
    }
}