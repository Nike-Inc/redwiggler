package com.nike.redwiggler.core.models

import java.io.File
import java.nio.file.Files

import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, JsonParser}

import scala.io.Source
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("EndpointCall")
case class EndpointCall(
                         verb: HttpVerb,
                         responseBody: Option[String],
                         path: String,
                         responseHeaders: Seq[(String, String)],
                         requestBody: Option[String],
                         code: Int
                       )

object EndpointCall extends DefaultJsonProtocol {
  implicit object httpVerbFormat extends JsonFormat[HttpVerb] {
    override def read(json: JsValue): HttpVerb = HttpVerb.from(json.convertTo[String])

    override def write(obj: HttpVerb): JsValue = JsString(obj.name)
  }
  implicit val format = jsonFormat6(EndpointCall.apply)

  def fromFile(file: File) : EndpointCall = JsonParser(Source.fromFile(file).mkString).convertTo[EndpointCall]
  def toFile(file: File, endpointCall : EndpointCall) : Unit = {
    Files.write(file.toPath, format.write(endpointCall).prettyPrint.getBytes("UTF-8"))
  }
}

@JSExportTopLevel("EndpointCallParser")
object EndpointCallParser {
  @JSExport("fromObject")
  def fromJsObject(obj : js.Dynamic) : EndpointCall = EndpointCall(
    verb = HttpVerb.from(obj.verb.asInstanceOf[String]),
    responseBody = Option(obj.responseBody.asInstanceOf[String]),
    path = obj.path.asInstanceOf[String],
    responseHeaders = Seq(),
    requestBody = Option(obj.requestBody.asInstanceOf[String]),
    code = obj.code.asInstanceOf[Int]
  )
}
