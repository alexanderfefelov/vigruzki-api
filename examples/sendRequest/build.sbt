scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.bouncycastle" % "bcprov-jdk15on" % "1.56",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.56",
  "com.typesafe" % "config" % "1.3.1",
  "joda-time" % "joda-time" % "2.9.7",
  "org.joda" % "joda-convert" % "1.8.1",
  "com.github.alexanderfefelov" %% "vigruzki-api" % "3.1",
  "com.github.pathikrit" %% "better-files" % "2.16.0"
)

TwirlKeys.templateImports ++= Seq(
  "org.joda.time.DateTime", "org.joda.time.format.ISODateTimeFormat"
)

lazy val root = (project in file("."))
  .enablePlugins(SbtTwirl)
