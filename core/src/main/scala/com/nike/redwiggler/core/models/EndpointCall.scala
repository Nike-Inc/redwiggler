package com.nike.redwiggler.core.models

import java.io.File
import java.nio.file.Files

import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, JsonParser}

import scala.io.Source

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
    override def read(json: JsValue): HttpVerb = HttpVerb.valueOf(json.convertTo[String])

    override def write(obj: HttpVerb): JsValue = JsString(obj.name())
  }
  implicit val format = jsonFormat6(EndpointCall.apply)

  def fromFile(file: File) : EndpointCall = JsonParser(Source.fromFile(file).mkString).convertTo[EndpointCall]
  def toFile(file: File, endpointCall : EndpointCall) : Unit = {
    Files.write(file.toPath, format.write(endpointCall).prettyPrint.getBytes("UTF-8"))
    JsonParser(Source.fromFile(file).mkString).convertTo[EndpointCall]
  }
}
