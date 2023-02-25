package polyren

import polyren.prelude.*

object Main extends cats.effect.IOApp:

  override def run(args: List[String]): IO[cats.effect.ExitCode] =
    polyren.app.Program.run.as(cats.effect.ExitCode.Success)
