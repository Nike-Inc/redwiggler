package com.nike.redwiggler.core

import com.nike.redwiggler.core.models._

object ApiValidator {

  def apply(specifications: Seq[EndpointSpecification], calls: Seq[EndpointCall]): Seq[ValidationStatus] = {
    val matchedSpecifications = specifications.map(spec => spec -> calls.filter(spec.matches))

    val unmatchedCalls = (calls diff matchedSpecifications.flatMap(_._2)).
      map(CallNotMatchedBySpecification.apply)

    val specificationsWithNoCalls = matchedSpecifications.
      filter(_._2.isEmpty).
      map(_._1).
      map(UntestedSpecification.apply)

    val matchedCallsAndSpecifications = matchedSpecifications.
      filter(_._2.nonEmpty).
      flatMap { case (spec, matchedCalls) => matchedCalls.map(call => MatchedCall(call, spec)) }

    val matchedStatuses = matchedCallsAndSpecifications.
      map(_.matchedStatus)

    unmatchedCalls ++ specificationsWithNoCalls ++ matchedStatuses
  }

  case class MatchedCall(call: EndpointCall, specification: EndpointSpecification) {

    def matchedStatus = (call.responseBody, specification.responseSchema) match {
      case (Some(responseBody), Some(responseSchema)) =>
        responseSchema.validate(responseBody) match {
          case Some(validations) =>
            SchemaValidationFailed(call, specification, validations)
          case None =>
            ValidationPassed(call, specification)
        }
      case _ =>
        ValidationFailed(call, specification)
    }
  }

}
