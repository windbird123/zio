package com.github.windbird123.zlayer.map

import zio._

class BaseAdder(base: Int) {
  def inc(n: Int): Int = base + n
}

object ZLayerMap extends zio.App {
  val logic: URIO[Has[BaseAdder], Int] = ZIO.access[Has[BaseAdder]](_.get[BaseAdder].inc(5))

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val calculator: ZLayer[Has[String], Nothing, Has[BaseAdder]] = ZLayer.fromService[String, BaseAdder] { str =>
      val base = str.toInt
      new BaseAdder(base)
    }

    val stringLayer: ULayer[Has[String]] = ZLayer.succeed("3")

    val fullLayer: ZLayer[Any, Nothing, Has[BaseAdder]] = stringLayer >>> calculator

    val prog = for {
      out <- logic.provideCustomLayer(fullLayer)
      _   <- console.putStrLn(s"OUT: $out")
    } yield ()
    prog.exitCode
  }
}
