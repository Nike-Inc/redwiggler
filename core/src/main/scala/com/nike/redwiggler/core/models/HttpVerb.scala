package com.nike.redwiggler.core.models

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("HttpVerb")
case class HttpVerb(name: String)

object HttpVerb {
  val POST = HttpVerb("POST")
  val GET = HttpVerb("GET")
  val PUT = HttpVerb("PUT")
  val DELETE = HttpVerb("DELETE")
  val OPTION = HttpVerb("OPTION")
  val PATCH = HttpVerb("PATCH")

  def from(verb : String) : HttpVerb = HttpVerb(verb.toUpperCase())
}

