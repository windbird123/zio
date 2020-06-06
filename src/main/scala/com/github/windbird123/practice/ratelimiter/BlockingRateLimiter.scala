package com.github.windbird123.practice.ratelimiter

import zio._

class BlockingRateLimiter(perSecond: Double, buffer: Int) {
  private val rateLimiter: RateLimiter = Runtime.default.unsafeRun(RateLimiter.make(perSecond, buffer))

  def rateLimit[T](body: => T): T =
    Runtime.default.unsafeRun(rateLimiter.rateLimit(blocking.effectBlocking(body)))
}

object BlockingExample {
  def main(args: Array[String]): Unit = {
    val rateLimiter = new BlockingRateLimiter(perSecond = 1.0, buffer = 2)

    (1 to 10).foreach(i =>
      rateLimiter.rateLimit {
        println(i)
      }
    )
  }
}
