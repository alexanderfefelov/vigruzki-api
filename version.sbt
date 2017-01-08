version in ThisBuild := "3.1"

// Fix sbt warnings:
//   [warn] Attempting to overwrite SOME_PATH_IN_LOCAL_REPO
//   [warn] This usage is deprecated and will be removed in sbt 1.0.
// See http://stackoverflow.com/a/26089552

isSnapshot := true