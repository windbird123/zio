package com.github.windbird123.logging

import com.typesafe.scalalogging.{CanLog, Logger, LoggerTakingImplicit}
import org.slf4j.MDC
import zio._

// MDC Logging 보다는 간단한 경우 MarkerLogging 을 쓰는게 좋을 것 같다.
case class MDCKey(value: String)

object CanLogMarkerId extends CanLog[MDCKey] {
  override def logMessage(originalMsg: String, mdcKey: MDCKey): String = {
    MDC.put("requestId", mdcKey.value)
    originalMsg
  }

  override def afterLog(a: MDCKey): Unit =
    MDC.remove("requestId")
}

object MDCLogging extends zio.App {
  implicit val markerLogging: CanLog[MDCKey] = CanLogMarkerId
  val logger: LoggerTakingImplicit[MDCKey]   = Logger.takingImplicit[MDCKey]("MyLogger")

  def program()(implicit mdcKey: MDCKey): UIO[Int] = UIO.effectTotal {
    logger.info("TEST")
    3
  }

  def program2()(implicit markerId: MDCKey): UIO[Int] = UIO.effectTotal {
    logger.info("TEST")
    3
  }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    implicit val mdcKey: MDCKey = MDCKey("123")

    for {
      fiber <- program().fork
      y     <- program2()
      x     <- fiber.join
    } yield y
  }
}
