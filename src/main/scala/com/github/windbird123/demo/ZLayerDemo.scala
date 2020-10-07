package com.github.windbird123.demo

import java.io.{File, PrintWriter}
import java.nio.charset.StandardCharsets

import zio._

import scala.io.{BufferedSource, Source}

case class Config(inputPath: String, outputPath: String)

object Demo extends zio.App {

  val inputLayer: ZLayer[Has[Config], Throwable, Has[BufferedSource]] = ZLayer.fromServiceManaged { (config: Config) =>
    Task.effect {
      Source.fromFile(new File(config.inputPath), StandardCharsets.UTF_8.name())
    }.toManaged(source => UIO(source.close()))
  }

  val outputLayer: ZLayer[Has[Config], Throwable, Has[PrintWriter]] = ZLayer.fromServiceManaged { (config: Config) =>
    Task.effect {
      new PrintWriter(new File(config.outputPath), StandardCharsets.UTF_8.name())
    }.toManaged(writer => UIO(writer.close()))
  }

  val logic: ZIO[Has[BufferedSource] with Has[PrintWriter], Throwable, Unit] =
    ZIO.accessM[Has[BufferedSource] with Has[PrintWriter]] { env =>
      val source = env.get[BufferedSource]
      val writer = env.get[PrintWriter]

      Task.effect {
        source.getLines().foreach { line =>
          val inc = line.trim.toInt + 1
          writer.write(inc + "\n")
        }
      }
    }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val configLayer = ZLayer.succeed(Config("d:/now/input.txt", "d:/now/output.txt"))

    val fullLayer: ZLayer[Any, Throwable, Has[BufferedSource] with Has[PrintWriter]] =
      configLayer >>> (inputLayer ++ outputLayer)
    val program = logic.provideCustomLayer(fullLayer)
    program.exitCode
  }
}
