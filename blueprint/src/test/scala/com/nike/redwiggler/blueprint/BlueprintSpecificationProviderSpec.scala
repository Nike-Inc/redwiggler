package com.nike.redwiggler.blueprint

import com.nike.redwiggler.blueprint.parser.{DrafterBlueprintParser, ProtagonistBlueprintParser}
import com.nike.redwiggler.core.models._
import org.everit.json.schema.{ArraySchema, ObjectSchema, StringSchema}
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source
import collection.JavaConverters._

class BlueprintSpecificationProviderSpec extends FunSpec with Matchers {

  for {
    parser <- Seq(
      ProtagonistBlueprintParser(),
      DrafterBlueprintParser
    )
  } {
    describe(parser.name) {
      it("parses simple md") {
        val blueprint = Source.fromInputStream(getClass.getResourceAsStream("test1.md")).mkString
        val provider = BlueprintSpecificationProvider(blueprint, parser)
        val specs = provider.getEndPointSpecs.asScala

        specs should contain only(
          EndpointSpecification(
            verb = HttpVerb.GET,
            path = Path(Seq(LiteralPathComponent("my"), LiteralPathComponent("api"), LiteralPathComponent("v1"))),
            code = 200,
            responseSchema = Some(JsonSchema(
              ObjectSchema.builder()
                .addPropertySchema("pages", ObjectSchema.builder()
                  .addPropertySchema("next", StringSchema.builder().build())
                  .addRequiredProperty("next")
                  .build()
                )
                .addPropertySchema("objects", ArraySchema.builder()
                  .allItemSchema(ObjectSchema.builder()
                    .addPropertySchema("foo", StringSchema.builder().build())
                    .addPropertySchema("id", StringSchema.builder().build())
                    .addRequiredProperty("id")
                    .build()
                  )
                  .build()
                )
                .addRequiredProperty("pages")
                .addRequiredProperty("objects")
                .build()
            )),
            requestSchema = None
          ),
          EndpointSpecification(
            verb = HttpVerb.GET,
            path = Path(Seq(LiteralPathComponent("my"), LiteralPathComponent("api"), LiteralPathComponent("v1"))),
            code = 304,
            responseSchema = None,
            requestSchema = None
          )
        )
      }
    }
  }
}
