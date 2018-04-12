package com.nike.redwiggler.swagger

import io.swagger.models.{ArrayModel, ComposedModel, Model, RefModel}
import io.swagger.models.properties.{ArrayProperty, Property, RefProperty}

sealed trait SchemaPath {
  def /(path : SchemaPath) : SchemaPath = ComposedPath(this, path)

  def asString : Seq[String]
}

object SchemaPath {
  import scala.language.implicitConversions

  implicit def property2Path(property : Property) : SchemaPath = PropertyPath(property)
  implicit def modelToPath(model : Model) : SchemaPath = ModelPath(model)
  implicit def stringToPath(name : String) : SchemaPath = StringPath(name)
}

case class StringPath(name : String) extends SchemaPath {
  override def asString: Seq[String] = Seq(name)
}
case class ModelPath(model : Model) extends SchemaPath {
  override def asString: Seq[String] = Seq(model match {
    case arrayModel : ArrayModel => "array"
    case refModel : RefModel => refModel.getSimpleRef
    case composed : ComposedModel => "Composed"
    case _ => model.getClass.getName + " - " + model.getReference + " - " + model.getTitle
  })
}
case class PropertyPath(property: Property) extends SchemaPath {
  override def asString: Seq[String] = Seq(property match {
    case refProperty : RefProperty => refProperty.getSimpleRef
    case arrayProperty : ArrayProperty => "array"
    case _ => property.getType + " - " + property.getName
  })
}
case class ComposedPath(left: SchemaPath, right: SchemaPath) extends SchemaPath {
  override def asString: Seq[String] = left.asString ++ right.asString
}
case object RootPath extends SchemaPath {
  override def /(path: SchemaPath) : SchemaPath = path

  override def asString: Seq[String] = Seq()
}
