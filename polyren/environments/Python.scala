package polyren.environments

import org.graalvm.polyglot.{ Context, EnvironmentAccess, HostAccess, PolyglotAccess }

import polyren.prelude.*

import java.nio.file.Paths
import java.time.ZoneId

opaque type Python = Context

object Python:

  // TODO
  final case class Config()

  def resource(config: Config): Resource[IO, Python] = Resource.fromAutoCloseable(
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
        .options(
          new java.util.HashMap[String, String].tap(_.put("python.Executable", "/tmp/venv/bin/graalpython"))
        )
        .out(System.out) // TODO
        .err(System.err) // TODO
        .build()
    }
  )

extension (sc: StringContext)
  inline def python(args: Any*)(using context: Python): Runner = new Runner(context, "python", sc.s(args))
