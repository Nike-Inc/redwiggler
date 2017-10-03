package com.nike.redwiggler.swagger

import java.util

import com.nike.redwiggler.core.EndpointSpecificationProvider
import com.nike.redwiggler.core.models.{EndpointSpecification, HttpVerb, JsonSchema, Path => RedwigglerPath}
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
        path = basePath / SwaggerPath(pathUri),
        responseSchema = Option(response.getSchema).map(resolveSchema(RootPath)).map(JsonSchema.apply),
        requestSchema = findRequestSchema(path, operation).map(JsonSchema.apply),
        verb = HttpVerb.from(method.name())
      )
    }
  }.asJava

  private lazy val basePath = if (StringUtils.isEmpty(swagger.getBasePath)) RedwigglerPath(Seq()) else RedwigglerPath(swagger.getBasePath)

  lazy val definitions : Map[String, Schema] = for {
    (name, schemaModel) <- Option(swagger.getDefinitions).map(_.asScala).toSeq.flatten.toMap
  } yield {
    name -> resolveSchemaModel(RootPath)(schemaModel)
  }

  private def findRequestSchema(path: Path, operation: Operation) = {
    Option(operation.getParameters).map(_.asScala).toSeq.flatten ++
      Option(path.getParameters).map(_.asScala).toSeq.flatten ++
      Option(swagger.getParameters).map(_.asScala.values).toSeq.flatten
  }.filter(_.getIn == "body")
    .collect {
      case bodyParameter: BodyParameter => resolveSchemaModel(RootPath)(bodyParameter.getSchema)
    }.headOption

  private def resolveSchema(path : SchemaPath)(schemaProperty: Property): Schema = schemaProperty match {
    case refProperty: RefProperty =>
      val ref = refProperty.getSimpleRef
      val model = swagger.getDefinitions.get(ref)
      resolveSchemaModel(path / schemaProperty)(model)
    case enumProperty: StringProperty if enumProperty.getEnum != null =>
      EnumSchema.builder
        .possibleValues(enumProperty.getEnum.asScala.toSet.asInstanceOf[Set[Object]].asJava)
        .description(enumProperty.getDescription)
        .title(enumProperty.getTitle)
        .build
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
        .addItemSchema(resolveSchema(path / arrayProperty)(arrayProperty.getItems))
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
        propertySchema = resolveSchema(path / objectProperty)(property)
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

  private def resolveSchemaModel(path: SchemaPath)(model: Model): Schema = model match {
    case null =>
      throw SchemaNotFoundException(path)
    case arrayModel : ArrayModel =>
      ArraySchema.builder()
        .addItemSchema(resolveSchema(path / arrayModel)(arrayModel.getItems))
      .build()
    case refModel: RefModel =>
      val ref = refModel.getSimpleRef
      val resolvedModel = swagger.getDefinitions.get(ref)
      resolveSchemaModel(path / refModel)(resolvedModel)
    case composedModel: ComposedModel =>
      CombinedSchema.builder
        .subschema(resolveSchemaModel(path / composedModel)(composedModel.getChild))
        .subschemas(composedModel.getAllOf.asScala.map(resolveSchemaModel(path / composedModel)).asJava)
        .criterion(CombinedSchema.ALL_CRITERION)
        .build
    case modelImpl: ModelImpl if modelImpl.getProperties == null && "string" == modelImpl.getType =>
      StringSchema.builder
        .formatValidator(fromFormat(modelImpl))
        .build
    case modelImpl: ModelImpl =>
      val objectSchema = ObjectSchema.builder
      for {
        (propertyName, property) <- Option(modelImpl.getProperties).map(_.asScala).getOrElse(Seq())
      } {
        objectSchema.addPropertySchema(propertyName, resolveSchema(path / propertyName)(property))
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
    case null => FormatValidator.NONE
    case "byte" => ByteFormatValidator
    case _ => FormatValidator.forFormat(model.getFormat)
  }
}

object SwaggerEndpointSpecificationProvider {
  import io.swagger.parser.SwaggerParser
  import java.io._
  import scala.io._

  def apply(file : File) : SwaggerEndpointSpecificationProvider = {
    val swaggerParser = new SwaggerParser()
    val swagger = swaggerParser.parse(Source.fromFile(file).mkString)
    SwaggerEndpointSpecificationProvider(swagger)
  }

  def apply(is: InputStream) : SwaggerEndpointSpecificationProvider = {
    val swaggerParser = new SwaggerParser()
    val swagger = swaggerParser.parse(Source.fromInputStream(is).mkString)
    SwaggerEndpointSpecificationProvider(swagger)
  }

  def apply(s : String) : SwaggerEndpointSpecificationProvider = {
    val swaggerParser = new SwaggerParser()
    val swagger = swaggerParser.parse(s)
    SwaggerEndpointSpecificationProvider(swagger)
  }
}

case class SchemaNotFoundException(path : SchemaPath) extends IllegalStateException
