package com.github.windbird123

import zio._

object RepeatWhile extends zio.App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val effect = for {
      r <- random.nextDouble
      _ <- console.putStrLn(s"Called: $r")
    } yield r

    effect.repeatWhile(_ < 0.9).exitCode
  }
}
