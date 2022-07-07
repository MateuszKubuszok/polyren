package polyren.app

import polyren.config.*
import polyren.integrations.*
import polyren.prelude.*

object Program:
  private val highlighterConfig = HighlighterConfig.RougeConfig

  private def testRun(highlighter: Highlighter) = for
    _ <- IO(println("Running on noice docker image!"))
    result <- highlighter.highlight("echo test", "bash")
    _ <- IO(println(result))
    _ <- IO(println("Done"))
  yield ()

  def run: IO[Unit] = Highlighter.resource(highlighterConfig).use { highlighter =>
    testRun(highlighter = highlighter)
  }
