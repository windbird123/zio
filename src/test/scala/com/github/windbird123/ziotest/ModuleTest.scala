package com.github.windbird123.ziotest

import zio.test.Assertion._
import zio.test.{DefaultRunnableSpec, ZSpec, _}
import zio.{Has, IO, ZIO, ZLayer, console}

object Roy {
  trait Service {
    def getCuve(id: Int): IO[String, Double]
  }

  def getCuve(id: Int): ZIO[Has[Roy.Service], String, Double] =
    ZIO.accessM(_.get[Roy.Service].getCuve(id))

  val live: ZLayer[Any, Nothing, Has[Roy.Service]] =
    ZLayer.succeed(new Service {
      override def getCuve(id: Int): IO[String, Double] = {
        IO.succeed(id + 1.0)
      }
    })
}

object ServiceLogicBetweenModules {
  val roy = for {
    r <- Roy.getCuve(3)
    _ <- console.putStrLn(s"RESULT: $r")
  } yield r
}

object AppMain extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val layer = Roy.live ++ console.Console.live
    ServiceLogicBetweenModules.roy.provideLayer(layer).fold(_ => 1, _ => 0)
  }
}

// integration test 와 같은.. ServiceLogicBetweenModules.roy 를 테스트 해 보자(unit test 이상)
object ModuleTestSpec extends DefaultRunnableSpec {
  override def spec: ZSpec[zio.test.environment.TestEnvironment, Any] =
    suite("all tests")(roySuite)

  val roySuite = suite("module test")(testM("module ") {
    // 필요에 따라 Roy.live 대신에 test 용을 구현해 대체할 수 있다.
    val layer = Roy.live ++ console.Console.live
    for {
      output <- ServiceLogicBetweenModules.roy.provideLayer(layer)
    } yield assert(output)(equalTo(4.0))
  })
}
