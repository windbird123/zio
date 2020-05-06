package com.github.windbird123

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pureconfig.ConfigReader.Result
import pureconfig._

sealed trait AuthMethod
case class Login(username: String, password: String) extends AuthMethod
case class Token(token: String)                      extends AuthMethod
case class PrivateKey(pkFile: java.io.File)          extends AuthMethod

case class ServiceConf(
  host: String,
  port: Int,
  useHttps: Boolean,
  authMethods: List[AuthMethod]
)

object PureConfigTest extends LazyLogging {
  def main(args: Array[String]): Unit = {
    import pureconfig.generic.auto._
    val config: Result[ServiceConf] = ConfigSource.fromConfig(ConfigFactory.load("my_app")).load[ServiceConf] // ConfigSource.file(...)
    println(config)
  }
}
