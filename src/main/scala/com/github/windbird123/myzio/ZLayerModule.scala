package com.github.windbird123.myzio

import zio._

case class User(id: Int, name: String)

trait DBError

object UserRepo {
  trait Service {
    def getUser(userId: Int): IO[DBError, Option[User]]
    def createUser(user: User): IO[DBError, Unit]
  }

  // accessor
  def getUser(userId: Int): ZIO[Has[UserRepo.Service], DBError, Option[User]] =
    ZIO.accessM(_.get.getUser(userId))

  def createUser(user: User): ZIO[Has[UserRepo.Service], DBError, Unit] =
    ZIO.accessM(_.get.createUser(user))

  // instance
  // type Layer[+E, +ROut] = ZLayer[Any, E, ROut]
  val inMemory: Layer[Nothing, Has[UserRepo.Service]] =
    ZLayer.succeed(new Service {
      override def getUser(userId: Int): IO[DBError, Option[User]] = {
        val user = User(456, "def")
        UIO.effectTotal(Some(user))
      }

      override def createUser(user: User): IO[DBError, Unit] =
        UIO.effectTotal(())
    })
}

// 아래처럼 trait 를 object 안에서 밖으로 빼는건 어떨까?
// UserRepo 처럼 안으로 넣는게 나을것 같다..
trait Logging {
  def info(s: String): UIO[Unit]
  def error(s: String): UIO[Unit]
}

object Logging {
  // accessor
  def info(s: String): ZIO[Has[Logging], Nothing, Unit] =
    ZIO.accessM(_.get.info(s))

  def error(s: String): ZIO[Has[Logging], Nothing, Unit] =
    ZIO.accessM(_.get.error(s))

  // instance
  import zio.console.Console
  val consoleLogger: ZLayer[Console, Nothing, Has[Logging]] =
    ZLayer.fromFunction(
      console =>
        new Logging {
          override def info(s: String): UIO[Unit] =
            console.get.putStrLn(s"info - $s")

          override def error(s: String): UIO[Unit] =
            console.get.putStrLn(s"error - $s")
      }
    )
}

object ModuleTest {
  def main(args: Array[String]): Unit = {
    val user = User(123, "abc")
    val makeUser
      : ZIO[Has[Logging] with Has[UserRepo.Service], DBError, Unit] =
      for {
        _ <- Logging.info(s"insert user")
        _ <- UserRepo.createUser(user)
        _ <- Logging.info(s"user inserted")
      } yield ()

    import zio.console._
    val horizontalLayer: ZLayer[Console, Nothing, Has[Logging] with Has[
      UserRepo.Service
    ]] = Logging.consoleLogger ++ UserRepo.inMemory
    val fullLayer: Layer[Nothing, Has[Logging] with Has[
      UserRepo.Service
    ]] = Console.live >>> horizontalLayer

    val myProg = makeUser.provideLayer(fullLayer)
    Runtime.default.unsafeRunSync(myProg)
  }
}


object HasTest {
  trait A {
    def f() : Unit = {}
  }

  trait B {
    def g(): Unit = {}
  }

  def main(args: Array[String]): Unit = {
    val a = new A {}
    val ha = Has(a)
    val b = new B {}
    val hb = Has(b)

    val hc : Has[B] with Has[A] = hb ++ ha
    hc.get[A].f()
    hc.get[B].g()
  }
}
