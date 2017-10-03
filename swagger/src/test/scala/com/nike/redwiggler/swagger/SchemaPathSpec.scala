package com.nike.redwiggler.swagger

import io.swagger.models.properties.{ArrayProperty, ObjectProperty, RefProperty}
import io.swagger.models.{ArrayModel, ModelImpl}
import org.everit.json.schema.ArraySchema
import org.scalatest.{FunSpec, Matchers}

class SchemaPathSpec extends FunSpec with Matchers {

  describe("schemaPath") {
    it("should concatenate") {
      val arrayModel = new ArrayModel
      val refProperty = new RefProperty()
      refProperty.set$ref("#/definitions/HelloWorld")
      val path = RootPath / "foo" / arrayModel / refProperty

      path.asString should equal(Seq("foo", "array", "HelloWorld"))
    }
  }
  describe("modelPath") {
    it("should have name for ArrayModel") {
      val arrayModel = new ArrayModel
      arrayModel.setTitle("foobar")
      val modelPath = ModelPath(arrayModel)
      modelPath.asString should equal(Seq("array"))
    }
    it("should have name for ModelImpl") {
      val modelImpl = new ModelImpl
      modelImpl.setTitle("foobar")
      val modelPath = ModelPath(modelImpl)
      modelPath.asString should equal(Seq("foobar"))
    }
  }

  describe("propertyPath") {
    it("should have name for ArrayProperty") {
      val arrayProperty = new ArrayProperty
      arrayProperty.setTitle("foobar")
      val propertyPath = PropertyPath(arrayProperty)
      propertyPath.asString should equal(Seq("array"))
    }
    it("should have name for RefProperty") {
      val refProperty = new RefProperty()
      refProperty.setTitle("foobar")
      refProperty.set$ref("#/definitions/HelloWorld")
      val propertyPath = PropertyPath(refProperty)
      propertyPath.asString should equal(Seq("HelloWorld"))
    }
    it("should have name for ObjectProperty") {
      val objectProperty = new ObjectProperty()
      objectProperty.setTitle("foobar")
      val propertyPath = PropertyPath(objectProperty)
      propertyPath.asString should equal(Seq("foobar"))
    }
  }
}
