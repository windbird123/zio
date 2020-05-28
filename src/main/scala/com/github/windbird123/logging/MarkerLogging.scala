package com.github.windbird123.logging

import com.typesafe.scalalogging.LazyLogging
import org.slf4j.{Marker, MarkerFactory}
import zio._

object MarkerLogging extends zio.App with LazyLogging {
  def program()(implicit marker: Marker): UIO[Int] = UIO.effectTotal {
    logger.info(marker, "TEST 123")
    3
  }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    implicit val marker: Marker = MarkerFactory.getMarker("12345")
    program().exitCode
  }
}
