package com.nike.redwiggler.swagger

import java.util

import com.nike.redwiggler.core.EndpointSpecificationProvider
import com.nike.redwiggler.core.models.{EndpointSpecification, HttpVerb, JsonSchema}
import io.swagger.models._
import io.swagger.models.parameters.BodyParameter
import io.swagger.models.properties._
import org.apache.commons.lang3.StringUtils
import org.everit.json.schema._

import scala.collection.JavaConverters._
import scala.language.implicitConversions

case class SwaggerEndpointSpecificationProvider(swagger: Swagger) extends EndpointSpecificationProvider {

  def getEndPointSpecs: util.List[EndpointSpecification] = {
    for {
      paths <- Option(swagger.getPaths).toSeq
      (pathUri, path) <- paths.asScala
      (method, operation) <- path.getOperationMap.asScala
      (statusCode, response) <- operation.getResponses.asScala
    } yield {
      EndpointSpecification(
        code = Integer.parseInt(statusCode),
        path = basePath.getOrElse("") + pathUri,
        responseSchema = Option(response.getSchema).map(resolveSchema).map(JsonSchema.apply),
        requestSchema = findRequestSchema(path, operation).map(JsonSchema.apply),
        verb = HttpVerb.from(method.name())
      )
    }
  }.asJava

  private lazy val basePath = if (StringUtils.isEmpty(swagger.getBasePath)) None else Some(swagger.getBasePath)

  lazy val definitions : Map[String, Schema] = for {
    (name, schemaModel) <- Option(swagger.getDefinitions).map(_.asScala).toSeq.flatten.toMap
  } yield {
    name -> resolveSchema(schemaModel)
  }

  private def findRequestSchema(path: Path, operation: Operation) = {
    Option(operation.getParameters).map(_.asScala).toSeq.flatten ++
      Option(path.getParameters).map(_.asScala).toSeq.flatten ++
      Option(swagger.getParameters).map(_.asScala.values).toSeq.flatten
  }.filter(_.getIn == "body")
    .collect {
      case bodyParameter: BodyParameter => resolveSchema(bodyParameter.getSchema)
    }.headOption

  private def resolveSchema(schemaProperty: Property): Schema = schemaProperty match {
    case refProperty: RefProperty =>
      val ref = refProperty.get$ref.substring("#/definitions/".length)
      val model = swagger.getDefinitions.get(ref)
      resolveSchema(model)
    case stringProperty: StringProperty =>
      StringSchema.builder
        .maxLength(stringProperty.getMaxLength)
        .minLength(stringProperty.getMinLength)
        .pattern(stringProperty.getPattern)
        .formatValidator(formatValidator(stringProperty.getFormat))
        .title(stringProperty.getTitle)
        .description(stringProperty.getDescription)
        .build
    case arrayProperty: ArrayProperty =>
      ArraySchema.builder
        .maxItems(arrayProperty.getMaxItems)
        .minItems(arrayProperty.getMinItems)
        .addItemSchema(resolveSchema(arrayProperty.getItems))
        .title(arrayProperty.getTitle)
        .description(arrayProperty.getDescription)
        .build
    case abstractNumericProperty: AbstractNumericProperty =>
      val isInteger = BaseIntegerProperty.TYPE == abstractNumericProperty.getType
      NumberSchema.builder
        // do an equality check with Boolean.TRUE since getExclusive{Maximum|Minimum} might be null
        .exclusiveMaximum(java.lang.Boolean.TRUE == abstractNumericProperty.getExclusiveMaximum)
        .exclusiveMinimum(java.lang.Boolean.TRUE == abstractNumericProperty.getExclusiveMinimum)
        .maximum(abstractNumericProperty.getMaximum)
        .minimum(abstractNumericProperty.getMinimum)
        .requiresNumber(!isInteger)
        .requiresInteger(isInteger)
        .description(abstractNumericProperty.getDescription)
        .title(abstractNumericProperty.getTitle)
        .build
    case booleanProperty: BooleanProperty =>
      BooleanSchema.builder
        .description(booleanProperty.getDescription)
        .title(booleanProperty.getTitle)
        .build
    case objectProperty: ObjectProperty =>
      val objectSchema = ObjectSchema.builder
      for {
        (propertyName, property) <- objectProperty.getProperties.asScala
        propertySchema = resolveSchema(property)
      } {
        objectSchema.addPropertySchema(propertyName, propertySchema)
      }
      objectSchema
        .description(objectProperty.getDescription)
        .title(objectProperty.getTitle)
        .build
    case _ =>
      throw new IllegalStateException("Unsupported schema property type: " + schemaProperty.getClass)
  }

  private def formatValidator(format: String) = Option(format).map(FormatValidator.forFormat).getOrElse(FormatValidator.NONE)

  private def resolveSchema(model: Model): Schema = model match {
    case arrayModel: ArrayModel =>
      val arrayModel = model.asInstanceOf[ArrayModel]
      ArraySchema.builder
        .addItemSchema(resolveSchema(arrayModel.getItems))
        .build
    case refModel: RefModel =>
      val ref = refModel.get$ref.substring("#/definitions/".length)
      val resolvedModel = swagger.getDefinitions.get(ref)
      resolveSchema(resolvedModel)
    case composedModel: ComposedModel =>
      CombinedSchema.builder
        .subschema(resolveSchema(composedModel.getChild))
        .subschemas(composedModel.getAllOf.asScala.map(resolveSchema).asJava)
        .criterion(CombinedSchema.ALL_CRITERION)
        .build
    case modelImpl: ModelImpl if modelImpl.getEnum != null =>
      EnumSchema.builder
        .possibleValues(modelImpl.getEnum.asScala.toSet.asInstanceOf[Set[Object]].asJava)
        .description(modelImpl.getDescription)
        .title(modelImpl.getTitle)
        .build
    case modelImpl: ModelImpl if modelImpl.getProperties == null && "string" == modelImpl.getType =>
      StringSchema.builder
        .formatValidator(fromFormat(modelImpl))
        .build
    case modelImpl: ModelImpl if !modelImpl.getProperties.isEmpty =>
      val objectSchema = ObjectSchema.builder
      for {
        (propertyName, property) <- modelImpl.getProperties.asScala
      } {
        objectSchema.addPropertySchema(propertyName, resolveSchema(property))
      }
      Option(modelImpl.getRequired).map(_.asScala).toSeq.flatten.foreach(objectSchema.addRequiredProperty)
      objectSchema
        .title(modelImpl.getTitle)
        .description(modelImpl.getDescription)
        .build()
    case _ =>
      throw new IllegalStateException("Unsuppored model type: " + model.getClass)
  }

  private def fromFormat(model: ModelImpl) = model.getFormat match {
    case "byte" => ByteFormatValidator
    case _ => FormatValidator.forFormat(model.getFormat)
  }
}
