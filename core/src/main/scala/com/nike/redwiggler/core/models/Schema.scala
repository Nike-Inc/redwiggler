package com.nike.redwiggler.core.models

import org.everit.json.schema.{ValidationException, Schema => JsSchema}
import collection.JavaConverters._
import org.json.JSONObject

trait Schema {
  def validate(payload : String) : Option[ValidationFailure]
}

case class JsonSchema(schema : JsSchema) extends Schema {
  override def validate(payload: String): Option[ValidationFailure] = try {
    schema.validate(new JSONObject(payload))
    None
  } catch {
    case ve : ValidationException => Some(validationException2ValidationFailure(ve))
  }

  def validationException2ValidationFailure(cause : ValidationException) : ValidationFailure = ValidationFailure(
    message = cause.getErrorMessage,
    pointer = cause.getPointerToViolation,
    path = cause.getCausingExceptions.asScala.map(validationException2ValidationFailure)
  )
}

