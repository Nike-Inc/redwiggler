package com.nike.redwiggler.blueprint

import spray.json.DefaultJsonProtocol

case class Action(attributes: Attributes, method : String, examples : Seq[Example], parameters : Seq[Parameter])
case class Ast(name : String, description: String, resourceGroups : Seq[ResourceGroup])
case class AstHolder(ast : Ast)
case class Attributes(uriTemplate : String)
case class Example(name : String, requests : Seq[Request], responses : Seq[Response])
case class Parameter(name : String, `type` : String, required : Boolean)
case class Request(schema : String)
case class Resource(uriTemplate : String, actions : Seq[Action])
case class ResourceGroup(resources : Seq[Resource])
case class Response(name : String, schema : String)


trait AstProtocol extends DefaultJsonProtocol {
  implicit val response = jsonFormat2(Response.apply)
  implicit val request = jsonFormat1(Request.apply)
  implicit val parameter = jsonFormat3(Parameter.apply)
  implicit val example = jsonFormat3(Example.apply)
  implicit val attributes = jsonFormat1(Attributes.apply)
  implicit val action = jsonFormat4(Action.apply)
  implicit val resource = jsonFormat2(Resource.apply)
  implicit val resourceGroup = jsonFormat1(ResourceGroup.apply)
  implicit val ast = jsonFormat3(Ast.apply)
  implicit val astHolder = jsonFormat1(AstHolder.apply)
}