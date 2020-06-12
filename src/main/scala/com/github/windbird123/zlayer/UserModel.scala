package com.github.windbird123.zlayer

import zio._

trait UserModel {
  def insert(u: User): Task[Unit]
}

class DefaultUserModel(db: DB.Service) extends UserModel {
  override def insert(u: User): Task[Unit] = db.execute(s"INSERT INTO user VALUES ('${u.name}')")
}
