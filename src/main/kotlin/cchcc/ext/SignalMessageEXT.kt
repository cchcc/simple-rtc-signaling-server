package cchcc.ext

import cchcc.model.SignalMessage
import io.vertx.core.json.DecodeException
import io.vertx.core.json.Json
import io.vertx.rxjava.core.buffer.Buffer


fun SignalMessage.toJsonString() = Json.encode(this)!!

fun Buffer.toSignalMessage(): SignalMessage? = try {
    SignalMessage.from(toJsonObject().map)
} catch (e: DecodeException) {
    null
}