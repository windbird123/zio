package com.github.windbird123.zlayer

import zio._

object DB {
  trait Service {
    def execute(sql: String): Task[Unit]
  }

  def execute(sql: String): ZIO[Has[Service], Throwable, Unit] = ZIO.accessM(_.get[Service].execute(sql))

  val live: ZLayer[Has[ConnectionPool], Throwable, Has[Service]] = ZLayer.fromService {
    (connectionPool: ConnectionPool) =>
      new Service {
        override def execute(sql: String): Task[Unit] = Task.effect {
          println(s"running: $sql on connection ${connectionPool.name}")
        }
      }
  }

  val live2: ZLayer[Has[ConnectionPool], Throwable, Has[Service]] = ZIO
    .access[Has[ConnectionPool]] { env =>
      lazy val connectionPool = env.get[ConnectionPool]
      new Service {
        override def execute(sql: String): Task[Unit] = Task.effect {
          println(s"running: $sql on connection ${connectionPool.name}")
        }
      }
    }
    .toLayer
}
