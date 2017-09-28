scalaVersion in ThisBuild := "2.12.3"
crossScalaVersions := Seq("2.12.3", "2.11.11", "2.10.5")

organization in ThisBuild := "com.nike.redwiggler"
name := "redwiggler"

bintrayOrganization in ThisBuild := Some("nike")

import org.scoverage.coveralls.Imports.CoverallsKeys._

coverallsServiceName := Some("travis-pro")

licenses in ThisBuild := Seq(
  "BSD" -> new java.net.URL("https://opensource.org/licenses/BSD-3-Clause")
)

coverageMinimum in ThisBuild := 80
coverageFailOnMinimum in ThisBuild := true

releaseCrossBuild in ThisBuild := true

lazy val core = (project in file("core"))
  .settings(
    name := "redwiggler-core",
    libraryDependencies ++= Seq(
      "org.everit.json" % "org.everit.json.schema" % "1.5.1",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "io.spray" %%  "spray-json" % "1.3.3",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )

lazy val swagger = (project in file("swagger"))
  .dependsOn(core)
  .settings(
    name := "redwiggler-swagger",
    libraryDependencies ++= Seq(
      "io.swagger" % "swagger-parser" % "1.0.28",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )

lazy val restassured = (project in file("restassured"))
  .dependsOn(core)
  .settings(
    name := "redwiggler-restassured",
    libraryDependencies ++= Seq(
      "com.jayway.restassured" % "rest-assured" % "2.9.0",
      "org.mockito" % "mockito-core" % "2.7.22" % "test",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )

lazy val html = (project in file("html"))
  .dependsOn(core)
  .enablePlugins(SbtTwirl)
  .settings(
    name := "redwiggler-reports-html",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )

lazy val root = (project in file("."))
  .aggregate(core, swagger, restassured, html)
  .dependsOn(core, swagger, html)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )

lazy val `integration-tests` = (project in file("integration-tests"))
  .dependsOn(core, swagger, html)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )

lazy val readme = (project in file("readme"))
  .dependsOn(core, swagger, html)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    ),
    sourceGenerators in Test <+= (sourceManaged in Test, baseDirectory in root).map { (dir, baseDir) =>
      val f = new File(baseDir, "README.md")
      val s = f.getName
      val i = s.lastIndexOf(".")
      val base = if (i < 0) s else s.substring(0, i)
      val basename = base + "Spec.scala"
      println(s"generating $basename")
      val output = IO.reader(f) { br =>
        val sb = new StringBuilder
        sb.append("import org.scalatest._\n")
        sb.append("import org.scalatest.Matchers._\n")
        sb.append(s"class ${base}Spec extends FunSpec {\n")
        IO.foldLines(br, (false, 0, None : Option[String])) { case ((inc, index, header), line) =>
          if (inc) {
            if (line == "```") {
              sb.append("\n")
                .append("}")

              (false, index, header)
            } else {
              sb.append("  " + line + "\n")
              (true, index, header)
            }
          } else {
            if (line == "```scala") {
              val testName = header.map(_ + " ").getOrElse("") + index
              sb.append("\n")
                .append(s"""it("should parse $testName") {\n""")
              println(s"Wrote README.md $testName")
              (true, index + 1, header)
            } else {
              sb.append("  // " + line + "\n")
              if (line.startsWith("# ")) {
                (false, 0, Some(line.substring(1).trim))
              } else {
                (false, index, header)
              }
            }
          }
        }
        sb.append("}\n")
        sb.toString
      }
      val dest = dir / basename
      println(s"Wrote $dest")
      IO.write(dest, output)
      Seq(dest)
    }
  )
