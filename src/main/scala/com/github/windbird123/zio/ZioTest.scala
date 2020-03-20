package com.github.windbird123.zio

import zio.{App, ZIO}

object ZioTest extends App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    ZIO.succeed(0)
  }
}
