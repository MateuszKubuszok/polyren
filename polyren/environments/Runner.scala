package polyren.environments

import org.graalvm.polyglot.{ Context, Source, Value }

import polyren.prelude.*

final class Runner(context: Context, language: String, code: String, name: Option[String] = None) {

  def named(name: String) = new Runner(context = context, language = language, code = code, name = Some(name))
  def stripMargin: Runner = new Runner(context = context, language = language, code = code.stripMargin, name)
  def asSource:    Source = Source.newBuilder(language, code, name.orNull).build()
  def run:         Value  = context.eval(asSource)
}
