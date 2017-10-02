package com.nike.redwiggler.blueprint

import org.scalatest.{FunSpec, Matchers}

class BlueprintPathParserSpec extends FunSpec with Matchers {

  it("should parse literal path") {
    val path = BlueprintPathParser("/my/api/v1")
    path.asString should equal ("/my/api/v1")
  }

  it("should parse path with query parameters") {
    val path = BlueprintPathParser("/my/api/v1{?foo,bar}")
    path.asString should equal ("/my/api/v1")
  }
}
