package multimedia

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.jackson.jackson
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import multimedia.visca.*
import org.slf4j.event.Level
import java.time.Duration

fun main(args: Array<String>): Unit {
    val cmd = false
    val viscaService = ViscaService(viscaAdapter = DumbViscaAdapterImpl("COM5")).apply {
        start()
    }
    if (!cmd) {
        embeddedServer(Netty, port = 8080) {
            module(viscaService)
        }.start(true)
    } else {
        Cmd(viscaService).start()
    }
}

private fun menu() = commands.keys


@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(viscaService: ViscaService) {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }

    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        header(HttpHeaders.AccessControlAllowHeaders)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        anyHost()
        maxAge = Duration.ofDays(1)
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response

    }
    install(ContentNegotiation) {
        jackson {
            registerModule(KotlinModule())
            configure(SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            })

            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    routing {
        post("/commands") {

            val msgs = call.receive<List<LinkedHashMap<String, Any>>>().map { it.getMsg() }
            msgs.forEach { viscaService.executeMsg(it) }
            call.respond(HttpStatusCode.OK)
        }

        get {
            call.respond(commands.keys)
        }
        get("/responses") {
            call.respond(viscaService.responses)
        }

    }
}

private fun LinkedHashMap<String, Any>.getMsg() =
    Msg(
        source = getByte("source") ?: 0,
        content = get("content") as String,
        dest = getByte("dest") ?: 1,
        p = getByte("p"),
        t = getByte("t")
    )

private fun LinkedHashMap<String, Any>.getByte(key: String) = get(key)?.let { it as Int }?.toByte()


