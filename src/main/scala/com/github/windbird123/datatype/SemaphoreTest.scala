package com.github.windbird123.datatype

import java.util.concurrent.TimeUnit

import zio._
import zio.clock.Clock
import zio.console.Console
import zio.duration._

import scala.collection.immutable

object SemaphoreTest extends zio.App {
  val task: ZIO[Console with Clock, Nothing, Unit] = for {
    _ <- console.putStrLn("start")
    _ <- ZIO.sleep(Duration(2, TimeUnit.SECONDS))
    _ <- console.putStrLn("end")
  } yield ()

  val semTask: Semaphore => ZIO[Console with Clock, Nothing, Unit] = (sem: Semaphore) =>
    for {
      _ <- sem.withPermit(task) // withPermits(1)(task)
    } yield ()

  val semTaskSeq: Semaphore => immutable.IndexedSeq[ZIO[Console with Clock, Nothing, Unit]] = (sem: Semaphore) =>
    (1 to 3).map(_ => semTask(sem))

  val program: ZIO[Console with Clock, Nothing, Unit] = for {
    sem <- Semaphore.make(1)
    seq <- UIO(semTaskSeq(sem))
    _   <- ZIO.collectAllPar(seq)
  } yield ()

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = program.exitCode
}
