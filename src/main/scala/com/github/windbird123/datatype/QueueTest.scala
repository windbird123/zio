package com.github.windbird123.datatype

import java.util.concurrent.TimeUnit

import zio._
object QueueTest extends zio.App {
  val res: UIO[Int] = for {
    queue <- Queue.bounded[Int](100) // Queue.unbounded, Queue.dropping, Queue.sliding
    _     <- queue.offer(1)          // offer 는 suspend ..
    v1    <- queue.take              // take 도 suspend ..
  } yield v1

  val res2: UIO[Unit] = for {
    queue <- Queue.bounded[Int](1)
    _     <- queue.offer(1)
    f     <- queue.offer(1).fork // will be suspended because the queue is full
    _     <- queue.take
    _     <- f.join
  } yield ()

  val res3: UIO[String] = for {
    queue <- Queue.bounded[String](100)
    f     <- queue.take.fork // will be suspended because the queue is empty
    _     <- queue.offer("something")
    v     <- f.join
  } yield v

  // take 대신에 poll 을 사용해 suspend 되는 것을 피할 수 있다. queue 가 empty 일 때는 None 을 리턴
  val polled: UIO[Option[Int]] = for {
    queue <- Queue.bounded[Int](100)
    _     <- queue.offer(10)
    _     <- queue.offer(20)
    head  <- queue.poll
  } yield head

  // Queue#takeUpTo(5):  If the queue doesn't have enough items to return, it will return all the items without waiting for more offers.
  // Queue#takeAll: it also returns without waiting (an empty list if the queue is empty).

  //////////////////////////////////////////////////////////////////////////////////
  // Queue#contramap
  // A type 넣은(offer) 다음 꺼낼(take) 때는 B type 을 가져올 수 있다.
  // Queue#contramap(f: A => B)
  // 넣을 때는 String, 꺼낼 때는 Int 로 가져오기 위해  (s: String) => s.length
  // "abcde" 를 넣었는데 꺼내 보니 5
  // ==> contramap 이라는 이름이 적당한 것이지 의문???
  //////////////////////////////////////////////////////////////////////////////////
  val contraQueue = for {
    queue    <- Queue.bounded[Int](3)
    lenQueue = queue.contramap((s: String) => s.length)
    _        <- lenQueue.offer("abcde")
    out      <- lenQueue.take // out is Int type
    _        <- console.putStrLn(out.toString) // 5
  } yield ()

  import zio.duration._
  import zio.clock._

  val timeQueued: UIO[ZQueue[Clock, Nothing, Clock, Nothing, String, (Duration, String)]] =
    for {
      queue <- Queue.bounded[(Long, String)](3)

      // 넣을 때는 String type 이지만 꺼낼 때는 (넣은시간, String)
      enqueueTimestamps = queue.contramapM { el: String => currentTime(TimeUnit.MILLISECONDS).map((_, el)) }

      durations = enqueueTimestamps.mapM {
        case (enqueueTs, el) => // (넣은시간, String) 을 꺼내
          currentTime(TimeUnit.MILLISECONDS).map(dequeueTs => ((dequeueTs - enqueueTs).millis, el)) // 체류 시간 계산
      }
    } yield durations

  val timeQueueTest = for {
    queue <- timeQueued
    _     <- queue.offer("a")
    out   <- queue.take
    _     <- console.putStrLn(out.toString())
  } yield ()

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    contraQueue *> UIO.succeed(0)
//    timeQueueTest *> UIO.succeed(0)
}
