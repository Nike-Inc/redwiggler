package com.nike.redwiggler.core.models

case class ValidationResult(
                             path: String,
                             verb: HttpVerb,
                             code: Int,
                             result: ValidationStatus,
                             spec: EndpointSpecification,
                             call: EndpointCall
                           )
