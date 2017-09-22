organization in ThisBuild := "com.nike.redwiggler"

name := "redwiggler"

bintrayOrganization in ThisBuild := Some("nike")
//stage releases on publish
bintrayReleaseOnPublish in ThisBuild := false

licenses in ThisBuild := Seq(
  "BSD" -> new java.net.URL("https://opensource.org/licenses/BSD-3-Clause")
)

val generalSettings = Seq(
    bintrayPackage := "redwiggler"
)

scalaVersion in ThisBuild := "2.11.8"
coverageMinimum in ThisBuild := 80
coverageFailOnMinimum in ThisBuild := true

lazy val core = (project in file("core"))
  .settings(generalSettings)
  .settings(
    name := "redwiggler-core",
    bintrayPackage := "redwiggler",
    libraryDependencies ++= Seq(
      "org.everit.json" % "org.everit.json.schema" % "1.5.1",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "io.spray" %%  "spray-json" % "1.3.3",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )

lazy val swagger = (project in file("swagger"))
  .settings(generalSettings)
  .dependsOn(core)
  .settings(
    name := "redwiggler-swagger",
    libraryDependencies ++= Seq(
      "io.swagger" % "swagger-parser" % "1.0.28",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )

lazy val restassured = (project in file("restassured"))
  .settings(generalSettings)
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
  .settings(generalSettings)
  .dependsOn(core)
  .enablePlugins(SbtTwirl)
  .settings(
    name := "redwiggler-outputs-html",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )

lazy val root = (project in file("."))
  .aggregate(core, swagger, restassured, html)

lazy val `integration-tests` = (project in file("integration-tests"))
  .settings(generalSettings)
  .dependsOn(core, swagger, html)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )
