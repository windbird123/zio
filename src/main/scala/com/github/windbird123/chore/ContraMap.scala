package com.github.windbird123.chore

final case class Box[A](value: A)

trait Printable[A] { self =>
  def format(value: A): String

  def contramap[B](f: B => A): Printable[B] = new Printable[B] {
    override def format(value: B): String = self.format(f(value))
  }
}

object ContraMap {
  implicit val stringPrintable = new Printable[String] {
    override def format(value: String): String = s"[$value]"
  }

  implicit val booleanPrintable = new Printable[Boolean] {
    override def format(value: Boolean): String = if (value) "yes" else "no"
  }

  implicit val boxPrintable = stringPrintable.contramap[Box[String]](box => box.value)

  def format[A: Printable](value: A): String = implicitly[Printable[A]].format(value)

  def main(args: Array[String]): Unit = {
    val box = Box("Hello World")
    val out = format(box)
    println(out)
  }
}
