package com.nike.redwiggler.blueprint.parser

import java.io.{File, FileOutputStream, PrintWriter}
import java.util.concurrent.TimeUnit

import com.nike.redwiggler.blueprint.{Ast, AstHolder, AstProtocol}
import spray.json.JsonParser

import scala.io.Source

object DrafterBlueprintParser extends BlueprintParser with AstProtocol {

  private val LOGGER = org.slf4j.LoggerFactory.getLogger(getClass)

  override def name: String = "drafter"

  override def parse(blueprint: String): Ast = JsonParser(invokeDrafter(blueprint)).convertTo[AstHolder].ast

  private def invokeDrafter(blueprint: String): String = {
    val inputFile = File.createTempFile("redwiggler_drafter", ".js")
    val inputFileOut = new PrintWriter(new FileOutputStream(inputFile))
    inputFileOut.write(blueprint)
    inputFileOut.close()

    val outputFile = File.createTempFile("redwiggler_drafter", ".js")
    val process = Runtime.getRuntime.exec(Array(script,
      "-o", outputFile.getAbsolutePath,
      "-f", "json",
      "-t", "ast",
      inputFile.getAbsolutePath
    ))
    process.waitFor(10, TimeUnit.SECONDS)
    if (process.exitValue() == 0) {
      val x = Source.fromInputStream(process.getInputStream).mkString
      LOGGER.debug(x)
      Source.fromFile(outputFile).mkString
    } else {
      LOGGER.error("drafter exit value: " + process.exitValue())
      val x = Source.fromInputStream(process.getInputStream).mkString
      LOGGER.error(x)
      val error = Source.fromInputStream(process.getErrorStream).mkString
      LOGGER.error(error)
      throw new RuntimeException("Unable to execute drafter: " + error)
    }
  }

  private lazy val script = {
    val drafter = new File("drafter/bin/drafter")
    if (drafter.exists()) {
      LOGGER.info("using local drafter: " + drafter.getAbsolutePath)
      drafter.getAbsolutePath
    } else {
      LOGGER.info("using drafter in path")
      "drafter"
    }
  }
}
