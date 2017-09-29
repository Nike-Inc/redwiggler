package com.nike.redwiggler.core;

import com.nike.redwiggler.core.models.EndpointSpecification;

import java.util.ArrayList;
import java.util.List;

public interface EndpointSpecificationProvider {

    List<EndpointSpecification> getEndPointSpecs();

    default EndpointSpecificationProvider concat(EndpointSpecificationProvider other) {
        return () -> {
            List<EndpointSpecification> list = new ArrayList<>();
            list.addAll(EndpointSpecificationProvider.this.getEndPointSpecs());
            list.addAll(other.getEndPointSpecs());
            return list;
        };
    }
}
