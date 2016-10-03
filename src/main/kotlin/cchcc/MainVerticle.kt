package cchcc

import cchcc.ext.toSignalMessage
import cchcc.model.RTCPeer
import cchcc.model.RTCRoom
import cchcc.model.SignalMessage
import io.vertx.core.Future
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.core.http.HttpServer
import io.vertx.rxjava.core.http.ServerWebSocket

class MainVerticle() : AbstractVerticle() {

    private val logger: Logger by lazy { LoggerFactory.getLogger(javaClass) }
    private val httpServer: HttpServer by lazy { vertx.createHttpServer() }
    private val rooms = mutableMapOf<String, RTCRoom>()

    override fun start(startFuture: Future<Void>?) {
        super.start(startFuture)

        httpServer.websocketHandler { connected(it) }
                .listenObservable(12345)
                .subscribe({
                    logger.info("listening WebSocket port : ${it.actualPort()}")
                }
                        , {
                    logger.error("failed to listening : ${it.message}")
                })

        logger.info("${javaClass.name} deployed")
    }

    override fun stop(stopFuture: Future<Void>?) {
        httpServer.close()
        super.stop(stopFuture)
    }

    private fun connected(webSocket: ServerWebSocket) = RTCPeer(webSocket).let {
        peer ->
        val peerAddress = webSocket.remoteAddress().let { "${it.host()}:${it.port()}" }
        logger.info("$peerAddress is connected")

        val closePeerWithLog = { log: String ->
            peer.room?.let { room ->
                room.opponentsOf(peer)?.let { oppPeer ->
                    oppPeer.closeWebSocket()
                    room.remove(oppPeer)
                }
                room.remove(peer)
                rooms.remove(room.name)
            }
            peer.websocketSubscription?.unsubscribe()
            peer.closeWebSocket()
            logger.info(log)
        }

        peer.websocketSubscription = webSocket.toObservable().subscribe(received@ {
            logger.info("$peerAddress received : $it")

            val msg = it.toSignalMessage()
            when (msg) {
                is SignalMessage.room -> {
                    peer.room?.let {
                        closePeerWithLog("$peerAddress is closed by duplicated room ${it.name}")
                        return@received
                    }
                    if (rooms.containsKey(msg.name)) {
                        rooms[msg.name]!!.let {
                            it.callee = peer
                            peer.send(SignalMessage.roomJoined(msg.name))
                            // start RTC
                            it.caller?.send(SignalMessage.startAsCaller(G.ICE))
                            it.callee?.send(SignalMessage.startAsCallee(G.ICE))
                        }

                    } else {
                        rooms.put(msg.name, RTCRoom(msg.name, peer))
                        peer.send(SignalMessage.roomCreated(msg.name))
                    }
                }
                else -> closePeerWithLog("$peerAddress is closed by not specified message")
            }
        }, error@ {
            closePeerWithLog("$peerAddress is closed by error : ${it.message}")
        }, closed@ {
            peer.isClosed = true
            closePeerWithLog("$peerAddress is closed")
        })

    }
}