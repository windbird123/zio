package com.github.windbird123

case class Player(name: String, score: Int)

object PureExample1 {
  def contest(p1: Player, p2: Player): Unit =
    if (p1.score > p2.score) println(s"${p1.name} is the winner")
    else if (p2.score > p1.score) println(s"${p2.name} is the winner")
    else println("It's a draw")
}

object PureExample2 {
  def winner(p1: Player, p2: Player): Option[Player] =
    if (p1.score > p2.score) Some(p1)
    else if (p1.score < p2.score) Some(p2)
    else None

  def contest(p1: Player, p2: Player): Unit = winner(p1, p2) match {
    case Some(Player(name, _)) => println(s"$name is the winner")
    case None                  => println("It's a draw")
  }
}

object PureExample3 {
  def winner(p1: Player, p2: Player): Option[Player] =
    if (p1.score > p2.score) Some(p1)
    else if (p1.score < p2.score) Some(p2)
    else None

  def winnerMsg(p: Option[Player]): String = p match {
    case Some(Player(name, _)) => s"$name is the winner"
    case None                  => "It's a draw"
  }

  def contest(p1: Player, p2: Player): Unit = println(winnerMsg(winner(p1, p2)))
}
