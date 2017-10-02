import sbt._
import Keys._

object ReadmeTests extends sbt.AutoPlugin {
  object autoImport {
    lazy val generateReadmeTest = taskKey[Unit]("Generate Readme Test")
    lazy val readmeFile = settingKey[File]("README.md file")
  }

  import autoImport._

  override def projectSettings : Seq[Def.Setting[_]] = Seq(
    readmeFile := new File(baseDirectory.value, "README.md"),
    sourceGenerators in Test <+= (readmeFile, sourceManaged in Test).map { (f, dir) =>
      if (!f.exists) {
        throw new IllegalStateException(s"Could not find $f")
      }
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
}
