package com.github.windbird123.zlayer

import zio._

object ZLayerInterface {
  trait Service {
    def init(v: Int): UIO[Int]
    def process(box: String): UIO[String]
  }

  def init(v: Int): ZIO[Has[Service], Nothing, Int]            = ZIO.accessM(_.get[Service].init(v))
  def process(box: String): ZIO[Has[Service], Nothing, String] = ZIO.accessM(_.get[Service].process(box))

  val face: Layer[Nothing, Has[Service]] = ZLayer.succeed(new Service {
    override def init(v: Int): UIO[Int]            = UIO(1)
    override def process(box: String): UIO[String] = UIO("FACE")
  })

  val ocr: Layer[Nothing, Has[Service]] = ZLayer.succeed(new Service {
    override def init(v: Int): UIO[Int]            = UIO(2)
    override def process(box: String): UIO[String] = UIO("OCR")
  })
}

object MyProg extends zio.App {
  def stepLogic(v: Int, box: String): ZIO[Has[ZLayerInterface.Service], Nothing, String] =
    for {
      x <- ZLayerInterface.init(v)
      y <- ZLayerInterface.process(box)
    } yield s"$x: $y"

  def faceStep(v: Int, box: String): UIO[String] = stepLogic(v, box).provideLayer(ZLayerInterface.face)
  def ocrStep(v: Int, box: String): UIO[String]  = stepLogic(v, box).provideLayer(ZLayerInterface.ocr)

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    val prog = for {
      face <- faceStep(1, "faceBox")
      ocr  <- ocrStep(2, "ocrBox")
    } yield s"$face -> $ocr"

    prog.flatMap(x => console.putStrLn(x)).exitCode
  }
}
