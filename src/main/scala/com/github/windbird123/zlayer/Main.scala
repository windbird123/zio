package com.github.windbird123.zlayer

import zio._

object Main extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    val program: ZIO[Has[UserRegistration], Throwable, User] =
      ZIO.accessM(_.get[UserRegistration].register(User("kim", "wind@gmail.com")))

    val dbLayer: ZLayer[Any, Throwable, Has[DB.Service]] =
      ZLayer.succeed(DBConfig("jdbc://localhost")) >>> ConnectionPool.live >>> DB.live

    val userRegistrationLayer: ZLayer[Any, Throwable, Has[UserRegistration]] = dbLayer.map { env =>
      lazy val userModel        = new DefaultUserModel(env.get[DB.Service])
      lazy val userRegistration = new UserRegistration(DefaultUserNotifier, userModel)
      Has(userRegistration)
    }

    program.provideLayer(userRegistrationLayer).map(u => println(s"Registered user: $u (layers)")).exitCode
  }

  def runAlternative(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {

    val dbLayer: ZLayer[Any, Throwable, Has[DB.Service]] =
      ZLayer.succeed(DBConfig("jdbc://localhost")) >>> ConnectionPool.live >>> DB.live

    val program = ZIO.accessM[Has[DB.Service]] { env =>
      Task.effect {
        lazy val userModel        = new DefaultUserModel(env.get[DB.Service])
        lazy val userRegistration = new UserRegistration(DefaultUserNotifier, userModel)
        userRegistration.register(User("kim", "wind@gmail.com"))
      }
    }

    program.provideCustomLayer(dbLayer).map(u => println(s"Registered user: $u (layers)")).exitCode

  }
}
