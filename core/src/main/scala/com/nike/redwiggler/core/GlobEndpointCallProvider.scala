package com.nike.redwiggler.core

import java.io.{File, FileNotFoundException}
import java.util
import java.util.regex.Pattern

import collection.JavaConverters._
import com.nike.redwiggler.core.models.EndpointCall
import org.slf4j.LoggerFactory

case class GlobEndpointCallProvider(dir : File, pattern : String) extends EndpointCallProvider {

  import GlobEndpointCallProvider._

  override def getCalls: util.List[EndpointCall] = {
    LOGGER.info(s"Doing glob match dir=$dir pattern=$pattern")
    val callProvider = FileBasedEndpointCallProvider(findFiles)
    callProvider.getCalls
  }

  private def findFiles = if (!dir.exists) {
    LOGGER.error("The following directory does not exist: dir={}", dir.getPath)
    throw new GlobDirectoryDoesNotExist(dir)
  } else {
    LOGGER.info(s"Looking for glob match dir=$dir pattern=$pattern")
    val files = dir.listFiles
    LOGGER.info(s"Found glob match dir=$dir pattern=$pattern count=${files.length}")
    files.filter { file =>
      !file.isDirectory && Pattern.matches(pattern, file.getName)
    }.toSeq.asJava
  }

}
object GlobEndpointCallProvider {
  private val LOGGER = LoggerFactory.getLogger(classOf[GlobEndpointCallProvider])
}
case class GlobDirectoryDoesNotExist(dir : File) extends FileNotFoundException("Cannot load data from nonexistant directory: " + dir.getAbsolutePath)
