package com.github.windbird123

import zio._

package object myzio {
  type UserRepo = Has[UserRepo.Service]
  type Logging = Has[Logging.Service]
}
