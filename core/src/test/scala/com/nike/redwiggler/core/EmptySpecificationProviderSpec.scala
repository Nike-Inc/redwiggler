package com.nike.redwiggler.core

import org.scalatest.{FunSpec, Matchers}

class EmptySpecificationProviderSpec extends FunSpec with Matchers {

  it("should return an empty list") {
    EmptySpecificationProvider.getEndPointSpecs.isEmpty should be(true)
  }
}
