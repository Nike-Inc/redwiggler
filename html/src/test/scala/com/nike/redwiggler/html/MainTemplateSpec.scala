package com.nike.redwiggler.html

import org.scalatest.{FunSpec, Matchers, WordSpec}

class MainTemplateSpec extends WordSpec with Matchers {

  "main" should {
    "have ref" in {
      mainReport.ref should equal(mainReport)
    }
    "render and apply the same" in {
      mainReport.render(Seq()) should equal(mainReport.apply(Seq()))
    }
    "f render and apply the same" in {
      mainReport.f(Seq()) should equal(mainReport.apply(Seq()))
    }
  }
}
