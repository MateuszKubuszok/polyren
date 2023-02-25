package polyren.app

import polyren.prelude.*
import polyren.environments.*
import polyren.integrations.*

object Program:
  private val highlighterConfig = Highlighter.Config.Rouge(Ruby.Config())

  private def testRun(highlighter: Highlighter) = for
    _ <- IO(scala.Predef.println("Running on noice docker image!"))
    result <- highlighter.highlight("echo test", "bash")
    _ <- IO(scala.Predef.println(result))
    _ <- IO(scala.Predef.println("Done"))
  yield ()

  def run: IO[Unit] = Highlighter.resource(highlighterConfig).use { highlighter =>
    testRun(highlighter = highlighter)
  }
