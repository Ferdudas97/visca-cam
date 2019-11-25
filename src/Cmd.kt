package multimedia

import multimedia.visca.Msg
import multimedia.visca.ViscaService
import multimedia.visca.commands

fun main() {

}
class Cmd(private val viscaService: ViscaService) {


    fun start() {
        viscaService.start()
        while (true) {
            printMenu()
            val command = readCmd()
            val msg = Msg(source = 0, dest = 1, content = command, p = 1, t = 1)
            viscaService.executeMsg(msg)
        }
    }

    private fun menu() = commands.keys
    private fun readCmd() = readLine()!!
    private fun printMenu() = menu().mapIndexed { index, s -> "$index - $s" }
        .forEach(::println)
}