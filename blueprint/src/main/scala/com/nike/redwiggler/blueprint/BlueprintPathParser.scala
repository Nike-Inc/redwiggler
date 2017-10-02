package com.nike.redwiggler.blueprint

import com.nike.redwiggler.core.models.{LiteralPathComponent, Path, PathComponent, PlaceHolderPathComponent}

object BlueprintPathParser {

  def apply(path : String) : Path = {
    if (path.contains("{?")) {
      apply(path.substring(0, path.indexOf("{?")))
    } else {
      Path(Path(path).components.map(parseComponent))
    }
  }

  private def parseComponent(component : PathComponent) : PathComponent = component match {
    case LiteralPathComponent(c) if c.startsWith(":") => PlaceHolderPathComponent(c.substring(1))
    case _ => component
  }
}
