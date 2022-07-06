package polyren

import cats.effect.{ ExitCode, Resource }
import polyren.prelude.*

object Main extends cats.effect.IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    (for
      _ <- Resource.unit[IO]
      _ <- Resource.eval(IO(println("Running on noice docker image!")))
      highlighter <- integrations.Highlighter.rouge
      out <- Resource.eval(highlighter.highlight("echo test", "bash"))
      _ <- Resource.eval(IO(println(out)))
      _ <- Resource.eval(IO(println("Done")))
    yield ()).use_.as(cats.effect.ExitCode.Success)
}
