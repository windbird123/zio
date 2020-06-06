package com.github.windbird123.practice.ratelimiter

import zio._
import zio.clock.Clock
import zio.duration.{Duration, _}

object RateLimiter {
  private def periodFrom(perSecond: Double): Duration = (1.second.toNanos.toDouble / perSecond).toInt.nanos

  def make(perSecond: Double, buffer: Int): ZIO[Clock, Nothing, RateLimiter] = {
    require(perSecond > 0 && buffer > 0)

    val period: Duration = periodFrom(perSecond)
    for {
      queue <- Queue.bounded[Unit](buffer)
      _     <- queue.take.repeat(Schedule.fixed(period)).fork
    } yield {
      new RateLimiter(queue)
    }
  }
}

class RateLimiter(queue: Queue[Unit]) {
  def rateLimit[R, E, A](effect: => ZIO[R, E, A]): ZIO[R, E, A] = queue.offer(()) *> effect
}

object RateLimiterTest extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    val prog = for {
      limiter <- RateLimiter.make(perSecond = 1.0, buffer = 2)
      _       <- ZIO.foreach(1 to 10)(i => limiter.rateLimit(console.putStrLn(i.toString)))
    } yield ()

    prog.exitCode
  }
}
