package com.github.windbird123.pitfall

import zio._

object MyRef {
  val ref: UIO[Ref[Int]] = Ref.make(0)

  def modify(): UIO[Int] =
    for {
      r   <- ref
      out <- r.modify(x => (x + 1, x + 1))
    } yield out
}

object EffectRT extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    val program = for {
      o1 <- MyRef.modify()
      _  <- console.putStrLn(s"o1: $o1") // o1: 1

      o2 <- MyRef.modify()
      _  <- console.putStrLn(s"o2: $o2") // o2: 1 (2가 아니라 1이 출력)
    } yield ()

    program.exitCode
  }
}
