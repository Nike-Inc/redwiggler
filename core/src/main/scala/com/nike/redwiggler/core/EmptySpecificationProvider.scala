package com.nike.redwiggler.core

import java.util
import java.util.Collections

import com.nike.redwiggler.core.models.EndpointSpecification

object EmptySpecificationProvider extends EndpointSpecificationProvider {
  override def getEndPointSpecs: util.List[EndpointSpecification] = Collections.emptyList()
}
