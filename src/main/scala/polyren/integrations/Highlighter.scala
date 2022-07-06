package polyren.integrations

import org.graalvm.polyglot.{ Context, EnvironmentAccess, HostAccess, PolyglotAccess, Source }
import polyren.prelude.*

import java.nio.file.Paths
import java.time.ZoneId

enum Highlighter:
  private case Pygments(context: Context)
  private case Rouge(context: Context)

  final def highlight(code: String, language: String): IO[String] = this match
    case Pygments(context) =>
      IO {
        // TODO: pass code and language
        val run =
          s"""import site
             |
             |from pygments import highlight
             |from pygments.lexers import PythonLexer
             |from pygments.formatters import HtmlFormatter
             |
             |code = 'print "Hello World"'
             |highlight(code, PythonLexer(), HtmlFormatter())""".stripMargin
        context.eval(Source.newBuilder("python", run, "pygments").build()).asString()
      }

    case Rouge(context) =>
      IO {
        // TODO: pass code and language
        println(language + code)
        val run =
          s"""require 'rouge'
             |
             |source = "echo test"
             |formatter = Rouge::Formatters::HTML.new
             |lexer = Rouge::Lexers::Shell.new
             |formatter.format(lexer.lex(source)""".stripMargin
        context.eval(Source.newBuilder("ruby", run, "rouge").build()).asString()
      }

object Highlighter:

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
    .map(Rouge.apply)

  def pygments: Resource[IO, Highlighter] = Resource
    .fromAutoCloseable(
      IO {
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
          .options(new java.util.HashMap[String, String])
          .out(System.out) // TODO
          .err(System.err) // TODO
          .build()
      }
    )
    .map(Pygments.apply)
