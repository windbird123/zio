package com.github.windbird123.ratelimiter

import zio._

import scala.concurrent.{Await, Future}
class FutureRateLimiter(perSecond: Int, buffer: Int) {
  private val runtime = zio.Runtime.default

  val rateLimiter: RateLimiter = {
    val limiter: RateLimiter = runtime.unsafeRun(RateLimiter.make(perSecond, buffer))
    runtime.unsafeRunToFuture(limiter.feedTokenPeriodically())
    limiter
  }

  def rateLimit[T](effect: () => Future[T]): Future[T] =
    runtime.unsafeRunToFuture(rateLimiter.rateLimit(ZIO.fromFuture(_ => effect())))
}

object FutureExample extends scala.App {
  import scala.concurrent.ExecutionContext.Implicits.global

  def putStrLineFuture(str: String) = Future(println(str))

  val rateLimiter = new FutureRateLimiter(perSecond = 1, buffer = 2)
  val future = Future.sequence(
    (1 to 10).map(i => rateLimiter.rateLimit(() => putStrLineFuture(i.toString)))
  )

  Await.result(future, scala.concurrent.duration.Duration.Inf)
}
