package com.nike.redwiggler.html

import java.io.File
import java.nio.file.Files

import com.nike.redwiggler.core.ReportProcessor
import com.nike.redwiggler.core.models.RedwigglerReport

case class HtmlReportProcessor(path : File) extends ReportProcessor {

  import HtmlReportProcessor._
  override def process(reports: Seq[RedwigglerReport]): Unit = {
    LOGGER.info("Writing " + path.toPath)
    val html = mainReport.apply(reports)
    val report = html.body
    Files.write(path.toPath, report.getBytes("utf-8"))
    LOGGER.info("Wrote " + path.toPath)
  }

}

object HtmlReportProcessor {
  val LOGGER = org.slf4j.LoggerFactory.getLogger(getClass)
}