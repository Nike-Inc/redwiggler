package com.nike.redwiggler.core

import com.nike.redwiggler.core.models.{EndpointCall, EndpointSpecification, RedwigglerReport}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Redwiggler")
object Redwiggler {

  def apply(callProvider: EndpointCallProvider,
            specificationProvider: EndpointSpecificationProvider,
            reportProcessor: ReportProcessor) : Seq[RedwigglerReport] = {
    val calls = callProvider.getCalls
    val specifications = specificationProvider.getEndPointSpecs

    val results = ApiValidator(specifications, calls)
    val reports = RedwigglerReport(validationResults = results)
    reportProcessor.process(reports)
    reports
  }

  @JSExport("apply")
  def jsApply(calls : js.Array[EndpointCall], reportProcessor : ReportProcessor): Unit = {
    apply(new EndpointCallProvider {

      override def getCalls(): Seq[EndpointCall] = calls.toArray.toSeq

    }, new EndpointSpecificationProvider {
      override def getEndPointSpecs: Seq[EndpointSpecification] = Seq()
    },reportProcessor)
  }
}
