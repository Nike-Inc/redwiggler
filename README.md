# Red wiggler
The composting worm.  Composts your contract specification and tests and confirms that the contract specification is being followed.

[![Build Status](https://travis-ci.org/Nike-Inc/redwiggler.svg?branch=master)](https://travis-ci.org/Nike-Inc/redwiggler)
[![Coverage Status](https://coveralls.io/repos/github/Nike-Inc/redwiggler/badge.svg?branch=master)](https://coveralls.io/github/Nike-Inc/redwiggler?branch=master)
[ ![Download](https://api.bintray.com/packages/nike/maven/redwiggler/images/download.svg) ](https://bintray.com/nike/maven/redwiggler/_latestVersion)

## Overview

RedWiggler has 3 main components:

1. EndpointSpecifications: defines the endpoint contract. Defined from the API documentation

    * Swagger Support: [ ![Download](https://api.bintray.com/packages/nike/maven/redwiggler-swagger/images/download.svg) ](https://bintray.com/nike/maven/redwiggler-swagger/_latestVersion)
    * [Blueprint Support](blueprint/README.md): [ ![Download](https://api.bintray.com/packages/nike/maven/redwiggler-blueprint/images/download.svg) ](https://bintray.com/nike/maven/redwiggler-blueprint/_latestVersion)
  
2. EndpointCall: An instance of a request/response to the service to be matched against the EndpointSpecification
3. ReportProcessor: Takes the result of the analysis and generates output (such as an html page)

    * HTML Reports: [ ![Downlaod](https://api.bintray.com/packages/nike/maven/redwiggler-reports-html/images/download.svg) ](https://bintray.com/nike/maven/redwiggler-reports-html/_latestVersion)

## Usage

Currently, the jars are only available on bintray.

### Gradle
```groovy
    repositories {
        jCenter()
    }
    dependencies {
        compile groupId: 'com.nike.redwiggler', artifactId: 'redwiggler-core_2.12', verson: redwigglerVersion
        compile groupId: 'com.nike.redwiggler', artifactId: 'redwiggler-swagger_2.12', verson: redwigglerVersion
        compile groupId: 'com.nike.redwiggler', artifactId: 'redwiggler-reports-html_2.12', verson: redwigglerVersion
        compile groupId: 'com.nike.redwiggler', artifactId: 'redwiggler-restassured_2.12', verson: redwigglerVersion
    }
```

### Sbt
```sbt
    Resolvers += Resolver.jcenterRepo
    libraryDependencies ++= Seq("core", "swagger", "reports-html", "restassured")
      .map(library => "com.nike.redwiggler" %% library % redwigglerVersion)
```

## Examples

```scala
import com.nike.redwiggler.core._
import com.nike.redwiggler.swagger._
import com.nike.redwiggler.html._

val swagger = SwaggerEndpointSpecificationProvider(
  """
    |swagger: "2.0"
    |paths:
    |  /:
    |    get:
    |      description: Gets Item
    |      responses:
    |        200:
    |          schema:
    |            properties:
    |              id:
    |                type: string
    """.stripMargin)
    
import java.io._
val requestDir = File.createTempFile("redwiggler", "requests")
requestDir.delete()
requestDir.mkdir()
val requests = new GlobEndpointCallProvider(requestDir, ".*.json")

val htmlReportFile = File.createTempFile("redwiggler", ".html")
val htmlRender = HtmlReportProcessor(htmlReportFile)

Redwiggler(callProvider = requests, specificationProvider = swagger, reportProcessor = htmlRender)

htmlReportFile.exists() should be(true)
```

## Current features:
+ Supports swagger 2.0
+ Compares every call to every schema and sees if there's a match to path, code and request schema
+ 5 types of outcome:
  * schema validation fails
  * call not matched by schema
  * schema is not covered
  * schema validation passes
  * call is matched by multiple schemas
+ Outputs a report in html format
