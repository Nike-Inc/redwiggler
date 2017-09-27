package com.nike.redwiggler.core

import com.nike.redwiggler.core.models.RedwigglerReport

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
}
