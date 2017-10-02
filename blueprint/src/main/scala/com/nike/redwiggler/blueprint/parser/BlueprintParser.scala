package com.nike.redwiggler.blueprint.parser

import com.nike.redwiggler.blueprint.Ast

trait BlueprintParser {
  def parse(blueprint : String) : Ast

  def name : String
}
