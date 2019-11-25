package multimedia.visca

import mu.KotlinLogging
import multimedia.visca.protocol.CommandMsg
import pl.edu.agh.kis.visca.ViscaResponseReader
import pl.edu.agh.kis.visca.cmd.*


var log = KotlinLogging.logger { }


fun ViscaAdapter.sendZoomTeleStd(msg: Msg) = sendCommand(CommandMsg.ZOOM_TELE(msg.p!!.toByte()), msg.source, msg.dest)


fun ViscaAdapter.sendZoomWideStd(msg: Msg) =
    sendCommand(CommandMsg.ZOOM_WIDE(msg.p!!), msg.source, msg.dest)

fun ViscaAdapter.sendPanTiltRight(msg: Msg) =
    sendCommand(CommandMsg.RIGHT(msg.p!!, msg.t!!), msg.source, msg.dest)

fun ViscaAdapter.sendPanTiltUp(msg: Msg) =
    sendCommand(CommandMsg.UP(msg.p!!, msg.t!!), msg.source, msg.dest)

fun ViscaAdapter.sendPanTiltLeft(msg: Msg) =
    sendCommand(CommandMsg.LEFT(msg.p!!, msg.t!!), msg.source, msg.dest)

fun ViscaAdapter.sendPanTiltDown(msg: Msg) =
    sendCommand(CommandMsg.DOWN(msg.p!!, msg.t!!), msg.source, msg.dest)

fun ViscaAdapter.sendPanTiltHome(msg: Msg) = sendCommand(CommandMsg.HOME(), msg.source, msg.dest)
//fun ViscaAdapter.sendPanTiltAbsolutePos(dest: Byte = 1, source: Byte = 0) = sendCommand(::PanTiltAbsolutePosCmd)
fun ViscaAdapter.getPanTiltMaxSpeed(msg: Msg) =
    sendCommand(CommandMsg.GET_MAX_SPEED(), msg.source, msg.dest)

fun ViscaAdapter.clearAllCmd(msg: Msg) = sendCommand(CommandMsg.CLEAR(), msg.source, 8)
fun ViscaAdapter.address(msg: Msg) = sendCommand(CommandMsg.ADDRESS(), msg.source, 8)
fun ViscaAdapter.setAddres(msg: Msg) = sendCommand(CommandMsg.ADDRESS_SET(msg.p!!),msg.source, msg.dest)
val commands = mapOf(
    "UP" to ViscaAdapter::sendPanTiltUp,
    "RIGHT" to ViscaAdapter::sendPanTiltRight,
    "LEFT" to ViscaAdapter::sendPanTiltLeft,
    "DOWN" to ViscaAdapter::sendPanTiltDown,
    "HOME" to ViscaAdapter::sendPanTiltHome,
    "ZOOM_TELE" to ViscaAdapter::sendZoomTeleStd,
    "ZOOM_WIDE" to ViscaAdapter::sendZoomWideStd,
//    "ABSOLUTE_POS" to ViscaAdapter::sendPanTiltAbsolutePos,
    "MAX_SPEED" to ViscaAdapter::getPanTiltMaxSpeed,
    "CLEAR" to ViscaAdapter::clearAllCmd,
    "ADDRESS" to ViscaAdapter::setAddres
)

fun ByteArray.toReadableString() = joinToString { String.format("%02X ", it) }

fun ViscaAdapter.sendCommand(cmdData: ByteArray, sourceAddress: Byte = 0, destination: Byte = 1) {

    log.info { "Command message ${cmdData.toReadableString()}" }
    val vCmd = ViscaCommand().apply {
        commandData = cmdData
        sourceAdr = sourceAddress
        destinationAdr = destination

    }.getCommandData()
    log.info { "Command ${vCmd.toReadableString()}" }

    sendMessage(vCmd)
}

private fun ViscaAdapter.sendCommand(cmd: CommandMsg, sourceAddress: Byte = 0, destination: Byte = 1) {
    sendCommand(cmd.byteArray, sourceAddress, destination)
}