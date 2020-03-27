package com.github.windbird123.practice

sealed case class StepError(code: Int, message: String)

object ER01 extends StepError(404, "NOT FOUND")


