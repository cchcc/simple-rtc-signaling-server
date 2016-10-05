package cchcc

import cchcc.model.ICEServer

object G {

    /**
     *  [
     *   {"uri":"stun:192.168.0.10:3478","username":"myid","password":"mypw"}
     *  ,{"uri":"turn:192.168.0.10:3478","username":"myid","password":"mypw"}
     *  ]
     */

//    val ICE = listOf(ICEServer("stun:192.168.0.10:3478", "myid", "mypw")
//                    ,ICEServer("turn:192.168.0.10:3478", "myid", "mypw"))

    val ICE = listOf<ICEServer>()
}