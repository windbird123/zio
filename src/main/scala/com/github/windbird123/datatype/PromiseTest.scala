package com.github.windbird123.datatype

import zio._

// Promise 의 활용 예로 생각해 볼 수 있는 것은
// http request 를 Promise 로 받아 queue 에 저장해 두고
// background thread 에서 각각의 Promise 를 처리 ..
object PromiseTest extends zio.App {
  val basicRace: ZIO[Any, String, Int] = for {
    p     <- Promise.make[String, Int]
    _     <- p.succeed(7).fork  // p.succeed 의 리턴 타입은 Promise 값 설정의 성공 여부를 나타내는 UIO[Boolean] 이다.
    _     <- p.fail("failed").fork
    _     <- p.done(Exit.succeed(3)).fork
    value <- p.await
  } yield value


  val completeRace: ZIO[Any, String, Int] = for {
    p     <- Promise.make[String, Int]
    _     <- p.complete(IO.succeed { println("TEST"); 3 }).fork // await 가 몇개든 IO.succeed 는 1번만 실행 됨.
    _     <- p.await
    value <- p.await
  } yield value

  val completeWithRace: ZIO[Any, String, Int] = for {
    p     <- Promise.make[String, Int]
    _     <- p.completeWith(IO.succeed { println("TEST"); 3 }).fork // await fiber 에서 await 개수만큼 IO.succeed 가 실행됨
    _     <- p.await
    value <- p.await
  } yield value

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val program = for {
      value <- completeWithRace
      _     <- console.putStr(value.toString)
    } yield ()

    program.tapError(s => UIO(println(s))).fold(_ => 1, _ => 0)
  }
}
