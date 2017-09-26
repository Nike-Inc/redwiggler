package com.nike.redwiggler.core

import java.io.{File, FileOutputStream, PrintWriter}

import com.nike.redwiggler.core.models.{EndpointCall, HttpVerb}
import org.scalatest.{FunSpec, Matchers, Outcome, fixture}

import scala.collection.JavaConverters._

class FileBasedEndpointCallProviderSpec extends FunSpec with Matchers {

  it("should find files") {
    val file = File.createTempFile("call", ".json")
    val out = new PrintWriter(new FileOutputStream(file))
    out.print("""
        |{
        | "verb": "GET",
        | "responseBody": "hello world",
        | "path": "/helloworld",
        | "responseHeaders": [],
        | "code": 200
        |}
      """.stripMargin)
    out.close()

    val callProvider = new FileBasedEndpointCallProvider(file)
    val calls = callProvider.getCalls.asScala
    calls should contain only EndpointCall(
        verb = HttpVerb.GET,
        responseBody = Some("hello world"),
        path = "/helloworld",
        responseHeaders = Seq(),
        requestBody = None,
        code = 200
      )
  }

}
