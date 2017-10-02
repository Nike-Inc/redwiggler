package com.nike.redwiggler.blueprint

import org.scalatest.{FunSpec, Matchers}

class BlueprintPathParserSpec extends FunSpec with Matchers {

  it("should parse literal path") {
    val path = BlueprintPathParser("/my/api/v1")
    path.asString should equal ("/my/api/v1")
  }

  it("should parse literal base path") {
    val path = BlueprintPathParser("/")
    path.asString should equal ("/")
  }

  it("should parse path with query parameters") {
    val path = BlueprintPathParser("/my/api/v1{?foo,bar}")
    path.asString should equal ("/my/api/v1")
  }

  it("should parse path with path parameters") {
    val path = BlueprintPathParser("/my/api/:id")
    path.asString should equal ("/my/api/{id}")
  }

  it("should parse path with path parameters in middle") {
    val path = BlueprintPathParser("/my/api/:id/v1")
    path.asString should equal ("/my/api/{id}/v1")
  }

  it("should parse path with path and query parameters") {
    val path = BlueprintPathParser("/my/api/:id{?foo,bar}")
    path.asString should equal ("/my/api/{id}")
  }
}
