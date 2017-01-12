scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.bouncycastle" % "bcprov-jdk15on" % "1.56",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.56",
  "com.typesafe" % "config" % "1.3.1",
  "joda-time" % "joda-time" % "2.9.7",
  "org.joda" % "joda-convert" % "1.8.1",
  "alexanderfefelov.github.com" %% "vigruzki-api" % "3.1"
)

TwirlKeys.templateImports ++= Seq(
  "org.joda.time.DateTime", "org.joda.time.format.ISODateTimeFormat"
)

lazy val root = (project in file("."))
  .enablePlugins(SbtTwirl)
