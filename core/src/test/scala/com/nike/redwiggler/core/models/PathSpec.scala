package com.nike.redwiggler.core.models

import org.scalatest.{FunSpec, Matchers}

class PathSpec extends FunSpec with Matchers {

  describe("literal paths") {
    it("should match when paths are literal matches") {
      val path = Path(Seq(LiteralPathComponent("hello"), LiteralPathComponent("world")))
      path.matches("/hello/world") should be(true)
    }
    it("should NOT match when paths are not literal matches") {
      val path = Path(Seq(LiteralPathComponent("hello"), LiteralPathComponent("world")))
      path.matches("/hello/bar") should be(false)
    }
    it("should NOT match extra characters") {
      val path = Path(Seq(LiteralPathComponent("hello"), LiteralPathComponent("world")))
      path.matches("/hello/world/baz") should be(false)
    }
    it("should NOT match less characters") {
      val path = Path(Seq(LiteralPathComponent("hello"), LiteralPathComponent("world")))
      path.matches("/hello") should be(false)
    }
  }

  describe("path parameters") {
    it("should match when paths are have different matches") {
      val path = Path(Seq(LiteralPathComponent("hello"), PlaceHolderPathComponent("id")))
      path.matches("/hello/world") should be(true)
    }
    it("should match when paths are some other value") {
      val path = Path(Seq(LiteralPathComponent("hello"), PlaceHolderPathComponent("id")))
      path.matches("/hello/bar") should be(true)
    }
    it("should NOT match extra characters") {
      val path = Path(Seq(LiteralPathComponent("hello"), PlaceHolderPathComponent("id")))
      path.matches("/hello/world/baz") should be(false)
    }
    it("should NOT match less characters") {
      val path = Path(Seq(LiteralPathComponent("hello"), PlaceHolderPathComponent("id")))
      path.matches("/hello") should be(false)
    }
  }

  describe("parse uri") {
    it("should parse absolute uri") {
      val path = Path("/hello/world")
      path should equal(Path(Seq(LiteralPathComponent("hello"), LiteralPathComponent("world"))))
    }
    it("should parse fail on non absolute uri ") {
      intercept[PathMustBeAbsoluteException] {
        Path("hello/world")
      }
    }
    it("should parse single slash") {
      val path = Path("/")
      path.components should have size 0
    }
  }

  it("should concatenate") {
    val path1 = Path("/hello/world")
    val path2 = Path("/over/there")
    val path3 = path1 / path2

    path3 should equal(Path("/hello/world/over/there"))
  }
}
