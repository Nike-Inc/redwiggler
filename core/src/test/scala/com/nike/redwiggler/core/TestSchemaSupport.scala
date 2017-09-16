package com.nike.redwiggler.core

import com.nike.redwiggler.core.models.Schema

case object AllValidSchema extends Schema {
  override def isValid(payload: String): Boolean = true
}
case object AllInvalidSchema extends Schema {
  override def isValid(payload: String): Boolean = false
}
