scalaVersion := "2.11.8"

resolvers += Resolver.file("Local repo", file(System.getProperty("user.home") + "/.ivy2/local"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "alexanderfefelov.github.com" %% "vigruzki-api" % "3.1"
)
