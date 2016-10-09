import cchcc.ext.toJsonString
import cchcc.ext.toSignalMessage
import cchcc.model.ICEServer
import cchcc.model.SignalMessage
import io.vertx.rxjava.core.buffer.Buffer
import org.junit.Assert
import org.junit.Test

class SignalMessageTest {

    @Test
    fun parse() {

        val jsonString = """{"type":"room","name":"abc"}"""

//        Assert.assertEquals(SignalMessage.room("abc")
//                ,Buffer.buffer(jsonString).toSignalMessage())

        // 위에 처럼해야 맞는건데
        // 각 클래스 마다 equals() 구현하는게 귀찮아서 그냥 json string 으로 동일한지를 비교함.
        // 조만간 sealed class 도 data sealed class 도 되도록 나오겠지...

        Assert.assertEquals(SignalMessage.room("abc").toJsonString()
                            , Buffer.buffer(jsonString).toSignalMessage()?.toJsonString())

        val s = """{"type":"startAsCaller","ice":[{"uri":"stun:192.168.0.10:3478","username":"myid","password":"mypw"},{"uri":"turn:192.168.0.10:3478","username":"myid","password":"mypw"}]}"""
        Assert.assertEquals(
        SignalMessage.startAsCaller(listOf(ICEServer.create("stun:192.168.0.10:3478", "myid", "mypw")
                , ICEServer.create("turn:192.168.0.10:3478", "myid", "mypw")))
                .toJsonString(),s)

        val msg = s.toSignalMessage()
        println(msg?.toJsonString())

    }
}