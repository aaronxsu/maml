package com.azavea.maml.ast

import com.azavea.maml.util._

import cats.data._
import Validated._

import java.security.InvalidParameterException

trait FoldableExpression { expression: Expression =>
  require(expression.children.length > 1, s"Incorrect number of arguments to a foldable expression. Expected >1, found ${expression.children.length}")
  val kindDerivation: (MamlKind, MamlKind) => MamlKind
  lazy val kind = this.children.map({ _.kind }).reduce({ kindDerivation(_, _) })
}

object FoldableExpression {
  def tileOrScalarDerivation(exp: FoldableExpression)(k1: MamlKind, k2: MamlKind): MamlKind = (k1, k2) match {
    case (MamlKind.Tile, MamlKind.Tile) => MamlKind.Tile
    case (MamlKind.Int, MamlKind.Int) => MamlKind.Int
    case (MamlKind.Tile, MamlKind.Int) => MamlKind.Tile
    case (MamlKind.Int, MamlKind.Tile) => MamlKind.Tile
    case (MamlKind.Double, MamlKind.Double) => MamlKind.Double
    case (MamlKind.Tile, MamlKind.Double) => MamlKind.Tile
    case (MamlKind.Double, MamlKind.Tile) => MamlKind.Tile
    case (MamlKind.Double, MamlKind.Int) => MamlKind.Double
    case (MamlKind.Int, MamlKind.Double) => MamlKind.Double
    case (x1, x2) => throw new InvalidParameterException(s"Expected tile, int, or double kind. Found $x1 $x2")
  }
}

