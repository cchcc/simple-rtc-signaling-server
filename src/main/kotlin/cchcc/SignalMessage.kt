package cchcc

sealed class SignalMessage(val fn: String, val o: Map<String, Any?>) {

    fun toMap() = mapOf("fn" to fn, "o" to o)

    operator fun component1() = fn
    operator fun component2() = o

    override fun toString(): String = toMap().toString()

    companion object {
        fun fromMap(map: Map<String, Any?>): SignalMessage? = try {
            val fn = map["fn"] as String
            @Suppress("UNCHECKED_CAST")
            val o = map["o"] as Map<String, Any?>

            when (fn) {
                "room" -> SignalMessage.room(o["name"] as String)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    class room(name: String) : SignalMessage("room", mapOf("name" to name))
}