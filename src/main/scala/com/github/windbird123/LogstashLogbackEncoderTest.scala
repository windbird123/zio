package com.github.windbird123

import com.typesafe.scalalogging.LazyLogging
import net.logstash.logback.argument.StructuredArguments._
import net.logstash.logback.marker.{LogstashMarker, Markers}
import org.slf4j.{MDC, MarkerFactory}

object LogstashLogbackEncoderTest extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val markerFromFactory = MarkerFactory.getMarker("MY_MARKER")
    MDC.put("MY_MDC", "mdc_1234")

    // MarkerFactory.getMarker 로 생성한 marker 는 json 의 tags 항목에 값이 들어간다. (잘사용 안할 듯?)
    // {"timestamp":"2020-11-08T20:54:15.071+09:00","message":["TEST"],"logger_name":"c.g.w.LogstashLogbackEncoderTest$","thread_name":"main","level":"INFO","MY_MDC":"mdc_1234","tags":["MY_MARKER"],"name":true,"x":1}
    logger.info(markerFromFactory, "TEST", kv("name", true), kv("x", 1))

    // 아래와 같이 생성한 marker 는 json 의 CUSTOM_MARKER 항목을 생성해 값이 들어간다.
    // {"timestamp":"2020-11-08T20:54:15.080+09:00","message":["TEST"],"logger_name":"c.g.w.LogstashLogbackEncoderTest$","thread_name":"main","level":"INFO","MY_MDC":"mdc_1234","CUSTOM_MARKER":"MY_MARKER_1234","enable":true,"age":22}
    val marker: LogstashMarker = Markers.append("CUSTOM_MARKER", "MY_MARKER_1234")
    logger.info(marker, "TEST", kv("enable", true), kv("age", 22))
  }
}
