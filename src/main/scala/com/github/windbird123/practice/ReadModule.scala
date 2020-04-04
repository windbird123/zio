package com.github.windbird123.practice

import zio._

object ReadModule {
  trait Service {
    def read(box: Box): IO[StepError, Box]
  }

  def read(box: Box): ZIO[Has[Service], StepError, Box] =
    ZIO.accessM(_.get[Service].read(box))

  val live: ZLayer[Has[Config], Nothing, Has[Service]] =
    ZLayer.fromFunction { env =>
      new Service {
        override def read(box: Box): IO[StepError, Box] = {
          val config = env.get[Config]
          if (config.server == "naver" && box.category == "blog")
            IO.succeed(box)
          else IO.fail(ER01)
        }
      }
    }

  // 위의 코드는 아래와 같이 ZLayer.fromEffect 를 이용해 아래와 같이 코딩할 수도 있다.
//  ZLayer.fromEffect(ZIO.access[Has[Config]] { env =>
//    new Service {
//      override def read(box: Box): IO[StepError, Box] = {
//        val config = env.get[Config]
//        if (config.server == "naver" && box.category == "blog")
//          IO.succeed(box)
//        else IO.fail(ER01)
//      }
//    }
//  })
}
