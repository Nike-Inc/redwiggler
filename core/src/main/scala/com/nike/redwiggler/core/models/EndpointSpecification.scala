package com.nike.redwiggler.core.models

case class EndpointSpecification(
                                  verb: HttpVerb,
                                  path: Path,
                                  code: Int,
                                  responseSchema: Option[Schema],
                                  requestSchema: Option[Schema]
                                ) {
  def matches(call : EndpointCall) : Boolean = {
    verb == call.verb && path.matches(call.path)
  }
}

