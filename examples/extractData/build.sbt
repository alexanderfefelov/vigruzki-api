scalaVersion := "2.12.6"

resolvers ++= Seq(
  Resolver.bintrayRepo("hajile", "maven")
)

libraryDependencies ++= Seq(
  "com.github.alexanderfefelov" %% "vigruzki-api" % "3.1",
  "com.github.pathikrit" %% "better-files" % "3.6.0",
  "com.netaporter" %% "scala-uri" % "0.4.16",
  "ru.smslv.akka" %% "akka-dns" % "2.4.2",
  "be.jvb.scala-ipv4" %% "scala-ipv4" % "0.4-SNAPSHOT"
)
