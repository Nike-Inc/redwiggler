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
      filter(_._2.size == 1).
      map { case (spec, matchedCalls) => MatchedCall(matchedCalls.head, spec) }

    val matchedStatuses = matchedCallsAndSpecifications.
      map(_.matchedStatus)

    unmatchedCalls ++ specificationsWithNoCalls ++ matchedStatuses
  }

  case class MatchedCall(call: EndpointCall, specification: EndpointSpecification) {
    def matchedStatus = (call.responseBody, specification.responseSchema) match {
      case (Some(responseBody), Some(responseSchema)) if responseSchema.isValid(responseBody) =>
        ValidationPassed(call, specification)
      case (Some(responseBody), Some(responseSchema)) =>
        SchemaValidationFailed(call, specification, None)
      case _ =>
        ValidationFailed(call, specification)
    }
  }

}
