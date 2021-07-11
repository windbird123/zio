package com.github.windbird123.zlayer2

import zio._
import zio.console.Console
import zio.magic._

import java.io.IOException

case class Item(url: String, domain: String)

// 이 경우는 typeclass 를 사용하는 것이 더 낫지 않을까?
trait Eq {
  def eqv(a: Item, b: Item): UIO[Boolean]
}

object Eq {
  def eqv(a: Item, b: Item): ZIO[Has[Eq], Nothing, Boolean] = ZIO.serviceWith[Eq](_.eqv(a, b))

  val byUrl: URLayer[Any, Has[Eq]]    = EqByUrl.toLayer[Eq]
  val byDomain: URLayer[Any, Has[Eq]] = EqByDomain.toLayer[Eq]
}

case class EqByUrl() extends Eq {
  override def eqv(a: Item, b: Item): UIO[Boolean] = UIO(a.url == b.url)
}

case class EqByDomain() extends Eq {
  override def eqv(a: Item, b: Item): UIO[Boolean] = UIO(a.domain == b.domain)
}

object Complex extends zio.App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val a = Item("a", "b")
    val b = Item("c", "b")

    val program: ZIO[Console, IOException, Unit] = for {
      urlBool    <- Eq.eqv(a, b).inject(Eq.byUrl)
      domainBool <- Eq.eqv(a, b).inject(Eq.byDomain)
      _          <- console.putStrLn(urlBool.toString + ", " + domainBool.toString)
    } yield ()

    program.exitCode
  }
}
