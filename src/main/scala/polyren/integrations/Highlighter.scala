package polyren.integrations

import org.graalvm.polyglot.{Context, EnvironmentAccess, HostAccess, PolyglotAccess, Source, Value}
import polyren.config.HighlighterConfig
import polyren.config.HighlighterConfig.{PygmentsConfig, RougeConfig}
import polyren.integrations.Highlighter.{HighlightingFailed, InitializationFailed}
import polyren.prelude.*

import java.nio.file.Paths
import java.time.ZoneId
import scala.util.control.NoStackTrace

sealed trait Highlighter:

  protected val caller: Value
  final def highlight(code: String, language: String): IO[String] =
    IO.raiseWhen(!caller.canExecute)(InitializationFailed) >>
      IO(Option(caller.execute(code, language).asString())).flatMap(IO.fromOption(_)(HighlightingFailed))

object Highlighter:

  case object InitializationFailed extends Exception("Failed to initialize highlighting function") with NoStackTrace
  case object HighlightingFailed extends Exception("Highlighting failed, returned value was null") with NoStackTrace
  final private class Pygments(context: Context) extends Highlighter:

    protected val caller =
      val run =
        s"""import site
           |
           |from pygments import highlight
           |from pygments.lexers import get_lexer_by_name
           |from pygments.formatters import HtmlFormatter
           |
           |lambda code, language: highlight(code, get_lexer_by_name(language), HtmlFormatter())
           |""".stripMargin
      context.eval(Source.newBuilder("python", run, "pygments").build())

  final private class Rouge(context: Context) extends Highlighter:

    protected val caller =
      val run =
        s"""require 'rouge'
           |
           |formatter = Rouge::Formatters::HTML.new
           |lexers = Rouge::Lexer.all
           |
           |lambda { |code, language| formatter.format(lexers[language].lex(code) }
           |""".stripMargin
      context.eval(Source.newBuilder("ruby", run, "rouge").build())

  def rouge: Resource[IO, Highlighter] = Resource
    .fromAutoCloseable(
      IO {
        Context
          .newBuilder("ruby")
          .allowCreateProcess(false)
          .allowCreateThread(false)
          .allowEnvironmentAccess(EnvironmentAccess.NONE)
          .allowHostAccess(HostAccess.NONE)
          .allowIO(true)
          .allowNativeAccess(true)
          .allowPolyglotAccess(PolyglotAccess.NONE)
          .allowValueSharing(false)
          .currentWorkingDirectory(Paths.get("/tmp"))
          .timeZone(ZoneId.systemDefault())
          .options(new java.util.HashMap[String, String])
          .out(System.out) // TODO
          .err(System.err) // TODO
          .build()
      }
    )
    .map(Rouge(_))

  def pygments: Resource[IO, Highlighter] = Resource
    .fromAutoCloseable(
      IO {
        import scala.util.chaining._
        Context
          .newBuilder("python")
          .allowCreateProcess(false)
          .allowCreateThread(false)
          .allowEnvironmentAccess(EnvironmentAccess.NONE)
          .allowHostAccess(HostAccess.NONE)
          .allowIO(true)
          .allowNativeAccess(true)
          .allowPolyglotAccess(PolyglotAccess.NONE)
          .allowValueSharing(false)
          .currentWorkingDirectory(Paths.get("/tmp"))
          .timeZone(ZoneId.systemDefault())
          .options(
            new java.util.HashMap[String, String].tap(_.put("python.Executable", "/tmp/venv/bin/graalpython"))
          )
          .out(System.out) // TODO
          .err(System.err) // TODO
          .build()
      }
    )
    .map(Pygments(_))
  
  def resource(config: HighlighterConfig): Resource[IO, Highlighter] = config match
    case PygmentsConfig => pygments
    case RougeConfig => rouge
