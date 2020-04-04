package com.github.windbird123.practice

sealed trait StepError {
  val code: Int
  val message: String
}

case object ER01 extends StepError {
  override val code: Int       = 404
  override val message: String = "NOT FOUND"
}
