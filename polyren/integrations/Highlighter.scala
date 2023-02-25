package polyren.integrations

import org.graalvm.polyglot.Value

import polyren.prelude.*
import polyren.environments.*

import java.nio.file.Paths
import java.time.ZoneId
import scala.util.control.NoStackTrace

enum Highlighter(caller: Value):

  private case Pygments(python: Python)
      extends Highlighter({
        given Python = python
        python"""import site
                |
                |from pygments import highlight
                |from pygments.lexers import get_lexer_by_name
                |from pygments.formatters import HtmlFormatter
                |
                |lambda code, language: highlight(code, get_lexer_by_name(language), HtmlFormatter())
                |""".stripMargin.named("pygments").run
      })

  private case Rouge(ruby: Ruby)
      extends Highlighter({
        given Ruby = ruby
        ruby"""require 'rouge'
              |
              |formatter = Rouge::Formatters::HTML.new
              |lexers = Rouge::Lexer.all
              |
              |lambda { |code, language| formatter.format(lexers[language].lex(code) }
              |""".stripMargin.named("rouge").run
      })

  final def highlight(code: String, language: String): IO[String] =
    IO.raiseWhen(!caller.canExecute)(Highlighter.InitializationFailed) >>
      IO(Option(caller.execute(code, language).asString())).flatMap(IO.fromOption(_)(Highlighter.HighlightingFailed))

object Highlighter:

  case object InitializationFailed extends Exception("Failed to initialize highlighting function") with NoStackTrace
  case object HighlightingFailed extends Exception("Highlighting failed, returned value was null") with NoStackTrace

  enum Config:
    case Pygments(pythonConfig: Python.Config)
    case Rouge(rubyConfig: Ruby.Config)

  def resource(config: Config): Resource[IO, Highlighter] = config match
    case Config.Pygments(pythonConfig) => Python.resource(pythonConfig).map(Pygments(_))
    case Config.Rouge(rubyConfig)      => Ruby.resource(rubyConfig).map(Rouge(_))
