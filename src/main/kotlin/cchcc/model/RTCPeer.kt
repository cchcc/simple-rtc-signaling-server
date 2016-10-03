package cchcc.model

import cchcc.ext.toJson
import io.vertx.rxjava.core.http.ServerWebSocket
import rx.Subscription

class RTCPeer(val webSocket: ServerWebSocket) {

    var isClosed: Boolean = false
    var room: RTCRoom? = null
    var websocketSubscription: Subscription? = null

    fun send(msg: SignalMessage) = webSocket.writeFinalTextFrame(msg.toJson().toString())

    fun closeWebSocket() {
        if (!isClosed) {
            webSocket.close()
            isClosed = true
        }
    }
}