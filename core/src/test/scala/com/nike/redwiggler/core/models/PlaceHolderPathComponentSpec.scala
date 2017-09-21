package com.nike.redwiggler.core.models

import org.scalatest.{FunSpec, Matchers}

class PlaceHolderPathComponentSpec extends FunSpec with Matchers {

  it("should always be true") {
    val placeholder = PlaceHolderPathComponent("foo")
    placeholder.matches("somestring") should be(true)
  }
}
