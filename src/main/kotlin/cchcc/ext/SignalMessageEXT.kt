package cchcc.ext

import cchcc.model.SignalMessage
import io.vertx.rxjava.core.buffer.Buffer
import io.vertx.core.json.DecodeException
import io.vertx.core.json.JsonObject

fun SignalMessage.toJson() = JsonObject(toMap())

fun String.toSignalMessage(): SignalMessage? = try {
    SignalMessage.fromMap(JsonObject(this).map)
} catch (e: DecodeException) {
    null
}

fun Buffer.toSignalMessage(): SignalMessage? = try {
    SignalMessage.fromMap(toJsonObject().map)
} catch (e: DecodeException) {
    null
}