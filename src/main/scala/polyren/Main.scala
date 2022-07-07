package polyren

import cats.effect.{ ExitCode, Resource }
import polyren.app.Program
import polyren.prelude.*

object Main extends cats.effect.IOApp:

  override def run(args: List[String]): IO[ExitCode] = Program.run.as(cats.effect.ExitCode.Success)
