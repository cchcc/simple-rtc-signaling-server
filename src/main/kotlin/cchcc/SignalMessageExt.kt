package cchcc

import io.vertx.core.json.DecodeException
import io.vertx.core.json.JsonObject

fun SignalMessage.toJson() = JsonObject(toMap())

fun String.toSignalMessage(): SignalMessage? = try {
    SignalMessage.fromMap(JsonObject(this).map)
} catch (e: DecodeException) {
    null
}
