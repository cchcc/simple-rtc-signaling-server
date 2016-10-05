package cchcc.model

import cchcc.ext.toJsonString
import io.vertx.rxjava.core.http.ServerWebSocket
import rx.Subscription

class RTCPeer(val webSocket: ServerWebSocket) {

    var isClosed: Boolean = false
    var room: RTCRoom? = null
    var webSocketSubscription: Subscription? = null

    fun send(msg: SignalMessage) = webSocket.writeFinalTextFrame(msg.toJsonString())

    fun closeWebSocket() {
        if (!isClosed) {
            webSocket.close()
            isClosed = true
        }
    }
}