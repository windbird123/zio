package com.github.windbird123.zlayer

import zio._

trait UserNotifier {
  def notify(u: User, msg: String): Task[Unit]
}

object DefaultUserNotifier extends UserNotifier {
  override def notify(u: User, msg: String): Task[Unit] = Task {
    println(s"Sending $msg to ${u.email}")
  }
}
