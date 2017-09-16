package com.nike.redwiggler.core;

import com.nike.redwiggler.core.models.EndpointSpecification;

import java.util.List;

public interface EndpointSpecificationProvider {

    List<EndpointSpecification> getEndPointSpecs();
}
