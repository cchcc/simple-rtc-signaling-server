package base

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

inline fun <R> latch(count: Int = 1, block: CountDownLatch.() -> R): R {
    val latch = CountDownLatch(count)
    val r = latch.block()
    latch.await()
    return r
}

inline fun <R> latch(count: Int = 1, timeoutMilliSec:Long, block: CountDownLatch.() -> R): R {
    val latch = CountDownLatch(count)
    val r = latch.block()
    latch.await(timeoutMilliSec, TimeUnit.MILLISECONDS)
    return r
}