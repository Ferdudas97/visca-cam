package multimedia.visca

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import mu.KotlinLogging
import multimedia.visca.protocol.ViscaResponseMsg
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.seconds
import kotlin.time.toDuration

class ViscaService(private val viscaAdapter: ViscaAdapter) {
    private val destCmdMap: Map<Byte, MutableList<Msg>> = (1..7).associate { it.toByte() to mutableListOf<Msg>() }
    val responses = ConcurrentLinkedQueue<ViscaResponseMsg>()
    val rawResponses = ConcurrentLinkedQueue<String>()
    val macros: MutableMap<String, Macro> = mutableMapOf()
    val logger = KotlinLogging.logger { }
    fun executeMsg(msg: Msg) {
        destCmdMap.getValue(msg.dest).add(msg)
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

    fun oneThreadStart() {
        GlobalScope.launch {
            while (true) {
                destCmdMap.entries.filter { it.value.isNotEmpty() }
                    .forEach {
                        sendToSpecifiedCamera(it.key).apply {
                        }
                    }

            }
        }
    }

    private suspend fun sendToSpecifiedCamera(dest: Byte) {
        val msg = destCmdMap[dest]!!.first()
        val f = commands.getValue(msg.content)
        f(viscaAdapter, msg)
        val byteResponse = viscaAdapter.readResponse()
        destCmdMap.getValue(dest).remove(msg)
        rawResponses.add(byteResponse.toReadableString())
        val response = ViscaResponseMsg.fromBytes(byteResponse)
        responses.add(response)
        logger.info { response.name }
        msg.delay?.let { delay(it * 1000L) }

    }

    fun executeMacro(name: String) {
        macros.get(name)?.messages?.forEach(this::executeMsg)
    }

    fun waitUntilCompletion(block: () -> ViscaResponseMsg) {
        var msg: ViscaResponseMsg? = null
        while (msg == null || !(ViscaResponseMsg.ACK == msg || ViscaResponseMsg.COMPLETION == (msg))) {
            msg = block()
        }
    }
}