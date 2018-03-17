val VERSION = "0.0.1-SNAPSHOT"

lazy val `revolut-test` = (project in file("."))
  .settings(
    name := "revolut-test",
    libraryDependencies ++=
        loggingApi.value ++ loggingLogback.value ++ scalaLogging.value ++ flyway.value ++
        akka.value ++ akkaHttp.value ++ akkaHttpCirce.value ++ slick.value ++ h2Database.value ++
        circe.value ++ enumeratum.value ++ scalatest.value ++ mockitoAll.value,
    version := VERSION
  )