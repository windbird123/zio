package com.github.windbird123.rabbitmq

import java.net.URI

import zio._
import zio.blocking.Blocking

object ClientTest extends zio.App {
  val safeChannelM: ZManaged[Blocking, Throwable, SafeChannel] = for {
    connection <- Amqp.connect(new URI("amqp://tala:fkdlsdnpqxkffk@wse-rabbitmq.dev.linecorp.com:5672"))
    channel    <- Amqp.createChannel(connection)
  } yield channel

  // stream 의 연속된 데이터를 읽고 마치는 문제가 있음 (참고: https://medium.com/@marcinbaraniecki/cool-article-b73e3411b292)
  // 몇초뒤데 rabbitmq 에 들어온 데이터는 읽어 오지 못한다.
  // ZStream.fromQueue 를 이용해 stream 을 만들면 reactive 하는 듯?
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    safeChannelM.use { safeChannel =>
      safeChannel
        .consume("tala.query", "windbird")
        .mapM { record =>
          val deliveryTag = record.getEnvelope.getDeliveryTag
          console.putStrLn(s"Received ${deliveryTag}: ${new String(record.getBody)}") *> safeChannel.ack(deliveryTag)
        }
        .runDrain
    }.exitCode
}
