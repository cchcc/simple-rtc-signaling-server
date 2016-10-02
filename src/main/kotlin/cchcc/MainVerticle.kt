package cchcc

import io.vertx.core.Future
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.core.http.HttpServer
import io.vertx.rxjava.core.http.ServerWebSocket

class MainVerticle() : AbstractVerticle() {

    private val logger: Logger by lazy { LoggerFactory.getLogger(javaClass) }
    private val httpServer: HttpServer by lazy { vertx.createHttpServer() }

    override fun start(startFuture: Future<Void>?) {
        super.start(startFuture)

        httpServer.websocketHandler { connectedWebsocket(it) }
             .listenObservable(12345)
             .subscribe({
                 logger.info("listening websocket port : ${it.actualPort()}")
             }, {
                 logger.error("failed listening : ${it.message}")
             })

        logger.info("${javaClass.name} deployed")
    }

    override fun stop(stopFuture: Future<Void>?) {
        httpServer.close()
        super.stop(stopFuture)
    }

    private fun connectedWebsocket(webSocket: ServerWebSocket) = with(webSocket) {
        val remoteAddress = remoteAddress().host().toString()
        logger.info("$remoteAddress is connected")

        toObservable().subscribe({
                     logger.info("$remoteAddress received : $it")
                 }, {
                    logger.error("$remoteAddress error : ${it.message}")
                 }, {
                     logger.info("$remoteAddress is closed")
                 })

        vertx.timerStream(1000 * 10).toObservable()
            .subscribe {
                close()
                logger.info("$remoteAddress is closed by time over")
            }
    }

}