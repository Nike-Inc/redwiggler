package com.nike.redwiggler.core

import java.io.File
import java.util
import java.util.Collections.singletonList
import collection.JavaConverters._

import com.nike.redwiggler.core.models.EndpointCall

case class FileBasedEndpointCallProvider(files : util.List[File]) extends EndpointCallProvider {

  def this(file: File) {
    this(singletonList(file))
  }

  override def getCalls: util.List[EndpointCall] = files.asScala.map(EndpointCall.fromFile).asJava
}
