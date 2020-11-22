package com.github.windbird123

import zio._
import zio.duration._

object RepeatWhile extends zio.App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val effect = for {
      r <- random.nextDouble
      _ <- console.putStrLn(s"Called: $r")
    } yield r

    effect.repeatWhileM { x =>
      if (x < 0.9) UIO(println("repeat ..")) *> UIO(true).delay(1.second)
      else UIO(false)
    }.exitCode
  }
}
