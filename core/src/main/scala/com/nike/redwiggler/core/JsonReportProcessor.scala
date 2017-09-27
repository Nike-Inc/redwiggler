package com.nike.redwiggler.core

import java.io._

import com.nike.redwiggler.core.models.{HttpVerb, RedwigglerReport}
import spray.json.DefaultJsonProtocol._
import spray.json.{JsString, JsValue, JsonFormat, JsonWriter}

case class RedwigglerReportDetails(verb : HttpVerb, path : String, passed : Int, total : Int)
object RedwigglerReportDetails {
  implicit object httpVerbFormat extends JsonFormat[HttpVerb] {
    override def read(json: JsValue): HttpVerb = HttpVerb.valueOf(json.convertTo[String])

    override def write(obj: HttpVerb): JsValue = JsString(obj.name())
  }
  implicit val format = jsonFormat4(RedwigglerReportDetails.apply)
}

case class JsonReportProcessor(file : File) extends ReportProcessor {
  override def process(reports: Seq[RedwigglerReport]): Unit = {
    val out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file)))
    out.print(implicitly[JsonWriter[Seq[RedwigglerReportDetails]]].write(reports.map(report => {
      RedwigglerReportDetails(
        verb = report.verbPath.verb,
        path = report.verbPath.path,
        total = report.total,
        passed = report.passed
      )
    })))
    out.close()
  }
}
