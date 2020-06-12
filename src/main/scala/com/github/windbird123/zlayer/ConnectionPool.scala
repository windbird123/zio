package com.github.windbird123.zlayer

import zio._

class ConnectionPool(val name: String, url: String) {
  def close(): Unit = ()
}

object ConnectionPool {
  def managedConnectionPool(dbConfig: DBConfig): ZManaged[Any, Throwable, ConnectionPool] =
    ZIO.effect(new ConnectionPool("my connection pool", dbConfig.url)).toManaged(x => UIO(x.close()))

  val live: ZLayer[Has[DBConfig], Throwable, Has[ConnectionPool]] =
    ZLayer.fromServiceManaged((dbConfig: DBConfig) => managedConnectionPool(dbConfig))
}
