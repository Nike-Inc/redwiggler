const fs = require('fs')
const vm = require('vm')

const scala = {}
function sandbox(path, context) {
  const buffer = fs.readFileSync(path)
  vm.runInNewContext(buffer, context, path)
}

sandbox(__dirname + '/../core/target/scala-2.12/redwiggler-core-fastopt.js', scala)

module.exports = scala
