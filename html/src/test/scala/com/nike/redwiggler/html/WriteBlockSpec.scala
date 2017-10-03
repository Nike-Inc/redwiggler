package com.nike.redwiggler.html

import org.scalatest.{Matchers, WordSpec}

class WriteBlockSpec extends WordSpec with Matchers {

  "writeBlock" should {
    "have ref" in {
      writeBlock.ref should equal(writeBlock)
    }
    "render and apply the same" in {
      writeBlock.render(Seq()) should equal(
        writeBlock.apply(Seq())
      )
    }
    "f render and apply the same" in {
      writeBlock.f(Seq()) should equal(
        writeBlock.apply(Seq())
      )
    }
  }
}
