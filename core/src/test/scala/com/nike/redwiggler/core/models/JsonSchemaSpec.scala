package com.nike.redwiggler.core.models

import org.everit.json.schema.{ObjectSchema, StringSchema}
import org.scalatest.{FunSpec, Matchers}

class JsonSchemaSpec extends FunSpec with Matchers {

  it("should validate valid schema/body") {
    val schema = JsonSchema(ObjectSchema.builder()
      .addPropertySchema("id", StringSchema.builder().build())
      .build()
    )

    val json =
      """
        |{
        |  "id": "foo"
        |}
      """.stripMargin

    schema.validate(json) should equal(None)
  }

  it("should return failure if invalid schema/body") {
    val schema = JsonSchema(ObjectSchema.builder()
      .addPropertySchema("id", StringSchema.builder().build())
      .addRequiredProperty("id")
      .build()
    )

    val json =
      """
        |{
        |  "id2": "foo"
        |}
      """.stripMargin

    schema.validate(json) should equal(Some(ValidationFailure(
      message = "required key [id] not found",
      pointer = Some("#"),
      path = Seq()
    )))
  }

  it("should return failure if invalid json") {
    val schema = JsonSchema(ObjectSchema.builder()
      .addPropertySchema("id", StringSchema.builder().build())
      .build()
    )

    val json =
      """
        |invalid json
      """.stripMargin

    schema.validate(json) should equal(Some(ValidationFailure(
      message = "A JSONObject text must begin with '{' at 2 [character 1 line 2]",
      pointer = None,
      path = Seq()
    )))
  }
}
