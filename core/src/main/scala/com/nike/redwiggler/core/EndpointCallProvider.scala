package com.nike.redwiggler.core

import com.nike.redwiggler.core.models.{EndpointCall, EndpointSpecification, RedwigglerReport}

trait EndpointCallProvider {

  def getCalls() : Seq[EndpointCall]
}

trait EndpointSpecificationProvider {
  def getEndPointSpecs : Seq[EndpointSpecification]
}

trait ReportProcessor {
  def process(reports: Seq[RedwigglerReport]): Unit

  def andThen(reportProcessor: ReportProcessor): ReportProcessor = {
    val thisProcessor: ReportProcessor = this
    new ReportProcessor {
      override def process(reports: Seq[RedwigglerReport]): Unit = {
        thisProcessor.process(reports)
        reportProcessor.process(reports)
      }
    }
  }
}
