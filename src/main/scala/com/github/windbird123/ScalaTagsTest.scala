package com.github.windbird123

import scalatags.Text.all._

object ScalaTagsTest {
  def main(args: Array[String]): Unit = {
    val myVal = "KJM"
    val dom = html(
      head(
        script(src := "http//..."),
        script()
      ),
      body(
        div(
          h1(id := "title", "This is a title"),
          p("M")
        ),
        table(
          Seq(tr("abc"), tr("23"))
        )

      )
    )

    val s = dom.render
    println(s)
  }

}
