package com.github.windbird123.datatype

import com.typesafe.scalalogging.LazyLogging
import zio._
import zio.duration._
object ScheduleTest extends zio.App with LazyLogging {

  val task = UIO.effectTotal {
    logger.info("TASK ===")
  }

  val taskFail: IO[String, Int] = IO.fail {
//    Thread.sleep(1000L)
    logger.error("fail")
    "fail"
  }

  val forever     = Schedule.forever
  val never       = Schedule.never
  val upTo10      = Schedule.recurs(10)
  val spaced      = Schedule.spaced(10.seconds)
  val exponential = Schedule.exponential(1.second)

  // schedule combinator
  val jitteredExponential = exponential.jittered
  val boosted             = Schedule.spaced(1.second).delayed(_ + 2.second) // 1초 space 를 2초를 더해 3초로 수정
  val sequential          = Schedule.recurs(3) andThen Schedule.spaced(1.second) // 최초 3번은 space 없이, 이후에는 1초씩 space 하면서
  val expUpTo4            = Schedule.exponential(1.second) && Schedule.recurs(4) // 두개의 조건을 모두 만족하는 타이밍에 ==> exponential 로 4번까지
  val expCapped           = Schedule.exponential(1.second) || Schedule.spaced(2.second) // 두개의 조건중 하나라도 만족하는 타이밍이면 ..

  // Stops retrying after a specified amount of time has elapsed
  val expMaxElapsed = (Schedule.spaced(2.second) >>> Schedule.elapsed).whileOutput(_ < 10.seconds)

  // Retry only when a specific exception occurs
  import scala.concurrent.TimeoutException

  // 이걸 많이 쓰게 될 것 같다.
  val whileTimeout = Schedule.spaced(2.seconds) && Schedule.recurs(3) && Schedule.doWhile[Exception] {
    case _: TimeoutException => true
    case _                   => false
  }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    val startTask = UIO.effectTotal(logger.info("START ============"))
    startTask *> taskFail.retry(expUpTo4).exitCode
  }
}
