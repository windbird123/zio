package com.github.windbird123.zlayer

import zio._

class UserRegistration(notifier: UserNotifier, userModel: UserModel) {
  def register(u: User): Task[User] =
    for {
      _ <- userModel.insert(u)
      _ <- notifier.notify(u, "Welcome!")
    } yield u
}
