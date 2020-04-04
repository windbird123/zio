package com.github.windbird123.overview

import com.typesafe.scalalogging.LazyLogging
import zio.console.Console
import zio.{Has, ZIO, ZLayer, console}

object ZLayerSimple extends zio.App with LazyLogging {
  val myProg: ZIO[Console with Has[Int], Nothing, Int] = for {
    i <- ZIO.access[Has[Int]](_.get[Int])
    _ <- console.putStrLn(s"i=$i")
  } yield i

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val intLayer = ZLayer.succeed(3)
    myProg.provideSomeLayer[Console](intLayer)
  }
}
