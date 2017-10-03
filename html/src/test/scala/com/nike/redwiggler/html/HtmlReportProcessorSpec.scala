package com.nike.redwiggler.html

import java.io.File

import com.nike.redwiggler.core.models._
import org.scalatest.{Outcome, fixture}

import collection.JavaConverters._

class HtmlReportProcessorSpec extends fixture.FunSpec {

  describe("different types of ValidationStatus") {
    for {
      result <- Seq(
        ValidationPassed(call = callA, specification = specificationA),
        ValidationFailed(call = callA, specification = specificationA),
        CallMatchedMultipleSpecifications(call = callA, specifications = Seq(specificationA)),
        CallNotMatchedBySpecification(call = callA),
        SchemaValidationFailed(
          call = callB,
          specification = specificationB,
          validationFailure = ValidationFailure(message = "failure", pointer = None, path = Seq(
            ValidationFailure(message = "failure2", pointer = None, path = Seq())
          ))
        )
      )
    } {
      it(s"should render html for $result") { fixture =>
        import fixture._

        processor.process(Seq(
          RedwigglerReport(
            verbPath = VerbPath(verb = HttpVerb.GET, path = "/foo/bar"),
            results = Seq(
              result
            )
          )
        ).asJava)
      }
    }
  }

  it("should render html") { fixture =>
    import fixture._
    processor.process(Seq(
      RedwigglerReport(
        verbPath = VerbPath(verb = HttpVerb.GET, path = "/foo/bar"),
        results = Seq(
          ValidationPassed(call = callA, specification = specificationA),
          ValidationFailed(call = callA, specification = specificationA),
          SchemaValidationFailed(
            call = callB,
            specification = specificationB,
            validationFailure = ValidationFailure(message = "failure", pointer = None, path = Seq(
              ValidationFailure(message = "failure2", pointer = None, path = Seq())
            ))
          )
        )
      )
    ).asJava)
  }

  override protected def withFixture(test: OneArgTest): Outcome = withFixture(test.toNoArgTest(new FixtureParam))

  class FixtureParam {
    private val file = File.createTempFile("redwiggler", ".html")
    file.deleteOnExit()
    val processor = new HtmlReportProcessor(file)
  }
}
