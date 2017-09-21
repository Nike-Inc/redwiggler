package com.nike.redwiggler.core

import com.nike.redwiggler.core.models.{Schema, ValidationFailure}

case object AllValidSchema extends Schema {

  override def validate(payload: String): Option[ValidationFailure] = None
}
case object AllInvalidSchema extends Schema {
  override def validate(payload: String): Option[ValidationFailure] = Some(ValidationFailure(pointer = "pointer", message = "foobar", path = Seq()))
}
