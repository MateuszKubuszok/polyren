val root = (project in file("."))
  .settings(
    name := "polyren",
    version := "0.1.0",
    scalaVersion := "3.1.3",
    scalacOptions ++= Seq(
      "--deprecation",
      "--explain",
      "-Yno-predef",
    ),
    libraryDependencies ++= Seq(
      "org.graalvm.sdk" % "graal-sdk" % "22.1.0" % Provided,
      "org.typelevel" %% "cats-effect" % "3.3.13",
    ),
    dockerExposedPorts ++= Seq(),
    dockerBaseImage := "polyren-base:latest",
  )
  .enablePlugins(JavaAppPackaging, DockerPlugin)
