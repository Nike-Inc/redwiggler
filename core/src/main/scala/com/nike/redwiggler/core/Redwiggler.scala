package com.nike.redwiggler.core

import com.nike.redwiggler.core.models.RedwigglerReport
import collection.JavaConverters._

object Redwiggler {

  def apply(callProvider: EndpointCallProvider,
            specificationProvider: EndpointSpecificationProvider,
            reportProcessor: ReportProcessor) : Seq[RedwigglerReport] = {
    val calls = callProvider.getCalls.asScala
    val specifications = specificationProvider.getEndPointSpecs.asScala

    val results = ApiValidator(specifications, calls)
    val reports = RedwigglerReport(validationResults = results)
    reportProcessor.process(reports.asJava)
    reports
  }
}
