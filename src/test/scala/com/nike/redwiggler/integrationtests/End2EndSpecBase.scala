package com.nike.redwiggler.integrationtests


import java.util

import com.nike.redwiggler.core._
import com.nike.redwiggler.core.models.RedwigglerReport
import com.nike.redwiggler.html.HtmlReportProcessor
import org.scalatest.{FunSpec, Matchers}
import spray.json.{DefaultJsonProtocol, JsonParser}

import scala.io.Source

abstract class End2EndSpecBase(name : String) extends FunSpec with Matchers with DefaultJsonProtocol {

  import java.io._

  private val testDirs = new File(getClass.getResource(name).getFile).listFiles()

  def specificationProvider(testDir: File): EndpointSpecificationProvider

  for {
    testDir <- testDirs
  } {
    it(s"should process ${testDir.getName}") {
      Redwiggler(
        callProvider = new GlobEndpointCallProvider(testDir, "requests.*.json"),
        specificationProvider = specificationProvider(testDir),
        reportProcessor = new ReportProcessor {
          val file = File.createTempFile("redwiggler", ".json")
          val writer = JsonReportProcessor(file)

          override def process(reports: util.List[RedwigglerReport]): Unit = {
            writer.process(reports)

            val expected = JsonParser(Source.fromFile(new File(testDir, "expected.json")).mkString).convertTo[Seq[RedwigglerReportDetails]]
            val actual = JsonParser(Source.fromFile(file).mkString).convertTo[Seq[RedwigglerReportDetails]]

            expected should contain theSameElementsAs actual
          }
        }.andThen(HtmlReportProcessor(new File("/tmp/" + testDir.getName + ".html")))
      )
    }
  }

}
