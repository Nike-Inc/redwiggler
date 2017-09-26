package com.nike.redwiggler.html

import java.io.File
import java.nio.file.Files
import java.util

import com.nike.redwiggler.core.ReportProcessor
import com.nike.redwiggler.core.models.RedwigglerReport
import collection.JavaConverters._

case class HtmlReportProcessor(path : File) extends ReportProcessor {

  import HtmlReportProcessor._
  override def process(reports: util.List[RedwigglerReport]): Unit = {
    LOGGER.info("Writing " + path.toPath)
    val html = mainReport.apply(reports.asScala)
    val report = html.body
    Files.write(path.toPath, report.getBytes("utf-8"))
    LOGGER.info("Wrote " + path.toPath)
  }

}

object HtmlReportProcessor {
  val LOGGER = org.slf4j.LoggerFactory.getLogger(getClass)
}