package com.nike.redwiggler.blueprint

import com.nike.redwiggler.core.models.{LiteralPathComponent, Path, PathComponent}

object BlueprintPathParser {

  def apply(path : String) : Path = {
    val seq = Path(path).components.reverse
    Path(seq.tail.reverse) / cleanupLast(seq.headOption)
  }

  def cleanupLast(component : Option[PathComponent]) : Path = component match {
    case Some(LiteralPathComponent(path)) if path.contains("{?") => Path(Seq(LiteralPathComponent(path.substring(0, path.indexOf("{?")))))
    case Some(LiteralPathComponent(path)) => Path(Seq(LiteralPathComponent(path)))
    case None => Path()
    case Some(c) => Path(Seq(c))
  }
}
