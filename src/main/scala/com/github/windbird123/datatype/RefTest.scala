package com.github.windbird123.datatype

import com.typesafe.scalalogging.LazyLogging
import zio._
import zio.console.Console

object RefTest extends zio.App with LazyLogging {
  // get/set ref
  val myProg = for {
    ref <- Ref.make(100)
    v1  <- ref.get
    v2  <- ref.set(v1 - 50)
  } yield v2

  // update a ref
  val myProg2: ZIO[Any, Nothing, UIO[Unit]] = Ref.make(0).map(ref => ref.update(_ + 1))

  // state transformer
  val myProg3: ZIO[Console, Nothing, Unit] = {
    val stateTransition = (state: Int) => (s"result: $state", state + 1)
    for {
      r        <- Ref.make(0) // initial value
      freshVar = r.modify(stateTransition)  // S => (A, S)

      v1 <- freshVar
      _  <- console.putStrLn(s"v1=$v1")
      v2 <- freshVar
      _  <- console.putStrLn(s"v2=$v2")
      v3 <- freshVar
      _  <- console.putStrLn(s"v3=$v3")
    } yield ()
  }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = myProg3.as[Int](0)
}
