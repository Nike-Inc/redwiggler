scalaVersion in ThisBuild := "2.12.3"
crossScalaVersions := Seq("2.12.3", "2.11.11", "2.10.5")

organization in ThisBuild := "com.nike.redwiggler"
name := "redwiggler"

bintrayOrganization in ThisBuild := Some("nike")

import org.scoverage.coveralls.Imports.CoverallsKeys._

testOptions in Test in ThisBuild += Tests.Argument(TestFrameworks.ScalaTest, "-oDF")
libraryDependencies in ThisBuild ++= Seq(
  "org.pegdown" % "pegdown" % "1.4.2" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

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
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", new File(target.value, "/test-reports-html").getAbsolutePath),
    libraryDependencies ++= Seq(
      "org.everit.json" % "org.everit.json.schema" % "1.5.1",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "io.spray" %%  "spray-json" % "1.3.3"
    )
  )

lazy val swagger = (project in file("swagger"))
  .dependsOn(core)
  .settings(
    name := "redwiggler-swagger",
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", new File(target.value, "/test-reports-html").getAbsolutePath),
    libraryDependencies ++= Seq(
      "io.swagger" % "swagger-parser" % "1.0.28"
    )
  )

lazy val blueprint = (project in file("blueprint"))
  .dependsOn(core)
  .settings(
    name := "redwiggler-blueprint",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )

lazy val restassured = (project in file("restassured"))
  .dependsOn(core)
  .settings(
    name := "redwiggler-restassured",
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", new File(target.value, "/test-reports-html").getAbsolutePath),
    libraryDependencies ++= Seq(
      "com.jayway.restassured" % "rest-assured" % "2.9.0",
      "org.mockito" % "mockito-core" % "2.7.22" % "test"
    )
  )

lazy val html = (project in file("html"))
  .dependsOn(core)
  .enablePlugins(SbtTwirl)
  .settings(
    name := "redwiggler-reports-html",
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", new File(target.value, "/test-reports-html").getAbsolutePath)
  )

lazy val root = (project in file("."))
  .aggregate(core, swagger, restassured, html, blueprint)
  .enablePlugins(ReadmeTests)
  .settings(ReadmeTests.projectSettings)
  .dependsOn(core, swagger, html)
  .settings(
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", new File(target.value, "/test-reports-html").getAbsolutePath)
  )
