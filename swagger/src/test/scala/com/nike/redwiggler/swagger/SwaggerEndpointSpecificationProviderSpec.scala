package com.nike.redwiggler.swagger

import java.io.InputStream

import com.nike.redwiggler.core.models.HttpVerb._
import com.nike.redwiggler.core.models.{EndpointSpecification, JsonSchema, Path}
import io.swagger.parser.SwaggerParser
import org.everit.json.schema._
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._
import scala.io.Source

class SwaggerEndpointSpecificationProviderSpec extends FunSpec with Matchers {

  private def loadEndpoints(is: InputStream) = {
    val swaggerParser = new SwaggerParser()
    val swagger = swaggerParser.parse(Source.fromInputStream(is).mkString)
    SwaggerEndpointSpecificationProvider(swagger)
      .getEndPointSpecs
      .asScala
  }

  private def loadProvider(is: InputStream) = {
    val swaggerParser = new SwaggerParser()
    val swagger = swaggerParser.parse(Source.fromInputStream(is).mkString)
    SwaggerEndpointSpecificationProvider(swagger)
  }

  it("should get single definition") {
    val endpoints = loadEndpoints(getClass.getResourceAsStream("schema_parsing.swagger.yaml"))

    val schema = ObjectSchema.builder()
      .addPropertySchema("object", StringSchema.builder().build())
      .build()

    val endpointSpecification = EndpointSpecification(
      verb = POST,
      path = Path("/object"),
      code = 204,
      requestSchema = Some(JsonSchema(schema)),
      responseSchema = None
    )

    endpoints should equal(Seq(endpointSpecification))
  }

  it("example1") {
    val endpoints = loadEndpoints(getClass.getResourceAsStream("example1.swagger.yaml"))

    val postCreateRequestSchema = ObjectSchema.builder()
      .addPropertySchema("foo", StringSchema.builder()
        .description("foobar")
        .build()
      )
      .addPropertySchema("documentType", StringSchema.builder()
        .description("The type of the document")
        .build()
      )
      .addRequiredProperty("documentType")
      .build()

    val itemSchema = ObjectSchema.builder()
      .addPropertySchema("documentCreationDate", StringSchema.builder()
        .formatValidator(FormatValidator.NONE)
        .description("ISO 8601 Date")
        .build()
      )
      .addPropertySchema("documentType", StringSchema.builder()
        .formatValidator(FormatValidator.NONE)
        .description("The type of the document")
        .build()
      )
      .addPropertySchema("id", StringSchema.builder()
        .pattern("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$")
        .description("globally Unique id")
        .build()
      )
      .addPropertySchema("required", BooleanSchema.builder()
        .build()
      )
      .build()

    val postCreate = EndpointSpecification(
      path = Path("/my/resource/v2"),
      verb = POST,
      code = 201,
      responseSchema = Some(JsonSchema(itemSchema)),
      requestSchema = Some(JsonSchema(postCreateRequestSchema))
    )

    val postCreate401 = EndpointSpecification(
      verb = POST,
      path = postCreate.path,
      code = 401,
      requestSchema = Some(JsonSchema(postCreateRequestSchema)),
      responseSchema = None
    )

    val search = EndpointSpecification(
      verb = GET,
      path = postCreate.path,
      code = 200,
      requestSchema = None,
      responseSchema = Some(JsonSchema(
        ObjectSchema.builder()
          .addPropertySchema("pages", StringSchema.builder()
            .build()
          )
          .build()
      )
      )
    )

    val itemGetSuccess = EndpointSpecification(
      verb = GET,
      path = SwaggerPath("/my/resource/v2/{id}"),
      code = 200,
      requestSchema = None,
      responseSchema = Some(JsonSchema(itemSchema))
    )

    val itemGetFailure = EndpointSpecification(
      verb = GET,
      path = SwaggerPath("/my/resource/v2/{id}"),
      code = 401,
      requestSchema = None,
      responseSchema = None
    )

    endpoints should contain only(
      postCreate,
      postCreate401,
      search,
      itemGetSuccess,
      itemGetFailure
    )
  }

  describe("basePath") {
    describe("missingBasePath") {
      val endpoints = loadEndpoints(getClass.getResourceAsStream("missing_base_path.swagger.yaml"))

      for {
        endpoint <- endpoints
      } {
        it(endpoint.toString) {
          val path = endpoint.path
          path should equal(Path("/"))
        }
      }
    }

    describe("hasBasePath") {
      val endpoints = loadEndpoints(getClass.getResourceAsStream("schema_parsing.swagger.yaml"))

      for {
        endpoint <- endpoints
      } {
        it(endpoint.toString) {
          val path = endpoint.path
          path should equal(Path("/object"))
        }
      }
    }
  }

  it("propertyParsing") {
    val provider = loadProvider(getClass.getResourceAsStream("property_parsing.swagger.yaml"))

    val expected = ObjectSchema.builder()
      .addPropertySchema("foo", NumberSchema.builder()
        .requiresInteger(true)
        .requiresNumber(false)
        .build()
      )
      .addPropertySchema("bar", NumberSchema.builder()
        .requiresInteger(false)
        .requiresNumber(true)
        .build()
      )
      .addPropertySchema("baz", ObjectSchema.builder()
        .addPropertySchema("foo", StringSchema.builder().build())
        .build()
      )
      .build()

    provider.definitions should equal(Map("GetResponse" -> expected))
  }

  it("byteInputOperation") {
    val endpoints = loadEndpoints(getClass.getResourceAsStream("byteInputOperation.yaml"))

    val schema = StringSchema.builder()
      .formatValidator(ByteFormatValidator)
      .build()

    val endpointSpecification = EndpointSpecification(
      verb = POST,
      path = SwaggerPath("/mypath"),
      code = 204,
      requestSchema = Some(JsonSchema(schema)),
      responseSchema = None
    )

    endpoints should equal(Seq(endpointSpecification))
  }
}
