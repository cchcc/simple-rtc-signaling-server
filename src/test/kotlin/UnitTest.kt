import cchcc.SignalMessage
import cchcc.toJson
import org.junit.Assert
import org.junit.Test

class UnitTest {
    @Test
    fun test_SignalMessage() {

        Assert.assertEquals("""{"fn":"room","o":{"name":"test"}}"""
            ,SignalMessage.room("test").toJson().toString())

    }
}