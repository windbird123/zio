package com.github.windbird123.overview

import java.io.{File, FileNotFoundException}

import zio._
import zio.clock.Clock
object ErrorHandleRetry extends App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    // either or option
    val zEither: UIO[Either[String, Int]] = IO.fail("Failed").either

    // opposite of either
    val zAbsolve: IO[String, Int] = zEither.absolve


    trait DBError
    case object RecordNotFound extends DBError
    val lookupDB : IO[DBError, Option[String]] = ???

    // lookupDB 의 결과가 None 일 경우, 이를 Error 쪽에 포함되도록 한다.
    val dbSome: ZIO[Any, Option[DBError], String] = lookupDB.some
    val dbSomeError: ZIO[Any, DBError, String] = dbSome.mapError {
      case Some(x) => x
      case None => RecordNotFound
    }

    ///////////////////////////////////////////////////////////////////////
    // tapError 와 orElse 를 적극적으로 사용하자. 끝판왕은 foldM
    // error recover 를 위해서는 catchSome, catchAll 사용
    ///////////////////////////////////////////////////////////////////////
    IO.fail("error").tapError(msg => console.putStrLn(msg)) // msg 를 출력하고 결과는 에러 그대로 보내준다.

    // catchAll
    val catchAllTest: Task[File] = Task
      .effect(new File("first.txt"))
      .catchAll(_ => Task.effect(new File("second.txt")))

    // catchSome
    val catchSomeTest: Task[String] =
      Task.fail(new Exception("fail")).catchSome {
        case _: IndexOutOfBoundsException => IO.effect("fail")
      }

    // fallback
    val fallbackTask: UIO[Int] = Task.effect(1 / 0).orElse(UIO.succeed(3))

    // fold, foldM
    Task.fail(throw new Exception("")).fold(_ => 0, x => x)

    // retry
    val retriedOpenFile: ZIO[clock.Clock, Throwable, Array[Byte]] = {
      val task: Task[Array[Byte]] = Task.fail(new FileNotFoundException("not"))
      task.retry(Schedule.recurs(3))
    }

    // retryOrElse, retryOrElseEither
    retriedOpenFile.retryOrElse(
      Schedule.recurs(5), // retry policy 에 관해서는 datatype 의 Schedule 을 더 볼 것
      (_: Throwable, _: Long) => Task.succeed(Array.empty[Byte])
    )

    UIO(ExitCode.success)
  }
}
