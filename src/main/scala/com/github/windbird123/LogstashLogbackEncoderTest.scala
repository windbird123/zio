package com.github.windbird123

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import com.typesafe.scalalogging.LazyLogging
import net.logstash.logback.appender.listener.LoggingEventAppenderListenerImpl
import net.logstash.logback.argument.StructuredArguments._
import net.logstash.logback.marker.{LogstashMarker, Markers}
import org.slf4j.{MDC, Marker, MarkerFactory}

import scala.collection.JavaConverters._

object LogstashLogbackEncoderTest extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val markerFromFactory: Marker = MarkerFactory.getMarker("MY_MARKER")
    MDC.put("MY_MDC", "mdc_1234")

    // MarkerFactory.getMarker 로 생성한 marker 는 json 의 tags 항목에 값이 들어간다.
    // {"timestamp":"2020-11-08T20:54:15.071+09:00","message":["TEST"],"logger_name":"c.g.w.LogstashLogbackEncoderTest$","thread_name":"main","level":"INFO","MY_MDC":"mdc_1234","tags":["MY_MARKER"],"name":true,"x":1}
    logger.info(markerFromFactory, "TEST", kv("name", true), kv("x", 1))

    // 아래와 같이 생성한 marker 는 json 의 CUSTOM_MARKER 항목을 생성해 값이 들어간다.
    // {"timestamp":"2020-11-08T20:54:15.080+09:00","message":["TEST"],"logger_name":"c.g.w.LogstashLogbackEncoderTest$","thread_name":"main","level":"INFO","MY_MDC":"mdc_1234","CUSTOM_MARKER":"MY_MARKER_1234","enable":true,"age":22}
    val marker: LogstashMarker = Markers.append("CUSTOM_MARKER", "MY_MARKER_1234").and(Markers.append("SECOND", 2))
    logger.info(marker, "TEST", kv("enable", true), kv("age", 22))
  }
}

class MyAppenderListener extends LoggingEventAppenderListenerImpl {
  override def eventAppended(appender: Appender[ILoggingEvent], event: ILoggingEvent, durationInNanos: Long): Unit = {
    println("CALLED ==========================================")
    println("MarkerName: " + event.getMarker.getName)

    def loop(iter: Iterator[Marker]): Unit = {
      if (iter.hasNext) {
        val marker = iter.next()

        println("Marker22222: " + marker.toString)
        loop(iter)
      }
    }

    loop(event.getMarker.iterator().asScala)
  }
}
