package com.daverstevens

import play.api.libs.json._
import org.specs2.mutable.Specification
import org.specs2.ScalaCheck

class ReadsWritesSpec extends Specification {

  "JsResult.flatMap should result in the same value as using applicative" ! {
    val nodeD = D(1, "5", DValue(5.5D))

    val json = Node.writes.writes(nodeD)

    val resultViaApplicative = Node.readsWithApplicative.reads(json)
    val resultViaFlatMap = Node.readsWithFlatMap.reads(json)

    resultViaApplicative ==== resultViaFlatMap
  }

}
