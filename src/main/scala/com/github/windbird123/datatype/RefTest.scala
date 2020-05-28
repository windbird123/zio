package com.github.windbird123.datatype

import com.typesafe.scalalogging.LazyLogging
import zio._
import zio.console.Console

object RefTest extends zio.App with LazyLogging {
  // get/set ref
  val myProg: ZIO[Any, Nothing, Unit] = for {
    ref <- Ref.make(100)
    v1  <- ref.get
    v2  <- ref.set(v1 - 50)
  } yield v2

  // update a ref
  val myProg2: ZIO[Any, Nothing, UIO[Unit]] = Ref.make(0).map(ref => ref.update(_ + 1))

  // state transformer (마치 state monad 같은)
  // RefExternalTest 형태로 사용하는 것이 더 좋아 보임
  val myProg3: ZIO[Console, Nothing, Unit] = {
    val stateTransition  = (state: Int) => (s"result: $state", state + 3)
    val stateTransition2 = (state: Int) => (s"result: $state", state * 2)
    Ref.make(1).flatMap { r =>
      def freshVar: UIO[String]  = r.modify(stateTransition) // S => (A, S)
      def freshVar2: UIO[String] = r.modify(stateTransition2)

      for {
        v1 <- freshVar
        _  <- console.putStrLn(s"v1=$v1") // v1=result: 1
        v2 <- freshVar
        _  <- console.putStrLn(s"v2=$v2") // v2=result: 4
        v3 <- freshVar2
        _  <- console.putStrLn(s"v3=$v3") // v3=result: 7
        v4 <- freshVar2
        _  <- console.putStrLn(s"v4=$v4") // v4=result: 14
      } yield ()
    }
  }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = myProg3.exitCode
}

object RefExternalTest extends zio.App with LazyLogging {
  def f1(ref: Ref[Int]): ZIO[Any, Nothing, String] = ref.modify((state: Int) => (s"result: $state", state + 3))
  def f2(ref: Ref[Int]): ZIO[Any, Nothing, String] = ref.modify((state: Int) => (s"result: $state", state * 2))

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    val program: ZIO[Console, Nothing, Unit] = for {
      ref <- Ref.make(1)

      v1 <- f1(ref)
      _  <- console.putStrLn(s"v1=$v1") // v1=result: 1
      v2 <- f1(ref)
      _  <- console.putStrLn(s"v2=$v2") // v2=result: 4
      v3 <- f2(ref)
      _  <- console.putStrLn(s"v3=$v3") // v3=result: 7
      v4 <- f2(ref)
      _  <- console.putStrLn(s"v4=$v4") // v4=result: 14
    } yield ()

    program.exitCode
  }
}
