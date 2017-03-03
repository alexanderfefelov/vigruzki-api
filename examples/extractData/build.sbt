scalaVersion := "2.11.8"

resolvers ++= Seq(
  Resolver.bintrayRepo("hajile", "maven")
)

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "alexanderfefelov.github.com" %% "vigruzki-api" % "3.1",
  "com.github.pathikrit" %% "better-files" % "2.16.0",
  "com.netaporter" %% "scala-uri" % "0.4.16",
  "ru.smslv.akka" %% "akka-dns" % "2.4.2"
)
