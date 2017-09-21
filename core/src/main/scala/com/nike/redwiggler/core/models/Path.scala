package com.nike.redwiggler.core.models

case class Path(components : Seq[PathComponent]) {
  def matches(path : String) : Boolean = {
    val splitTokens = path.split("/")
    val tokens = if (path.startsWith("/")) {
      splitTokens.tail
    } else {
      splitTokens
    }
    if (tokens.length == components.length) {
      components.zip(tokens).map { case (component, token) => component.matches(token) }.forall(x => x)
    } else {
      false
    }
  }

  def /(path : Path) : Path = Path(components ++ path.components)

  def asString : String = components.map(_.asString).mkString("/")
}

object Path {
  def apply() : Path = Path(Seq())
  def apply(uri : String) : Path = {
    if (uri == "/") {
      apply()
    } else if (uri.startsWith("/")) {
      Path(uri.tail.split("/").map(LiteralPathComponent.apply))
    } else {
      throw PathMustBeAbsoluteException()
    }
  }
}
case class PathMustBeAbsoluteException() extends IllegalStateException

sealed trait PathComponent {
  def matches(path : String) : Boolean

  def asString : String
}
case class LiteralPathComponent(path : String) extends PathComponent {
  override def matches(path: String): Boolean = this.path == path
  override def asString : String = path
}
case class PlaceHolderPathComponent(id : String) extends PathComponent {
  override def matches(path: String): Boolean = true
  override def asString : String = "{" + id + "}"
}
