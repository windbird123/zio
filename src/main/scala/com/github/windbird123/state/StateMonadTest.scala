package com.github.windbird123.state

case class GolfState(distance: Int)

object State {
  def lift[A, S](value: A): State[A, S] = State((s: S) => (value, s))
}

case class State[A, S](run: S => (A, S)) {
//  def map[B](f: A => B): State[B, S] = State { (s: S) =>
//    val (currResult, currState) = run(s)
//    val nextResult              = f(currResult)
//    (nextResult, currState)
//  }

  def map[B](f: A => B): State[B, S] = flatMap(a => State.lift(f(a)))

  def flatMap[B](f: A => State[B, S]): State[B, S] = State { (s: S) =>
    val (currResult, currState) = run(s)
    val nextState               = f(currResult)
    nextState.run(currState)
  }
}

object StateMonadTest {
  def swing(distance: Int): State[Int, GolfState] = State { (state: GolfState) =>
    val newDistance = state.distance + distance
    (newDistance, GolfState(newDistance))
  }

  def main(args: Array[String]): Unit = {
    val initialState = GolfState(3)

    val logic = for {
      _     <- swing(15)
      _     <- swing(10)
      total <- swing(1)
    } yield total

    val out = logic.run(initialState)
    println(out)
  }
}
