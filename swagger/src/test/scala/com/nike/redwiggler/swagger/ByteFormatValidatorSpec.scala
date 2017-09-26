package com.nike.redwiggler.swagger

import java.util.Optional

import org.scalatest.{FunSpec, Matchers}

class ByteFormatValidatorSpec extends FunSpec with Matchers {

  it("shouldn't do any validations") {
    ByteFormatValidator.validate("foo") should equal(Optional.empty())
  }

  it("should have name binary") {
    ByteFormatValidator.formatName should equal("binary")
  }
}
