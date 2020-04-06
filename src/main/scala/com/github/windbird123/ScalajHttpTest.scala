package com.github.windbird123

import com.typesafe.scalalogging.LazyLogging
import scalaj.http._

object ScalajHttpTest extends LazyLogging {
  def main(args: Array[String]): Unit = {
    zio.blocking.effectBlocking {
      val res = Http("http://imgsrch.nave.com/api/doc/qc")
        .param("query", "황하나") // encoding 할 필요가 없다.
        .param("startDate", "20190401")
        .param("endDate", "20190421")
        .param("size", "10")
        .param("source", "imgsrch_model")
        .asString

      println(res.code)
      println(res.body)
    }

    val exRes = zio.blocking.effectBlocking {
//      val out = Http("http://date.jsontest.com/").asString.body
      val out = Http("http://foo.com/search/").asString.body
      logger.info(out)
      out
    }

    zio.Runtime.default.unsafeRunSync(exRes.provideLayer(zio.blocking.Blocking.live))
  }
}
