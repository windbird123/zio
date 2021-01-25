package com.github.windbird123.module

trait Animal
trait Color
case object Red extends Color

abstract class AnimalWithTail(color: Color) extends Animal

trait DogTailServices { this: AnimalWithTail =>
  def raiseTail = println("raising tail")
  def curlTail = println("curl tail")
}

trait DogMouthServices { this: AnimalWithTail =>
  def bark = println("bark!!")
}

object IrishSetter extends AnimalWithTail(Red) with DogTailServices with DogMouthServices

object ModuleProgram {
  def main(args: Array[String]): Unit = {
    IrishSetter.bark
    IrishSetter.curlTail
  }
}
