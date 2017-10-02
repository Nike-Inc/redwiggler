# Redwiggler API.md Blueprint support

# API.md parsers
## Protagonist
To use the [protagonist](https://github.com/apiaryio/protagonist) parser, it must be installed first via npm:

```shell
npm install protagonist
```

```scala
import com.nike.redwiggler.core._
import com.nike.redwiggler.blueprint._
import com.nike.redwiggler.html._

val blueprint = BlueprintSpecificationProvider(
  """
    | # My Api Api
    |
    | ## Overview
    | **MyAPI** is a sample.
    |
    | ### Search [GET /my/api/v1{?anchor,count,filter}]
    """.stripMargin, parser.ProtagonistBlueprintParser())
    
import java.io._
val requestDir = File.createTempFile("redwiggler", "requests")
requestDir.delete()
requestDir.mkdir()
val requests = GlobEndpointCallProvider(requestDir, ".*.json")

val htmlReportFile = File.createTempFile("redwiggler", ".html")
val htmlRender = HtmlReportProcessor(htmlReportFile)

Redwiggler(callProvider = requests, specificationProvider = blueprint , reportProcessor = htmlRender)

htmlReportFile.exists() should be(true)
```

## Drafter
The [drafter](https://github.com/apiaryio/drafter) cli can also be used. This must be installed as well and available on the PATH.

```scala
import com.nike.redwiggler.core._
import com.nike.redwiggler.blueprint._
import com.nike.redwiggler.html._

val blueprint = BlueprintSpecificationProvider(
  """
    | # My Api Api
    |
    | ## Overview
    | **MyAPI** is a sample.
    |
    | ### Search [GET /my/api/v1{?anchor,count,filter}]
    """.stripMargin, parser.DrafterBlueprintParser)
    
import java.io._
val requestDir = File.createTempFile("redwiggler", "requests")
requestDir.delete()
requestDir.mkdir()
val requests = GlobEndpointCallProvider(requestDir, ".*.json")

val htmlReportFile = File.createTempFile("redwiggler", ".html")
val htmlRender = HtmlReportProcessor(htmlReportFile)

Redwiggler(callProvider = requests, specificationProvider = blueprint , reportProcessor = htmlRender)

htmlReportFile.exists() should be(true)
```
