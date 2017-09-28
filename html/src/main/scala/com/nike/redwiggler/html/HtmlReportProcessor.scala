package com.nike.redwiggler.html

import java.io.File
import java.nio.file.Files

import com.nike.redwiggler.core.ReportProcessor
import com.nike.redwiggler.core.models.RedwigglerReport

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("HtmlReportProcessor")
case class HtmlReportProcessor(fileWriter : FileWriter) extends ReportProcessor {

  override def process(reports: Seq[RedwigglerReport]): Unit = {
    println("Writing " + fileWriter)
    val html = mainReport.apply(reports)
    fileWriter.write(html.body)
    println("Wrote " + fileWriter)
  }

  @JSExport("process")
  def jsProcess(reports: js.Array[RedwigglerReport]): Unit = {
    println("processing")
    process(reports.toArray.toSeq)
  }

}

trait FileWriter {
  def write(html : String)
}

@JSExportTopLevel("FunctionFileWriter")
case class FunctionFileWriter(fn : js.Function1[String, Unit]) extends FileWriter {
  override def write(html: String): Unit = {
    fn(html)
  }
}

object FileWriter {
  implicit def file2FileWriter(file : File) : FileWriter = FileFileWriter(file)
}

case class FileFileWriter(path : File) extends FileWriter {
  override def write(html: String): Unit = {
    Files.write(path.toPath, html.getBytes("UTF-8"))
  }
}
