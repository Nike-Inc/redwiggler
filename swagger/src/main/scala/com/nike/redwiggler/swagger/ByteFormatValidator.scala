package com.nike.redwiggler.swagger

import java.util.Optional

import org.everit.json.schema.FormatValidator

object ByteFormatValidator extends FormatValidator {
  override def validate(subject: String): Optional[String] = ???

  override def formatName : String = "binary"
}
