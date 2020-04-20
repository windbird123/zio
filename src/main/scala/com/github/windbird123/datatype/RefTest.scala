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
  val myProg3: ZIO[Console, Nothing, Unit] = {
    val stateTransition = (state: Int) => (s"result: $state", state + 3)
    val stateTransition2 = (state: Int) => (s"result: $state", state * 2)
    for {
      r        <- Ref.make(1) // initial value
      freshVar = r.modify(stateTransition)  // S => (A, S)

      v1 <- freshVar
      _  <- console.putStrLn(s"v1=$v1") // v1=result: 1
      v2 <- freshVar
      _  <- console.putStrLn(s"v2=$v2") // v2=result: 4
      v3 <- r.modify(stateTransition2)
      _  <- console.putStrLn(s"v3=$v3") // v3=result: 7
      v4 <- r.modify(stateTransition2)
      _  <- console.putStrLn(s"v4=$v4") // v4=result: 14
    } yield ()
  }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = myProg3.as[Int](0)
}
