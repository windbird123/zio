package com.github.windbird123.encoding.declarative

case class Box(name: String)

sealed trait Step { self =>
  def >>(that: Step): Step = Join(self, that)

  def pass(box: Box): Box = self match {
    case Join(prev, next) => next.pass(prev.pass(box))
    case FaceStep(name)   => Box(s"${box.name} >> $name")
    case OcrStep(name)    => Box(s"${box.name} >> $name")
  }

  def canHandle(box: Box): Boolean = self match {
    case Join(prev, next) => prev.canHandle(box) || next.canHandle(box)
    case FaceStep(name)   => box.name.contains(name)
    case OcrStep(name)    => box.name.contains(name)
  }
}

final case class Join(prev: Step, next: Step) extends Step

final case class FaceStep(name: String) extends Step
final case class OcrStep(name: String)  extends Step

object Test {
  def main(args: Array[String]): Unit = {
    val faceStep = FaceStep("Face")
    val ocrStep  = OcrStep("Ocr")

    val step = faceStep >> ocrStep
    val box  = Box("target: Face Ocr")

    val out = step.pass(box)
    println(out)

    val seq = Seq(faceStep, ocrStep)
    val filtered = seq.filter(_.canHandle(box))
    println(filtered)
  }
}
