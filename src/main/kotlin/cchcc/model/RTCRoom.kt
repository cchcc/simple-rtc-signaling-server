package cchcc.model

class RTCRoom(val name: String, peerCaller: RTCPeer) {

    init {
        peerCaller.room = this
    }

    var caller: RTCPeer? = peerCaller
        set(value) {
            field?.room = null
            value?.room = this
            field = value
        }

    var callee: RTCPeer? = null
        set(value) {
            field?.room = null
            value?.room = this
            field = value
        }

    fun opponentsOf(peer: RTCPeer): RTCPeer? = when(peer) {
        caller -> callee
        callee -> caller
        else -> null
    }

    fun remove(peer: RTCPeer): Unit = when(peer) {
        caller -> {
            caller?.room = null
            caller = null
        }
        callee -> {
            callee?.room = null
            callee = null
        }
        else -> Unit
    }

}