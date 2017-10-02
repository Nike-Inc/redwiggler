package com.nike.redwiggler.integrationtests
import java.io.File

import com.nike.redwiggler.blueprint.BlueprintSpecificationProvider
import com.nike.redwiggler.blueprint.parser.ProtagonistBlueprintParser
import com.nike.redwiggler.core.EndpointSpecificationProvider

class BlueprintSpec extends End2EndSpecBase("blueprint") {
  override def specificationProvider(testDir: File): EndpointSpecificationProvider =
    BlueprintSpecificationProvider(new File(testDir, "API.md"), ProtagonistBlueprintParser())
}
