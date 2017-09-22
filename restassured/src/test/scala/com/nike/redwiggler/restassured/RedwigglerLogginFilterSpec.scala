package com.nike.redwiggler.restassured

import java.io.File

import com.jayway.restassured.filter.{Filter, FilterContext}
import com.jayway.restassured.http.Method
import com.jayway.restassured.response._
import com.jayway.restassured.specification.{FilterableRequestSpecification, FilterableResponseSpecification, RequestSpecification, ResponseSpecification}
import com.nike.redwiggler.core.models.{EndpointCall, HttpVerb}
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, Outcome, fixture}
import spray.json.JsonParser

import scala.io.Source

class RedwigglerLogginFilterSpec extends fixture.FunSpec with MockitoSugar with Matchers {
  override protected def withFixture(test: OneArgTest): Outcome = withFixture(test.toNoArgTest(new FixtureParam))

  class FixtureParam {
    private val dir = File.createTempFile("redwiggler", "restassured")
    dir.delete()

    val filter : Filter = new RedwigglerLoggingFilter(dir)

    def files = dir.listFiles().toSeq

    def recordedCalls = files.map(file => JsonParser(Source.fromFile(file).mkString).convertTo[EndpointCall])
  }

  it("should record a response") { fixture =>
    val ctx = mock[FilterContext]
    val requestSpec = mock[FilterableRequestSpecification]
    val responseSpec = mock[FilterableResponseSpecification]
    val response = mock[Response]
    val validatableResponse = mock[ValidatableResponse]
    val extractableResponse = mock[ExtractableResponse[Response]]

    Mockito.when(ctx.next(requestSpec, responseSpec)).thenReturn(response)
    Mockito.when(response.then()).thenReturn(validatableResponse)
    Mockito.when(validatableResponse.extract()).thenReturn(extractableResponse)

    Mockito.when(requestSpec.getMethod).thenReturn(Method.DELETE)
    Mockito.when(requestSpec.getDerivedPath).thenReturn("/foo/bar")

    val responseBodyExtractionOptions = mock[ResponseBodyExtractionOptions]
    Mockito.when(extractableResponse.body()).thenReturn(responseBodyExtractionOptions)

    Mockito.when(extractableResponse.headers()).thenReturn(new Headers())
    Mockito.when(extractableResponse.statusCode()).thenReturn(207)
    Mockito.when(responseBodyExtractionOptions.asString()).thenReturn("content body")

    fixture.filter.filter(requestSpec, responseSpec, ctx)

    fixture.recordedCalls should contain only (
      EndpointCall(
        verb = HttpVerb.DELETE,
        responseBody = Some("content body"),
        path = "/foo/bar",
        responseHeaders = Seq(),
        requestBody = None,
        code = 207
      )
    )
  }
}
