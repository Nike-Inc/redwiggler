require('source-map-support').install();

const core = require('./redwiggler-core.js')
const html = require('./redwiggler-reports-html.js')

console.log(core)
const processor = new html.HtmlReportProcessor(new html.FunctionFileWriter(function (x) {
  console.log(x)
}))
//processor.process([])
//
core.Redwiggler.apply([
  core.EndpointCallParser.fromObject({
    verb: "GET",
    responseBody: "foo",
    path: "/foo/bar",
    requestBody: "foo",
    code: 5
  })
], processor)

