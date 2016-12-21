import base.latch
import cchcc.MainVerticle
import cchcc.ext.toJsonString
import cchcc.ext.toSignalMessage
import cchcc.model.SignalMessage
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import io.vertx.rxjava.core.Vertx
import okhttp3.*
import okhttp3.ws.WebSocket
import okhttp3.ws.WebSocketCall
import okhttp3.ws.WebSocketListener
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(VertxUnitRunner::class)
class WebSocketConnectionTest {

    private val url = "ws://127.0.0.1:12345"
    private val okHttpClient = OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build()

    companion object {
        @BeforeClass @JvmStatic
        fun beforeClass(context: TestContext) {
            Vertx.vertx().deployVerticle(MainVerticle::class.java.name, context.asyncAssertSuccess())
        }

        @AfterClass @JvmStatic
        fun afterClass(context: TestContext) {
            Vertx.vertx().close(context.asyncAssertSuccess())
        }
    }

    @Test
    fun connectWebSocketClient() {
        latch(5000) {

            WebSocketCall.create(okHttpClient, Request.Builder().url(url).build())
                    .enqueue(object : WebSocketListener {
                        var callerWs: WebSocket? = null
                        override fun onOpen(webSocket: WebSocket, response: Response) {
                            println("onOpen : $webSocket : $response")
                            callerWs = webSocket
                            webSocket.sendMessage(RequestBody.create(WebSocket.TEXT, SignalMessage.room("abc").toJsonString()))
                        }

                        override fun onPong(payload: okio.Buffer?) {
                        }

                        override fun onClose(code: Int, reason: String?) {
                            println("onClosed : $code : $reason")
                        }

                        override fun onFailure(e: IOException?, response: Response?) {
                            println("onFailure : $e : $response")
                            Assert.fail(e?.message)
                        }

                        override fun onMessage(resBody: ResponseBody?) {
                            val msgString = resBody?.string() ?: ""
                            println("onMessage : $msgString")

                            val msg = msgString.toSignalMessage() as SignalMessage.roomCreated
                            Assert.assertEquals("abc", msg.name)
                            callerWs?.close(1000, "")
                            countDown()
                        }
                    })

        }
    }

    @Test
    fun chat() {
        latch(5000) {

            WebSocketCall.create(okHttpClient, Request.Builder().url(url).build())
                    .enqueue(object : WebSocketListener {
                        var callerWs: WebSocket? = null
                        override fun onOpen(webSocket: WebSocket, response: Response) {
                            println("1onOpen : $webSocket : $response")
                            callerWs = webSocket
                            webSocket.sendMessage(RequestBody.create(WebSocket.TEXT, SignalMessage.room("abc2").toJsonString()))
                        }

                        override fun onPong(payload: okio.Buffer?) {
                        }

                        override fun onClose(code: Int, reason: String?) {
                            println("1onClosed : $code : $reason")
                        }

                        override fun onFailure(e: IOException?, response: Response?) {
                            println("1onFailure : $e : $response")
                            Assert.fail(e?.message)
                        }

                        override fun onMessage(resBody: ResponseBody?) {
                            val msgString = resBody?.string() ?: ""
                            println("1onMessage : $msgString")

                            val msg = msgString.toSignalMessage()
                            when (msg) {
                                is SignalMessage.startAsCaller -> {
                                    callerWs!!.sendMessage(
                                            RequestBody.create(WebSocket.TEXT, SignalMessage.chat("hi").toJsonString())
                                    )
                                    callerWs!!.close(1000, "")
                                }
                            }
                        }
                    })

            Thread.sleep(500)

            WebSocketCall.create(okHttpClient, Request.Builder().url(url).build())
                    .enqueue(object : WebSocketListener {
                        var callerWs: WebSocket? = null
                        override fun onOpen(webSocket: WebSocket, response: Response) {
                            println("2onOpen : $webSocket : $response")
                            callerWs = webSocket
                            webSocket.sendMessage(RequestBody.create(WebSocket.TEXT, SignalMessage.room("abc2").toJsonString()))
                        }

                        override fun onPong(payload: okio.Buffer?) {
                        }

                        override fun onClose(code: Int, reason: String?) {
                            println("2onClosed : $code : $reason")
                        }

                        override fun onFailure(e: IOException?, response: Response?) {
                            println("2onFailure : $e : $response")
                            Assert.fail(e?.message)
                        }

                        override fun onMessage(resBody: ResponseBody?) {
                            val msgString = resBody?.string() ?: ""
                            println("2onMessage : $msgString")

                            val msg = msgString.toSignalMessage()
                            when (msg) {
                                is SignalMessage.chat -> {
                                    Assert.assertEquals(msg.message, "hi")
                                    callerWs!!.close(1000, "")
                                    countDown()
                                }
                            }
                        }
                    })

        }
    }
}