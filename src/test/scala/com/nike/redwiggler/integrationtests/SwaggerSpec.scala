package com.nike.redwiggler.integrationtests

import java.io.File

import com.nike.redwiggler.core._
import com.nike.redwiggler.swagger.SwaggerEndpointSpecificationProvider

class SwaggerSpec extends End2EndSpecBase("swagger") {

  override def specificationProvider(testDir: File): EndpointSpecificationProvider =
    SwaggerEndpointSpecificationProvider(new File(testDir, "swagger.yaml"))
}
