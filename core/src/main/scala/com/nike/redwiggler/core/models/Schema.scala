package com.nike.redwiggler.core.models

import org.everit.json.schema.{ValidationException, Schema => JsSchema}
import collection.JavaConverters._
import org.json.JSONObject

trait Schema {
  def isValid(payload : String) : Boolean
}

case class JsonSchema(schema : JsSchema) extends Schema {
  override def isValid(payload: String): Boolean = try {
    schema.validate(new JSONObject(payload))
    true
  } catch {
    case ve : ValidationException =>
      ve.printStackTrace()
      ve.getCausingExceptions.asScala.foreach(_.printStackTrace())
      false
  }
}
