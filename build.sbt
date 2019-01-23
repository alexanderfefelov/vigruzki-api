name := "vigruzki-api"
organization := "com.github.alexanderfefelov"

crossScalaVersions := Seq("2.11.12", "2.12.8")

lazy val dispatchV = "0.14.0"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.1.0",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1",
  "org.dispatchhttp" %% "dispatch-core" % dispatchV
)

scalaxbPackageName in(Compile, scalaxb) := "com.github.alexanderfefelov.vigruzki.api"

lazy val root = (project in file("."))
  .enablePlugins(ScalaxbPlugin)
  .settings(
    scalaxbDispatchVersion in (Compile, scalaxb) := dispatchV
  )

doc in Compile := target.map(_ / "none").value
