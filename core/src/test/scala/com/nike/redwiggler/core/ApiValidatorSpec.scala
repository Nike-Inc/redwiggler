package com.nike.redwiggler.core

import com.nike.redwiggler.core.models._
import org.scalatest.{FunSpec, Matchers}

class ApiValidatorSpec extends FunSpec with Matchers {

  val specificationA = EndpointSpecification(
    verb = HttpVerb.DELETE,
    path = Path("/foo/bar"),
    code = 200,
    responseSchema = None,
    requestSchema = None
  )

  val specificationB = EndpointSpecification(
    verb = HttpVerb.GET,
    path = Path("/foo/bar"),
    code = 200,
    responseSchema = None,
    requestSchema = None
  )

  val callA = EndpointCall(
    verb = specificationA.verb,
    responseBody = None,
    path = specificationA.path.asString,
    responseHeaders = Seq(),
    requestBody = None,
    code = 200
  )

  val callC = EndpointCall(
    verb = HttpVerb.GET,
    responseBody = None,
    path = "anotherpath",
    responseHeaders = Seq(),
    requestBody = None,
    code = 200
  )

  describe("matching") {
    it("should validate single matching call/specification") {
      val specification = specificationA.copy(responseSchema = Some(AllValidSchema))
      val call = callA.copy(responseBody = Some("foo"))
      val statuses = ApiValidator(
        specifications = Seq(specification),
        calls = Seq(call)
      )
      statuses should contain only ValidationPassed(call = call, specification = specification)
    }
    it("should validate multiple matching call/specification") {
      case object TestSchema extends Schema {
        override def validate(payload: String): Option[ValidationFailure] = if (payload == "foo") {
          None
        } else {
          Some(ValidationFailure(pointer = "foo", message = "bar", path = Seq()))
        }
      }
      val specification = specificationA.copy(responseSchema = Some(TestSchema))
      val call1 = callA.copy(responseBody = Some("foo"))
      val call2 = callA.copy(responseBody = Some("bar"))
      val statuses = ApiValidator(
        specifications = Seq(specification),
        calls = Seq(call1, call2)
      )
      statuses should contain only(
        ValidationPassed(call = call1, specification = specification),
        SchemaValidationFailed(call = call2, specification = specification, validationFailure = ValidationFailure(pointer = "foo", message = "bar", path = Seq()))
      )
    }
    it("should fail validation with single matching call/specification but invalid schema") {
      val specification = specificationA.copy(responseSchema = Some(AllInvalidSchema))
      val call = callA.copy(responseBody = Some("foo"))
      val statuses = ApiValidator(
        specifications = Seq(specification),
        calls = Seq(call)
      )
      statuses should contain only SchemaValidationFailed(call = call, specification = specification, ValidationFailure(pointer = "pointer", message = "foobar", path = Seq()))
    }
  }

  it("should validate specification with no calls") {
    val statuses = ApiValidator(Seq(specificationA), Seq())
    statuses should contain only UntestedSpecification(specificationA)
  }

  it("should validate call with no specification") {
    val statuses = ApiValidator(Seq(), Seq(callA))
    statuses should contain only CallNotMatchedBySpecification(callA)
  }

  describe("multiple") {
    it("should validate all non matching") {
      val statuses = ApiValidator(Seq(specificationA), Seq(callC))
      statuses should contain only(
        UntestedSpecification(specificationA),
        CallNotMatchedBySpecification(callC)
      )
    }
  }
}
