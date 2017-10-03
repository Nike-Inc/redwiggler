package com.nike.redwiggler

import com.nike.redwiggler.core.models.{EndpointCall, EndpointSpecification, HttpVerb, Path}

package object html {

  val specificationA = EndpointSpecification(
    verb = HttpVerb.DELETE,
    path = Path("/foo/bar"),
    code = 200,
    responseSchema = None,
    requestSchema = None
  )

  val specificationB = EndpointSpecification(
    verb = HttpVerb.GET,
    path = Path("/foo/bar"),
    code = 200,
    responseSchema = None,
    requestSchema = None
  )

  val callA = EndpointCall(
    verb = specificationA.verb,
    responseBody = None,
    path = specificationA.path.asString,
    responseHeaders = Seq(),
    requestBody = None,
    code = 200
  )

  val callB = EndpointCall(
    verb = specificationB.verb,
    responseBody = None,
    path = specificationB.path.asString,
    responseHeaders = Seq(),
    requestBody = None,
    code = 200
  )

}
