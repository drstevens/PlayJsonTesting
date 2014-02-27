package com.daverstevens

import play.api.libs.json._
import play.api.libs.functional.Applicative

case class DValue(d: Double)
object DValue {
  implicit val format: Format[DValue] = Json.format[DValue]
}

sealed trait Node {
  def i: Int
  def j: String
}
object Node {
  val writes: Writes[Node] = Writes { node =>
    val others = node match {
      case a: A => Nil
      case b: B => List("k" -> JsNumber(b.k))
      case c: C => List("l" -> JsNumber(c.l))
      case d: D => List("d" -> implicitly[Writes[DValue]].writes(d.d))
    }
    JsObject("i" -> JsNumber(node.i) :: "j" -> JsString(node.j) :: others)
  }

  val readsWithApplicative: Reads[Node] = Reads { json =>
    val iResult = (json \ "i").validate[Int]
    val jResult = (json \ "j").validate[String]

    val result: JsResult[(Int, String) => Node] =
      json \ "k" match {
        case _: JsUndefined =>
          json \ "l" match {
            case _: JsUndefined =>
              json \ "d" match {
                case _: JsUndefined => JsSuccess(A(_, _))
                case dJS => dJS.validate[DValue] map (d => D.apply(_, _, d))
              }
            case lJS => lJS.validate[Long] map (l => C.apply(_, _, l))
          }
        case kJS => kJS.validate[Long] map (k => B.apply(_, _, k))
      }
    implicitly[Applicative[JsResult]].apply[String, Node](
      implicitly[Applicative[JsResult]].apply[Int, String => Node](result.map(_.curried), iResult), jResult)
  }

  val readsWithFlatMap: Reads[Node] = Reads { json =>
    for {
      i <- (json \ "i").validate[Int]
      j <- (json \ "j").validate[String]
      result <- json \ "k" match {
        case _: JsUndefined =>
          json \ "l" match {
            case _: JsUndefined =>
              json \ "d" match {
                case _: JsUndefined => JsSuccess(A(i, j))
                case dJS => dJS.validate[DValue] map(D.apply(i, j, _))
            }
            case lJS => lJS.validate[Long] map (C.apply(i, j, _))
          }
        case kJS => kJS.validate[Long] map (B.apply(i, j, _))
      }
    } yield result
  }
}

case class A(i: Int, j: String) extends Node
case class B(i: Int, j: String, k: Long) extends Node
case class C(i: Int, j: String, l: Long) extends Node
case class D(i: Int, j: String, d: DValue) extends Node
