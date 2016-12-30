scalaVersion := "2.11.8"

resolvers += Resolver.file("Local repo", file(System.getProperty("user.home") + "/.ivy2/local"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "org.bouncycastle" % "bcprov-jdk15on" % "1.56",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.56",
  "com.typesafe" % "config" % "1.3.1",
  "joda-time" % "joda-time" % "2.9.7",
  "alexanderfefelov.github.com" %% "vigruzki-api" % "3.1"
)

TwirlKeys.templateImports ++= Seq(
  "org.joda.time.DateTime", "org.joda.time.format.ISODateTimeFormat"
)

lazy val root = (project in file("."))
  .enablePlugins(SbtTwirl)
