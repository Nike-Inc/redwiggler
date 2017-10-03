package com.nike.redwiggler.html

import org.scalatest.{Matchers, WordSpec}

class LogoTemplateSpec extends WordSpec with Matchers {

  "logo" should {
    "have ref" in {
      logo.ref should equal(logo)
    }
    "render and apply the same" in {
      logo.render() should equal(logo.apply())
    }
    "f render and apply the same" in {
      logo.f() should equal(logo.apply())
    }
  }
}
