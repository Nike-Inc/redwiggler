package com.nike.redwiggler.core.models

import org.everit.json.schema.{Schema => JsSchema}

trait Schema {
  def isValid(payload : String) : Boolean
}

case class JsonSchema(schema : JsSchema) extends Schema {
  override def isValid(payload: String): Boolean = ???
}
