package com.github.windbird123.encoding.declarative

case class Box(id: String)

/////////////////////////////////////////////////////////////////
sealed trait Step { self =>
  def >>(that: Step): Step = Proceed(self, that)
}

final case class Proceed(prev: Step, next: Step) extends Step

final case class FaceStep(id: String) extends Step {
  def detect(box: Box): Box = Box(s"${box.id} >> $id")
}

final case class OcrStep(id: String) extends Step {
  def detect(box: Box): Box = Box(s"${box.id} >> $id")
}
/////////////////////////////////////////////////////////////////

object StepUtil {
  def pass(step: Step, box: Box): Box = step match {
    case Proceed(prev, next) => pass(next, pass(prev, box)) // Step 의 composition 을 위해 !!!
    case step @ FaceStep(_)  => step.detect(box)
    case step @ OcrStep(_)   => step.detect(box)
  }
}


object RoyEncoding {
  def main(args: Array[String]): Unit = {
    val initialBox = Box("initialBox")
    val step       = FaceStep("face") >> OcrStep("ocr")

    val outputBox = StepUtil.pass(step, initialBox)
    println(outputBox.id)
  }
}
