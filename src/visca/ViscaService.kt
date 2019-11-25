package multimedia.visca

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import multimedia.visca.protocol.ViscaResponseMsg
import java.util.concurrent.ConcurrentLinkedQueue

class ViscaService(private val viscaAdapter: ViscaAdapter) {
    private val destCmdMap: Map<Byte, MutableList<Msg>> = (1..7).associate { it.toByte() to mutableListOf<Msg>() }
    val responses = ConcurrentLinkedQueue<ViscaResponseMsg>()
    val rawResponses = ConcurrentLinkedQueue<String>()
    val logger = KotlinLogging.logger {  }
    fun executeMsg(msg: Msg) {
        destCmdMap.getValue(msg.dest.toByte()).add(msg)
    }

    fun start() {
//        destCmdMap.keys.forEach {
//            GlobalScope.launch {
//                while (true) {
//                    sendToSpecifiedCamera(it).apply {
//                        print(this)
//                        responses.add(this)
//                    }
//                }
//            }
//        }
        oneThreadStart()
    }
    fun oneThreadStart(){
        GlobalScope.launch {
            while (true) {
                destCmdMap.entries.filter { it.value.isNotEmpty()  }
                    .forEach {
                        sendToSpecifiedCamera(it.key).apply {
                            responses.add(this)
                            logger.info { this.name }
                        }
                    }

            }
        }
    }
    private fun sendToSpecifiedCamera(dest: Byte) : ViscaResponseMsg {
        val msg = destCmdMap[dest]!!.first()
        val f = commands.getValue(msg.content)
        f(viscaAdapter, msg)
        val byteResponse = viscaAdapter.readResponse()
        destCmdMap.getValue(dest).remove(msg)
        rawResponses.add(byteResponse.toReadableString())
        return ViscaResponseMsg.fromBytes(byteResponse)
    }


    fun waitUntilCompletion(block: () -> ViscaResponseMsg) {
        var msg: ViscaResponseMsg? = null
        while (msg == null || !(ViscaResponseMsg.ACK == msg || ViscaResponseMsg.COMPLETION == (msg))) {
            msg = block()
        }
    }
}