package maml.eval.scalar

import maml.eval._
import cats._
import cats.data._
import cats.data.Validated._
import cats.data.{NonEmptyList => NEL, _}
import cats.implicits._
import spire.syntax.cfor._

import scala.reflect.ClassTag

trait ScalarRep extends LazyRep {
  def evaluate: Int
  def evaluateDouble: Double

  def as[A](implicit ct: ClassTag[A]): Interpreted[A] = {
    val cls = ct.runtimeClass
    if (classOf[Int] isAssignableFrom cls)
      Valid(evaluate.asInstanceOf[A])
    else if (classOf[Double] isAssignableFrom cls)
      Valid(evaluateDouble.asInstanceOf[A])
    else
      Invalid(NEL.of(EvalTypeError(cls.getName, List("int", "double"))))
  }
}

case class Scalar(value: Double) extends ScalarRep {
  def evaluate: Int = value.toInt
  def evaluateDouble: Double = value
}

case class ScalarFold(scalars: List[ScalarRep], f: (Int, Int) => Int, g: (Double, Double) => Double) extends ScalarRep {
  def evaluate = scalars.map(_.evaluate).reduce(f)
  def evaluateDouble = scalars.map(_.evaluateDouble).reduce(g)
}
