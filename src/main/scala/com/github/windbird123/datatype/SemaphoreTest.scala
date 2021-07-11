package com.github.windbird123.datatype

import zio._
import zio.duration._

import java.util.concurrent.TimeUnit

object SemaphoreTest extends zio.App {
  val task = for {
    _ <- console.putStrLn("start")
    _ <- ZIO.sleep(Duration(2, TimeUnit.SECONDS))
    _ <- console.putStrLn("end")
  } yield ()

  val semTask = (sem: Semaphore) =>
    for {
      _ <- sem.withPermit(task) // withPermits(1)(task)
    } yield ()

  val semTaskSeq = (sem: Semaphore) => (1 to 3).map(_ => semTask(sem))

  val program = for {
    sem <- Semaphore.make(1)
    seq <- UIO(semTaskSeq(sem))
    _   <- ZIO.collectAllPar(seq)
  } yield ()

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = program.exitCode
}
