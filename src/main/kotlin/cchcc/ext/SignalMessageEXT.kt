package cchcc.ext

import cchcc.model.SignalMessage
import io.vertx.core.json.DecodeException
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.buffer.Buffer


fun SignalMessage.toJsonString(): String = Json.encode(this)

fun String.toSignalMessage(): SignalMessage? = try {
    SignalMessage.from(JsonObject(this).map)
} catch (e: DecodeException) {
    null
}

fun Buffer.toSignalMessage(): SignalMessage? = try {
    SignalMessage.from(toJsonObject().map)
} catch (e: DecodeException) {
    null
}