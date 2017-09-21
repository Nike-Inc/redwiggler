package com.nike.redwiggler.core.models

import org.everit.json.schema.ValidationException

sealed abstract class ValidationStatus(val verbPath: VerbPath, val code : Int) {
  def this(call : EndpointCall) = this(VerbPath(path = call.path, verb = call.verb), code = call.code)
  def this(specification : EndpointSpecification) = this(VerbPath(path = specification.path.asString, verb = specification.verb), code = specification.code)
}

case class CallMatchedMultipleSpecifications(call : EndpointCall, specifications : Seq[EndpointSpecification]) extends ValidationStatus(call)
case class CallNotMatchedBySpecification(call : EndpointCall) extends ValidationStatus(call)
case class SchemaValidationFailed(call : EndpointCall, specification: EndpointSpecification, validationException : Option[ValidationException]) extends ValidationStatus(specification)
case class UntestedSpecification(specification : EndpointSpecification) extends ValidationStatus(specification)
case class ValidationPassed(call : EndpointCall, specification: EndpointSpecification) extends ValidationStatus(specification)
case class ValidationFailed(call : EndpointCall, specification: EndpointSpecification) extends ValidationStatus(specification)
