import cchcc.MainVerticle
import cchcc.ext.toJsonString
import cchcc.ext.toSignalMessage
import cchcc.model.SignalMessage
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import io.vertx.rxjava.core.Vertx
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.ByteString
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(VertxUnitRunner::class)
class WebSocketConnectionTest {

    private val url = "ws://127.0.0.1:12345"
    private val okHttpClient = OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build()

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass(context: TestContext) {
            Vertx.vertx().deployVerticle(MainVerticle::class.java.name, context.asyncAssertSuccess())
        }

        @AfterClass
        @JvmStatic
        fun afterClass(context: TestContext) {
            Vertx.vertx().close(context.asyncAssertSuccess())
        }
    }

    @Test
    fun createRoom() {
        latch(1, 5000) {

            okHttpClient.newWebSocket(Request.Builder().url(url).build(), object : okhttp3.WebSocketListener() {
                override fun onOpen(webSocket: okhttp3.WebSocket, response: Response) {
                    println("onOpen : $webSocket : $response")
                    webSocket.send(SignalMessage.room("abc").toJsonString())
                }

                override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
                    println("onMessage 0x1 text:$text")
                    val msg = text.toSignalMessage() as SignalMessage.roomCreated
                    Assert.assertEquals("abc", msg.name)
                    webSocket.close(1000, "")
                    countDown()
                }

                override fun onMessage(webSocket: okhttp3.WebSocket, bytes: ByteString) {
                    println("onMessage 0x2 bytes:$bytes")
                }

                override fun onClosing(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
                    println("onClosing code:$code, reason:$reason")
                    webSocket.close(1000, null)
                }

                override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
                    println("onClosed code:$code, reason:$reason")
                }

                override fun onFailure(webSocket: okhttp3.WebSocket, t: Throwable, response: Response?) {
                    println("onFailure t:$t, response:$response")
                    t.printStackTrace()
                }
            }
            )
        }
    }

    @Test
    fun chat() {
        latch(1, 5000) {
            okHttpClient.newWebSocket(Request.Builder().url(url).build(), object : okhttp3.WebSocketListener() {
                override fun onOpen(webSocket: okhttp3.WebSocket, response: Response) {
                    println("1onOpen : $webSocket : $response")
                    webSocket.send(SignalMessage.room("abc2").toJsonString())
                }

                override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
                    println("1onMessage 0x1 text:$text")
                    val msg = text.toSignalMessage()
                    when (msg) {
                        is SignalMessage.startAsCaller -> {
                            webSocket.send(SignalMessage.chat("hi").toJsonString())
                            webSocket.close(1000, "")
                        }
                    }
                }

                override fun onMessage(webSocket: okhttp3.WebSocket, bytes: ByteString) {
                    println("1onMessage 0x2 bytes:$bytes")
                }

                override fun onClosing(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
                    println("1onClosing code:$code, reason:$reason")
                    webSocket.close(1000, null)
                }

                override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
                    println("1onClosed code:$code, reason:$reason")
                }

                override fun onFailure(webSocket: okhttp3.WebSocket, t: Throwable, response: Response?) {
                    println("1onFailure t:$t, response:$response")
                    t.printStackTrace()
                }
            }
            )

            Thread.sleep(1000)

            okHttpClient.newWebSocket(Request.Builder().url(url).build(), object : okhttp3.WebSocketListener() {
                override fun onOpen(webSocket: okhttp3.WebSocket, response: Response) {
                    println("2onOpen : $webSocket : $response")
                    webSocket.send(SignalMessage.room("abc2").toJsonString())
                }

                override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
                    println("2onMessage 0x1 text:$text")
                    val msg = text.toSignalMessage()
                    when (msg) {
                        is SignalMessage.chat -> {
                            Assert.assertEquals(msg.message, "hi")
                            webSocket.close(1000, "")
                            countDown()
                        }
                    }
                }

                override fun onMessage(webSocket: okhttp3.WebSocket, bytes: ByteString) {
                    println("2onMessage 0x2 bytes:$bytes")
                }

                override fun onClosing(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
                    println("2onClosing code:$code, reason:$reason")
                    webSocket.close(1000, null)
                }

                override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
                    println("2onClosed code:$code, reason:$reason")
                }

                override fun onFailure(webSocket: okhttp3.WebSocket, t: Throwable, response: Response?) {
                    println("2onFailure t:$t, response:$response")
                    t.printStackTrace()
                }
            }
            )
        }
    }
}