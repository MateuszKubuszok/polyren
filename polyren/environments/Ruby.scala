package polyren.environments

import org.graalvm.polyglot.{ Context, EnvironmentAccess, HostAccess, PolyglotAccess }

import polyren.prelude.*

import java.nio.file.Paths
import java.time.ZoneId

opaque type Ruby = Context

object Ruby:

  // TODO
  final case class Config()

  def resource(config: Config): Resource[IO, Ruby] = Resource.fromAutoCloseable(
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
        .options(
          new java.util.HashMap[String, String]
        )
        .out(System.out) // TODO
        .err(System.err) // TODO
        .build()
    }
  )

extension (sc: StringContext)
  inline def ruby(args: Any*)(using context: Ruby): Runner = new Runner(context, "python", sc.s(args))
