package multimedia.visca

data class Msg(
    val source: Byte = 0,
    val dest: Byte = 0,
    val content: String,
    val p: Byte? = null,
    val t: Byte? = null,
    val delay: Int? = null
)
data class Macro(val name: String, val messages: List<Msg> = emptyList())

