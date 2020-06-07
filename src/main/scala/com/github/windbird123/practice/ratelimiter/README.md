* [Building a Super Easy Rate Limiter with ZIO](https://medium.com/wix-engineering/building-a-super-easy-rate-limiter-with-zio-88f1ccb49776) 의 구현
* [Building a Super Easy Rate Limiter with ZIO](https://medium.com/wix-engineering/building-a-super-easy-rate-limiter-with-zio-88f1ccb49776) 에서 설명된 
  BlockingRateLimiter, FutureRateLimiter 는 잘 동작하지 않는다. fork 로 만들어진 child fiber 를 포함한 parents 를 unsafeRun 에 적용할 경우 문제가 된다.
  이를 수정했다.
* 참고  
```scala
object ZioApp extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    val sample = console.putStrLn("test").repeat(Schedule.fixed(1.second)).fork *> ZIO.sleep(5.seconds)
    sample.exitCode
  }
}   // prints 'test' n-times

object ScalaApp {
  def main(args: Array[String]): Unit = {
    val sample = console.putStrLn("test").repeat(Schedule.fixed(1.second)).fork
    Runtime.default.unsafeRun(sample)
    Thread.sleep(5000)
  }
}   // prints 'test' only once
```
