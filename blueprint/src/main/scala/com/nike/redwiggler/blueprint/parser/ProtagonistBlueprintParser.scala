package com.nike.redwiggler.blueprint.parser

import java.io.{File, FileOutputStream, PrintWriter}
import java.util.concurrent.TimeUnit

import com.nike.redwiggler.blueprint.{Ast, AstHolder, AstProtocol}
import spray.json._

import scala.io.Source

case class ProtagonistBlueprintParser(nodePath : Seq[String]) extends BlueprintParser with AstProtocol {

  import ProtagonistBlueprintParser.LOGGER
  override def name: String = "protagonist"

  private def invokeNode(blueprint: String): String = {
    val script =
      """
        |var protagonist = require('protagonist');
        |var options = {
        | generateSourceMap: false,
        | type: 'ast'
        |}
        |var blueprint = process.argv[2]
        |var fs = require('fs');
        |var blueprintData = fs.readFileSync(blueprint).toString()
        |protagonist.parse(blueprintData, options, function(error, result) {
        |    if (error) {
        |        console.log(error);
        |        return;
        |    }
        |
        |    console.log(JSON.stringify(result));
        |});
      """.stripMargin

    val inputFile = File.createTempFile("redwiggler_protagonist", ".js")
    val inputFileOut = new PrintWriter(new FileOutputStream(inputFile))
    inputFileOut.write(blueprint)
    inputFileOut.close()

    val sourceFile = File.createTempFile("redwiggler_protagonist", ".js")
    val out = new PrintWriter(new FileOutputStream(sourceFile))
    out.write(script)
    out.close()

    LOGGER.info("using node path: " + nodePath.mkString(";"))
    val process = Runtime.getRuntime.exec(Array("node", sourceFile.getAbsolutePath, inputFile.getAbsolutePath), Array("NODE_PATH=" + nodePath.mkString(";")))
    process.waitFor(10, TimeUnit.SECONDS)
    if (process.exitValue() == 0) {
      Source.fromInputStream(process.getInputStream).mkString
    } else {
      LOGGER.error("protagonist exit value: " + process.exitValue())
      val x = Source.fromInputStream(process.getInputStream).mkString
      LOGGER.error(x)
      val error = Source.fromInputStream(process.getErrorStream).mkString
      LOGGER.error(error)
      throw new RuntimeException("Unable to execute protagonist: " + error)
    }
  }


  override def parse(blueprint : String) : Ast = {
    JsonParser(invokeNode(blueprint)).convertTo[AstHolder].ast
  }
}

object ProtagonistBlueprintParser {
  private val LOGGER = org.slf4j.LoggerFactory.getLogger(getClass)

  def apply() : ProtagonistBlueprintParser = this(
    Seq(System.getProperty("user.home") + "/node_modules", "node_modules")
      .map(f => new File(f))
      .filter(_.exists())
      .map(_.getAbsolutePath)
  )
}

