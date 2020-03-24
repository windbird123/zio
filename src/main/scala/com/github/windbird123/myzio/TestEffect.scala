package com.github.windbird123.myzio

import zio._

final case class Config(server: String, port: Int)

trait DatabaseOps {
  def getTableNames: Task[List[String]]
  def getColumnNames(table: String): Task[List[String]]
}

object TestEffect {
  val config: Config = Config("abc", 123)

  def main(args: Array[String]): Unit = {
    // simple case
    val eff = for {
      server <- ZIO.access[Config](_.server)
      port <- ZIO.access[Config](_.port)
    } yield s"server: $server, port: $port"

    val ready = eff.provide(config)
    Runtime.default.unsafeRunSync(ready)

    // config 를 가져오기 위해 계산이 필요할 경우 (environmental effects)
    val tablesAndColumns
      : ZIO[DatabaseOps, Throwable, (List[String], List[String])] = for {
      tables <- ZIO.accessM[DatabaseOps](_.getTableNames)
      columns <- ZIO.accessM[DatabaseOps](_.getColumnNames("user_table"))
    } yield (tables, columns)
  }
}
