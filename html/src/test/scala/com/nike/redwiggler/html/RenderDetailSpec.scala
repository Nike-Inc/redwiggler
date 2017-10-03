package com.nike.redwiggler.html

import com.nike.redwiggler.core.models.ValidationPassed
import org.scalatest.{Matchers, WordSpec}

class RenderDetailSpec extends WordSpec with Matchers {

  "renderDetail" should {
    "have ref" in {
      renderDetail.ref should equal(renderDetail)
    }
    "render and apply the same" in {
      renderDetail.render(3, Map(200 -> Seq(ValidationPassed(callA, specificationA)))) should equal(
        renderDetail.apply(3, Map(200 -> Seq(ValidationPassed(callA, specificationA))))
      )
    }
    "f render and apply the same" in {
      renderDetail.f(3, Map(201 -> Seq(ValidationPassed(callA, specificationA)))) should equal(
        renderDetail.apply(3, Map(201 -> Seq(ValidationPassed(callA, specificationA))))
      )
    }
  }
}
