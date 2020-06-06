package com.github.windbird123.practice.ratelimiter

import zio._

class BlockingRateLimiter(perSecond: Int, buffer: Int) {
  private val runtime: Runtime[zio.ZEnv] = Runtime.default
  private val rateLimiter: RateLimiter   = runtime.unsafeRun(RateLimiter.make(perSecond, buffer))

  def rateLimit[T](body: => T): T =
    runtime.unsafeRun(rateLimiter.rateLimit(blocking.effectBlocking(body)))
}

object BlockingExample {
  def main(args: Array[String]): Unit = {
    val rateLimiter = new BlockingRateLimiter(perSecond = 1, buffer = 2)

    (1 to 10).foreach(i =>
      rateLimiter.rateLimit {
        println(i)
      }
    )
  }
}
