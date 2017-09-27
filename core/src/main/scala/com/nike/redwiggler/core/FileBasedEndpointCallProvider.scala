package com.nike.redwiggler.core

import java.io.File

import com.nike.redwiggler.core.models.EndpointCall

case class FileBasedEndpointCallProvider(files : Seq[File]) extends EndpointCallProvider {

  def this(file: File) {
    this(Seq(file))
  }

  override def getCalls: Seq[EndpointCall] = files.map(EndpointCall.fromFile)
}
