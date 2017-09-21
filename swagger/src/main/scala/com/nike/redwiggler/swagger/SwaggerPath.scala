package com.nike.redwiggler.swagger

import com.nike.redwiggler.core.models.{LiteralPathComponent, Path, PlaceHolderPathComponent}

object SwaggerPath {

  def apply(uri : String) : Path = Path(Path(uri).components.map {
    case LiteralPathComponent(path) if path.startsWith("{") && path.endsWith("}") =>
      PlaceHolderPathComponent(path.substring(1, path.length - 1))
    case a => a
  })
}
