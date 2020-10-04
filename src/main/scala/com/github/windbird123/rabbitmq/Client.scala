package com.github.windbird123.rabbitmq

import java.net.URI
import java.util.concurrent.ExecutorService

import com.rabbitmq.client.impl.nio.NioParams
import com.rabbitmq.client.{
  CancelCallback,
  Channel,
  Connection,
  ConnectionFactory,
  ConsumerShutdownSignalCallback,
  DeliverCallback,
  Delivery,
  ShutdownSignalException
}
import zio._
import zio.blocking.Blocking
import zio.stream.ZStream
import zio.duration._

import scala.concurrent.ExecutionContextExecutorService

// https://github.com/svroonland/zio-amqp/blob/master/src/main/scala/nl/vroste/zio/amqp/Client.scala


// stream 의 연속된 데이터를 읽고 마치는 문제가 있음 (참고: https://medium.com/@marcinbaraniecki/cool-article-b73e3411b292)
// 몇초뒤데 rabbitmq 에 들어온 데이터는 읽어 오지 못한다.
class SafeChannel(channel: Channel, access: Semaphore) {
  def withChannel[R, T](f: Channel => ZIO[R, Throwable, T]): ZIO[R, Throwable, T] = access.withPermit(f(channel))

  def consume(
    queue: String,
    consumerTag: String,
    autoAck: Boolean = false
  ): ZStream[blocking.Blocking, Throwable, Delivery] = ZStream.effectAsyncM { cb =>
    withChannel { channel =>
      blocking.effectBlocking {
        channel.basicConsume(
          queue,
          autoAck,
          consumerTag,
          new DeliverCallback {
            override def handle(consumerTag: String, message: Delivery): Unit = {
              println("RECEIVED !!!!!!!!!!!!!!!")
              cb(ZIO.succeed(Chunk.single(message)))
            }
          },
          new CancelCallback {
            override def handle(consumerTag: String): Unit = cb(ZIO.fail(None))
          },
          new ConsumerShutdownSignalCallback {
            override def handleShutdownSignal(consumerTag: String, sig: ShutdownSignalException): Unit =
              cb(ZIO.fail(Some(sig)))
          }
        )
      }
    }.ensuring {
      withChannel(c => blocking.effectBlocking(c.basicCancel(consumerTag))).ignore
    }
  }

  def ack(deliveryTag: Long, multiple: Boolean = false): ZIO[Blocking, Throwable, Unit] =
    withChannel(channel => blocking.effectBlocking(channel.basicAck(deliveryTag, multiple)))
}

object Amqp {
  def connect(factory: ConnectionFactory): ZManaged[Blocking, Throwable, Connection] =
    ZIO
      .runtime[Any]
      .flatMap { runtime =>
        val eces: ExecutorService = runtime.platform.executor.asECES
        factory.useNio()
        factory.setNioParams(new NioParams().setNioExecutor(eces))
        blocking.effectBlocking(factory.newConnection(eces))
      }
      .toManaged(channel => UIO(channel.close()))

  def connect(uri: URI): ZManaged[Blocking, Throwable, Connection] = {
    val factory = new ConnectionFactory()
    factory.setUri(uri)
    connect(factory)
  }

  def createChannel(connection: Connection): ZManaged[Blocking, Throwable, SafeChannel] =
    (for {
      channel <- Task(connection.createChannel())
      permit  <- Semaphore.make(1)
    } yield new SafeChannel(channel, permit))
      .toManaged(_.withChannel(channel => blocking.effectBlocking(channel.close())).orDie)
}
