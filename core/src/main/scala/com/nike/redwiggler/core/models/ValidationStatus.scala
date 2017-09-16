package com.nike.redwiggler.core.models

import org.everit.json.schema.ValidationException

sealed trait ValidationStatus

case class CallMatchedMultipleSpecifications(call : EndpointCall, specifications : Seq[EndpointSpecification]) extends ValidationStatus
case class CallNotMatchedBySpecification(call : EndpointCall) extends ValidationStatus
case class SchemaValidationFailed(call : EndpointCall, specification: EndpointSpecification, validationException : Option[ValidationException]) extends ValidationStatus
case class UntestedSpecification(specification : EndpointSpecification) extends ValidationStatus
case class ValidationPassed(call : EndpointCall, specification: EndpointSpecification) extends ValidationStatus
case class ValidationFailed(call : EndpointCall, specification: EndpointSpecification) extends ValidationStatus
