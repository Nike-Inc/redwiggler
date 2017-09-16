package com.nike.redwiggler.core.models

case class RedwigglerReport(verbPath: VerbPath, results : Map[Int, Seq[ValidationResult]]) {

  def title: String = verbPath.verb + " " + verbPath.path

  def passed : Int = results.values.flatten.map(_.result).collect {
    case ValidationPassed(_, _) => 1
  }.sum

  def total : Int = results.values.flatten.size

  def badge: String = s"$passed/$total"
}

object RedwigglerReport {
  def apply(verbPath: VerbPath, results : Seq[ValidationResult]) : RedwigglerReport = {
    RedwigglerReport(verbPath, results.groupBy(_.code))
  }
}

case class VerbPath(verb : HttpVerb, path : String)
