package com.nike.redwiggler.core

import com.nike.redwiggler.core.models.{EndpointSpecification, HttpVerb, Path}
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

class SpecificationProviderSpec extends FunSpec with Matchers {

  it("should aggregate on concat") {
    case class SeqSpecificationProvider(getEndPointSpecs: java.util.List[EndpointSpecification]) extends EndpointSpecificationProvider

    val spec1 = EndpointSpecification(
      verb = HttpVerb.GET,
      path = Path(Seq()),
      code = 200,
      responseSchema = None,
      requestSchema = None
    )
    val spec2 = EndpointSpecification(
      verb = HttpVerb.POST,
      path = Path(Seq()),
      code = 200,
      responseSchema = None,
      requestSchema = None
    )

    val endpoints = SeqSpecificationProvider(Seq(spec1).asJava).concat(SeqSpecificationProvider(Seq(spec2).asJava))

    endpoints.getEndPointSpecs.asScala contains only(
      spec1,
      spec2
    )
  }
}
