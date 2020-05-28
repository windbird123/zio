package com.github.windbird123.ziotest

import zio.console._
import zio.duration._
import zio.test.Assertion._
import zio.test.TestAspect._
import zio.test._
import zio.test.environment.TestConsole
import zio.{ZIO, console}

// https://github.com/zio/zio-intellij/issues/29 이슈
// java8 을 쓸려면 zio 1.0.0-RC18 버전을 써야 한다
object HelloWorld {
  def sayHello: ZIO[Console, Nothing, Unit] = console.putStrLn("Hello, World!")
}

object HelloWorldSpec extends DefaultRunnableSpec {
  override def spec: ZSpec[zio.test.environment.TestEnvironment, Any] =
    suite("all tests")(sayHelloSuite, propertySuite, aspectSuite)

  val sayHelloSuite
    : Spec[TestConsole with Console, TestFailure[Nothing], TestSuccess] =
    suite("HelloWorldSpec")(testM("sayHello display output") {
      for {
        _ <- HelloWorld.sayHello
        output <- TestConsole.output
      } yield assert(output)(equalTo(Vector("Hello, World!\n")))
    })

  val propertySuite =
    suite("Gen test")(testM("property test using Gen") {
      check(Gen.anyInt, Gen.anyInt, Gen.anyInt) { (x, y, z) =>
        assert((x + y) + z)(equalTo(x + (y + z)))
      }
    }) @@ around(zio.console.putStrLn("BEFORE"))(
      _ => zio.console.putStrLn("AFTER")
    )

  val aspectSuite =
    suite("Aspect suite")(
      test("passing test") {
        assert(true)(isTrue)
      } @@ timeout(5.seconds),
      test("ignore test") {
        assert(true)(isTrue)
      } @@ ignore
    ) @@ before(zio.console.putStrLn("starts test =========")) @@ after(
      zio.console.putStrLn("finish test =====")
    )

  ////////////////////////////////////////////////////////////////////////////////////
  val clockSuite = suite("clock suite") (
    testM("time is non-zero") {
      assertM(zio.clock.nanoTime)(isGreaterThan(0L))
    }
  )

  val assertionForString = Assertion.containsString("Foo") && Assertion.endsWithString("Bar")


  import zio.test.Assertion.{isRight, isSome,equalTo, hasField}
  test("Check assertions") {
    assert(Right(Some(2)))(isRight(isSome(equalTo(2))))
  }

}
