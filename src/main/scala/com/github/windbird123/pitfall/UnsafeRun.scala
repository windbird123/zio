package com.github.windbird123.pitfall

import zio._
import zio.duration._

// test 를 6번 출력
object ZioApp extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    val sample = console.putStrLn("test").repeat(Schedule.fixed(1.second)).fork *> ZIO.sleep(5.seconds)
    sample.exitCode
  }
}

// test 를 1번만 출력
object ScalaApp {
  def main(args: Array[String]): Unit = {
    val sample = console.putStrLn("test").repeat(Schedule.fixed(1.second)).fork
    Runtime.default.unsafeRun(sample)
    Thread.sleep(5000)
  }
}
