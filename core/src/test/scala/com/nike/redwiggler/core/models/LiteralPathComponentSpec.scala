package com.nike.redwiggler.core.models

import org.scalatest.{FunSpec, Matchers}

class LiteralPathComponentSpec extends FunSpec with Matchers {

  it("should match if exact equals") {
    val component = LiteralPathComponent("foo")
    component.matches("foo") should be(true)
  }

  it("should NOT match if NOT exact equals") {
    val component = LiteralPathComponent("foo")
    component.matches("bar") should be(false)
  }
}
