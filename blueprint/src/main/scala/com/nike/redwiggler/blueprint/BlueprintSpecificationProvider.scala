package com.nike.redwiggler.blueprint

import java.util

import com.nike.redwiggler.blueprint.parser.BlueprintParser
import com.nike.redwiggler.core.EndpointSpecificationProvider
import com.nike.redwiggler.core.models._

import collection.JavaConverters._

case class BlueprintSpecificationProvider(blueprint : String, blueprintParser: BlueprintParser) extends EndpointSpecificationProvider {

  private lazy val ast = blueprintParser.parse(blueprint)

  override def getEndPointSpecs: util.List[EndpointSpecification] = (for {
    resourceGroup <- ast.resourceGroups
    resource <- resourceGroup.resources
    action <- resource.actions
    example <- action.examples
    response <- example.responses
  } yield {
    EndpointSpecification(
      verb = HttpVerb.from(action.method),
      path = BlueprintPathParser(action.attributes.uriTemplate),
      code = Integer.parseInt(response.name),
      responseSchema = Option(response.schema).flatMap(parseSchema),
      requestSchema = example.requests.headOption.map(_.schema).flatMap(parseSchema)
    )
  }).asJava

  private def parseSchema(schema : String) = if (schema.trim.isEmpty) {
    None
  } else {
    import org.everit.json.schema.loader.SchemaLoader
    import org.json.JSONObject
    import org.json.JSONTokener
    val rawSchema = new JSONObject(new JSONTokener(schema))
    Some(JsonSchema(SchemaLoader.load(rawSchema)))
  }
}
