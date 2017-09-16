package com.nike.redwiggler.restassured

import java.io.File
import java.util.UUID

import com.jayway.restassured.filter.{Filter, FilterContext}
import com.jayway.restassured.response.{Response, ValidatableResponse}
import com.jayway.restassured.specification.{FilterableRequestSpecification, FilterableResponseSpecification}
import com.nike.redwiggler.core.models.{EndpointCall, HttpVerb}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

case class RedwigglerLoggingFilter(outputDir: File) extends Filter {

  import RedwigglerLoggingFilter.LOGGER

  LOGGER.info("Writing RedWiggler logs to {}", outputDir)
  if (outputDir.mkdirs) {
    LOGGER.info("Created output directory {}", outputDir)
  }

  override def filter(requestSpec: FilterableRequestSpecification, responseSpec: FilterableResponseSpecification, ctx: FilterContext): Response = {
    val response = ctx.next(requestSpec, responseSpec)
    val index = UUID.randomUUID
    LOGGER.info(s"Logging request index=$index method=${requestSpec.getMethod} path=${requestSpec.getDerivedPath} statusCode=${response.getStatusCode}")
    val body = Option(requestSpec.getBody)
    recordCall(index, outputDir, response.then, requestSpec.getMethod.name, requestSpec.getDerivedPath, body)
    response
  }

  private def writeCall(index: UUID, dir: File, call: EndpointCall) = {
    if (dir.mkdirs) LOGGER.info("Created output directory {}", dir)
    val file = new File(dir, "response-" + index + ".json")
    EndpointCall.toFile(file, call)
  }

  def recordCall(index: UUID, dir: File, response: ValidatableResponse, verb: String, path: String, requestBody: Option[String]): Unit = {
    val ec = EndpointCall(
      code = response.extract().statusCode(),
      path = path,
      requestBody = requestBody,
      responseBody = Option(response.extract().body.asString()),
      verb = HttpVerb.from(verb),
      responseHeaders = response.extract().headers().asList().asScala.map(header => header.getName -> header.getValue)
    )
    writeCall(index, dir, ec)
  }
}

object RedwigglerLoggingFilter {
  private val LOGGER = LoggerFactory.getLogger(getClass)
}
