package com.nike.redwiggler.swagger

import java.io.InputStream

import com.nike.redwiggler.core.models.HttpVerb._
import com.nike.redwiggler.core.models.{EndpointSpecification, JsonSchema, Path}
import org.everit.json.schema._
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

class SwaggerEndpointSpecificationProviderSpec extends FunSpec with Matchers {

  private def loadEndpoints(is: InputStream) = {
    SwaggerEndpointSpecificationProvider(is)
      .getEndPointSpecs
      .asScala
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
      .addPropertySchema("documentType", EnumSchema.builder()
        .possibleValues(Set[AnyRef]("TYPE1", "TYPE2").asJava)
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
      .addPropertySchema("documentType", EnumSchema.builder()
        .possibleValues(Set[AnyRef]("TYPE1", "TYPE2").asJava)
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
    val provider = SwaggerEndpointSpecificationProvider(getClass.getResourceAsStream("property_parsing.swagger.yaml"))

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

  it("parses enum types") {
    val endpoints = loadEndpoints(getClass.getResourceAsStream("enum_parsing.swagger.yaml"))

    val schema = ObjectSchema.builder()
      .addPropertySchema("myEnum", EnumSchema.builder()
        .possibleValues(Set[AnyRef]("foo", "bar", "baz").asJava)
        .build())
      .build()

    val endpointSpecification = EndpointSpecification(
      verb = GET,
      path = Path(),
      code = 200,
      responseSchema = Some(JsonSchema(schema)),
      requestSchema = None
    )

    endpoints should equal(Seq(endpointSpecification))
  }

  it("parses array types") {
    val endpoints = loadEndpoints(getClass.getResourceAsStream("array_parsing.swagger.yaml"))

    val schema = ObjectSchema.builder()
      .addPropertySchema("myArray", ArraySchema.builder()
        .addItemSchema(StringSchema.builder().build())
        .build())
      .build()

    val endpointSpecification = EndpointSpecification(
      verb = GET,
      path = Path(),
      code = 200,
      responseSchema = Some(JsonSchema(schema)),
      requestSchema = None
    )

    endpoints should equal(Seq(endpointSpecification))
  }

  it("parses allOf types") {
    val endpoints = loadEndpoints(getClass.getResourceAsStream("allOf_parsing.swagger.yaml"))

    val schema = CombinedSchema.builder()
      .criterion(CombinedSchema.ALL_CRITERION)
      .subschemas(Seq[Schema](
        ObjectSchema.builder()
          .addPropertySchema("baz", StringSchema.builder().build())
        .build(),
        ObjectSchema.builder()
          .addPropertySchema("bar", StringSchema.builder().build())
          .build()
      ).asJava)
      .build()

    val endpointSpecification = EndpointSpecification(
      verb = GET,
      path = Path(),
      code = 200,
      responseSchema = Some(JsonSchema(schema)),
      requestSchema = None
    )

    endpoints should equal(Seq(endpointSpecification))
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
