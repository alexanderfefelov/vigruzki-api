name := "vigruzki-api"
organization := "com.github.alexanderfefelov"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.3"
)

scalaxbPackageName in(Compile, scalaxb) := "com.github.alexanderfefelov.vigruzki.api"

lazy val root = (project in file("."))
  .enablePlugins(ScalaxbPlugin)

doc in Compile := target.map(_ / "none").value
