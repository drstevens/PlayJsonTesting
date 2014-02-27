package com.daverstevens

import play.api.libs.json._
import play.api.data.validation.ValidationError
import org.specs2.mutable.Specification
import org.specs2.ScalaCheck
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import play.api.libs.functional.Applicative
import scalaz.Equal

class JsResultGenerators {

  val myApplicativeJsResult: Applicative[JsResult] = new Applicative[JsResult] {

    def pure[A](a: A): JsResult[A] = JsSuccess(a)

    def map[A, B](m: JsResult[A], f: A => B): JsResult[B] = m.map(f)

    def apply[A, B](mf: JsResult[A => B], ma: JsResult[A]): JsResult[B] = (mf, ma) match {
      case (JsSuccess(f, pf), JsSuccess(a, pa)) => JsSuccess(f(a)).repath(pf).repath(pa)
      case (JsError(e1), JsError(e2)) => JsError(JsError.merge(e1, e2))
      case (JsError(e), _) => JsError(e)
      case (_, JsError(e)) => JsError(e)
    }
  }

  val validRecursiveSearch = arbitrary[String].map(RecursiveSearch.apply)
  val validKeyPathNode = arbitrary[String].map(KeyPathNode.apply)
  val validIdxPathNode = arbitrary[Int].map(IdxPathNode.apply)
  val validPathNode: Gen[PathNode] = Gen.oneOf(validIdxPathNode, validKeyPathNode, validRecursiveSearch)
  val validJsPath: Gen[JsPath] = Gen.listOfN(3, validPathNode).map(JsPath.apply)
  def validJsSuccess[A](genA: Gen[A]): Gen[JsSuccess[A]] = for {
    a <- genA
    path <- validJsPath
  } yield JsSuccess(a, path)

  val errorComponent = for {
    path <- validJsPath
    errors <- Gen.listOfN(3, arbitrary[String].map(e => ValidationError(e)))
  } yield path -> errors
  val validJsError: Gen[JsError] = Gen.listOfN(3, errorComponent).map(JsError.apply)

  def validJsResults[A](genA: Gen[A]): Gen[JsResult[A]] = Gen.oneOf(validJsSuccess(genA), validJsError)
  implicit val arbJsResultInt = Arbitrary(validJsResults(arbitrary[Int]))
  val validIntToInt: Gen[Int => Int] = arbitrary[Int].map(i => j => i + j)
  implicit val arbJsResultIntToInt = Arbitrary(validJsResults(validIntToInt))

  implicit def jsResultEqual[A]: Equal[JsResult[A]] = Equal.equalA[JsResult[A]]


}
