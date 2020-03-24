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
  def getUser(userId: Int): ZIO[UserRepo, DBError, Option[User]] =
    ZIO.accessM(_.get.getUser(userId))

  def createUser(user: User): ZIO[UserRepo, DBError, Unit] =
    ZIO.accessM(_.get.createUser(user))

  // instance
  // type Layer[+E, +ROut] = ZLayer[Any, E, ROut]
  val inMemory: Layer[Nothing, UserRepo] = ZLayer.succeed(new Service {
    override def getUser(userId: Int): IO[DBError, Option[User]] = {
      val user = User(456, "def")
      UIO.effectTotal(Some(user))
    }

    override def createUser(user: User): IO[DBError, Unit] = UIO.effectTotal(())
  })
}

object Logging {
  trait Service {
    def info(s: String): UIO[Unit]
    def error(s: String): UIO[Unit]
  }

  // accessor
  def info(s: String): ZIO[Logging, Nothing, Unit] = ZIO.accessM(_.get.info(s))

  def error(s: String): ZIO[Logging, Nothing, Unit] =
    ZIO.accessM(_.get.error(s))

  // instance
  import zio.console.Console
  val consoleLogger: ZLayer[Console, Nothing, Logging] = ZLayer.fromFunction(
    console =>
      new Service {
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
    val makeUser: ZIO[Logging with UserRepo, DBError, Unit] = for {
     _ <- Logging.info(s"insert user")
     _ <- UserRepo.createUser(user)
    _ <- Logging.info(s"user inserted")
    } yield ()

    import zio.console._
    val horizontalLayer : ZLayer[Console, Nothing, Logging with UserRepo] = Logging.consoleLogger ++ UserRepo.inMemory
    val fullLayer: Layer[Nothing, Logging with UserRepo] = Console.live >>> horizontalLayer

    val myProg = makeUser.provideLayer(fullLayer)
    Runtime.default.unsafeRunSync(myProg)
  }
}
