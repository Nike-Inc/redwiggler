package com.nike.redwiggler.core

import java.io.{File, FileOutputStream, PrintWriter}

import com.nike.redwiggler.core.models.{EndpointCall, HttpVerb}
import org.scalatest.{Matchers, Outcome, fixture}

class GlobEndpointCallProviderSpec extends fixture.FunSpec with Matchers {

  it("should find files") { fixture =>
    fixture.createFile("foo1.xml", "hello world")
    fixture.createFile("foo1.json",
      """
        |{
        | "verb": "GET",
        | "responseBody": "hello world",
        | "path": "/helloworld",
        | "responseHeaders": [],
        | "code": 200
        |}
      """.stripMargin)

    val glob = new GlobEndpointCallProvider(fixture.dir, ".*.json")
    val calls = glob.getCalls
    calls should contain only EndpointCall(
        verb = HttpVerb.GET,
        responseBody = Some("hello world"),
        path = "/helloworld",
        responseHeaders = Seq(),
        requestBody = None,
        code = 200
      )
  }

  override protected def withFixture(test: OneArgTest): Outcome = withFixture(test.toNoArgTest(new FixtureParam))

  class FixtureParam {
    val dir = File.createTempFile("redwiggler", "glob")
    dir.delete()
    dir.mkdirs()

    def createFile(name : String, contents : String): Unit = {
      val file = new File(dir, name)
      val writer = new PrintWriter(new FileOutputStream(file))
      writer.print(contents)
      writer.close()
    }
  }
}
