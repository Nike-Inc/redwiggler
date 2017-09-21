package com.nike.redwiggler.swagger

import com.nike.redwiggler.core.models.{LiteralPathComponent, Path, PlaceHolderPathComponent}
import org.scalatest.{FunSpec, Matchers}

class SwaggerPathSpec extends FunSpec with Matchers {

  it("should parse literal path") {
    val path = SwaggerPath("/hello/world")
    path should equal(Path(Seq(LiteralPathComponent("hello"), LiteralPathComponent("world"))))
  }

  it("should parse path variable path") {
    val path = SwaggerPath("/hello/{id}/world")
    path should equal(Path(Seq(LiteralPathComponent("hello"), PlaceHolderPathComponent("id"), LiteralPathComponent("world"))))
  }
}
