package cchcc.model

sealed class SignalMessage(val fn: String) {

    open val o: Map<String, Any?> = emptyMap()

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
                "room" -> room(o["name"] as String)
                "roomCreated" -> roomCreated(o["name"] as String)
                "roomJoined" -> roomJoined(o["name"] as String)
                "startAsCaller" -> startAsCaller(o["ice"] as String)
                "startAsCallee" -> startAsCallee(o["ice"] as String)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    class room(val name: String) : SignalMessage("room") {
        override val o: Map<String, Any?> get() = mapOf("name" to name)
    }

    class roomCreated(val name: String) : SignalMessage("roomCreated") {
        override val o: Map<String, Any?> get() = mapOf("name" to name)
    }

    class roomJoined(val name: String) : SignalMessage("roomJoined") {
        override val o: Map<String, Any?> get() = mapOf("name" to name)
    }

    class startAsCaller(val ice: String) : SignalMessage("startAsCaller") {
        override val o: Map<String, Any?> get() = mapOf("ice" to ice)
    }

    class startAsCallee(val ice: String) : SignalMessage("startAsCallee") {
        override val o: Map<String, Any?> get() = mapOf("ice" to ice)
    }
}