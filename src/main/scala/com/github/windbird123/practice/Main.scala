package com.github.windbird123.practice

import zio._

object Main extends App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = myProg.exitCode

  val config: Config                 = Config("naver", 123)
  val cuveConnection: CuveConnection = CuveConnection("connect url")
  val initialBox: Box                = Box("blog", "http://...")

  val readAndWrite: ZIO[Has[WriteModule.Service] with Has[ReadModule.Service], StepError, Unit] =
    for {
      content <- ReadModule.read(initialBox)
      _       <- WriteModule.write(content)
    } yield ()

  val horizontalLayer
    : ZLayer[Has[CuveConnection] with Has[Config], Nothing, Has[WriteModule.Service] with Has[ReadModule.Service]] =
    WriteModule.live ++ ReadModule.live

  val bottomLayer: ZLayer[Any, Nothing, Has[Config] with Has[CuveConnection]] =
    ZLayer.succeed(cuveConnection) ++ ZLayer.succeed(config)

  val fullLayer: ZLayer[Any, Nothing, Has[WriteModule.Service] with Has[ReadModule.Service]] =
    bottomLayer >>> horizontalLayer

  val myProg: URIO[Any, Int] = readAndWrite.provideLayer(fullLayer).fold(_ => 1, _ => 0)
}
