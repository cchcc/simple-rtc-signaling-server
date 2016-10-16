# Simple RTC signaling server
간단한 WebRTC 시그널 서버  
## setting
별도로 운영하는 ICE 서버가 있으면 `G.kt`파일을 아래처럼 미리 수정.  
```kotlin
    val ICE = listOf(ICEServer.create("stun:192.168.0.10:3478", "myid", "mypw")
                ,ICEServer.create("turn:192.168.0.10:3478", "myid", "mypw"))
```  
없으면 <https://github.com/coturn/coturn> 이걸로 돌리면 잘됨.  

## build
```sh
./gradlew fatJar
```  
## run
```sh
java -jar simple-rtc-signaling-server-fat-runnable-0.1.jar
```
