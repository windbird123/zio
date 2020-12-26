package com.github.windbird123.variance

/////////////////////////////////////////////////////////////////////////////
// https://books.underscore.io/essential-scala/essential-scala.html#variance
/////////////////////////////////////////////////////////////////////////////

// Covariant Generic Sum Type Pattern
sealed trait Box[+A]
final case class SimpleBox[A](a: A) extends Box[A]
case object BaseBox                 extends Box[Nothing]

// Contravariant Position Pattern
case class My[+A, -B]() {
  // f(a: A): A 를 하면 에러가 나니 고친다면 ..
  def f[C >: A](c: C): C = ???

  // g(b: B): B 를 하면 에러가 나니 고친다면 ..
  def g[C <: B](b: B): C = ???
}

// function as parameter
sealed trait FuncParam[+A, +B] {
  // 아래 flatMap 은 문제 발생 why?

  // def flatMap[C](f: B => Sum[A, C]): Sum[A, C] = ???

  // f 는 함수의 인자로 contravariant 위치에 있다.
  // f 는 B => Sum[A, C] 의 supertype 이 되어야 한다.
  // supertype 이 되기 위해서는 B 가 covariant, A, C 가 contravariant 가 되어야 한다.
  // A 가 covariant 로 문제가 되므로 아래와 같이 고치면 된다.

  def flatMap[A2 >: A, C](f: B => FuncParam[A2, C]): FuncParam[A2, C] = ???
}

// function as return type
sealed trait FuncReturn[+A, +B] {
  // 아래는 문제가 됨
  //def f(): A => B = ???

  // f 의 리턴 타입인 A=>B 가  covariant 위치에 있다.
  // A => B 가 covariant 가 되기 위해서는  parameter type 인 A 가 contravariant 이고, return type 인 B 가 covariant 가 되어야 한다.
  // 아래와 같이 고쳐져야 한다.

  def f[C >: A](): C => B = ???
}


/**
 * FuncParam, FuncReturn 을 종합해 보면
 * trait[A, B] { ... } 내부에서 아래  f: A => B function type 이 param(contravariant 위치) 또는 return(covariant 위치) type 으로 사용될 때
 *
 * f 가 covariant 가 될려면 A 가 contravariant, B 가 covariant 가 되어야 한다.
 * f 가 contravariant 가 될려면 A 가 covariant, B 가 contravariant 가 되어야 한다.
 */

object VarianceTutorial {}
