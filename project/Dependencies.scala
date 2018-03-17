import sbt._
import sbt.plugins.{IvyPlugin, JvmPlugin}

object Dependencies extends AutoPlugin {

  override def trigger: PluginTrigger = AllRequirements

  override def requires: Plugins = JvmPlugin && IvyPlugin

  object autoImport {
    val loggingVersion = settingKey[String]("slf4j version")
    val loggingApi = settingKey[Seq[ModuleID]]("slf4j api")
    val loggingLogbackVersion = settingKey[String]("logback version")
    val loggingLogback = settingKey[Seq[ModuleID]]("slf4j logback impl")
    val scalaLogging = settingKey[Seq[ModuleID]]("scala logging")
    val scalaLoggingVersion = settingKey[String]("scala logging version")

    val akka = settingKey[Seq[ModuleID]]("akka")
    val akkaVersion = settingKey[String]("akka version")
    val akkaHttp = settingKey[Seq[ModuleID]]("akka http")
    val akkaHttpVersion = settingKey[String]("akka http version")

    val akkaHttpCirce = settingKey[Seq[ModuleID]]("akka http circe")
    val akkaHttpCirceVersion = settingKey[String]("akka http circe version")

    val circe = settingKey[Seq[ModuleID]]("circe (json library)")
    val circeVersion = settingKey[String]("circe (json library) version")

    val enumeratum = settingKey[Seq[ModuleID]]("enumeratum")
    val enumeratumVersion = settingKey[String]("enumeratum version")
    val enumeratumCirceVersion = settingKey[String]("enumeratum circe version")

    val scalatest = settingKey[Seq[ModuleID]]("scalatest")
    val scalatestVersion = settingKey[String]("scalatest version")

    val mockitoAll = settingKey[Seq[ModuleID]]("mockito all")
    val mockitoAllVersion = settingKey[String]("mockito all version")
  }

  import autoImport._

  override def projectSettings: Seq[Setting[_]] = versions ++ libraries

  private def versions = Seq(
    loggingVersion := "1.7.25",
    loggingLogbackVersion := "1.0.11",
    scalaLoggingVersion := "3.8.0",
    akkaVersion := "2.5.10",
    akkaHttpVersion := "10.1.0-RC2",
    akkaHttpCirceVersion := "1.20.0-RC2",
    circeVersion := "0.9.1",
    enumeratumVersion := "1.5.12",
    enumeratumCirceVersion := "1.5.15",
    scalatestVersion := "3.0.5",
    mockitoAllVersion := "1.10.19"
  )

  private def libraries = Seq(
    loggingApi := Seq(
      "org.slf4j" % "slf4j-api" % loggingVersion.value
    ),

    loggingLogback := loggingApi.value ++ Seq(
      "ch.qos.logback" % "logback-classic" % loggingLogbackVersion.value
    ),

    scalaLogging := Seq(
      "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion.value
    ),

    akka := Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion.value,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion.value,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion.value,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion.value % "test"
    ),

    akkaHttp := Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion.value,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion.value % "test"
    ),

    akkaHttpCirce := Seq(
      "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceVersion.value
    ),

    circe := Seq(
      "io.circe" %% "circe-generic" % circeVersion.value,
      "io.circe" %% "circe-parser" % circeVersion.value
    ),

    enumeratum := Seq(
      "com.beachape" %% "enumeratum" % enumeratumVersion.value,
      "com.beachape" %% "enumeratum-circe" % enumeratumCirceVersion.value
    ),

    scalatest := Seq(
      "org.scalatest" %% "scalatest" % scalatestVersion.value % "test"
    ),

    mockitoAll := Seq(
      "org.mockito" % "mockito-all" % mockitoAllVersion.value % "test"
    )
  )

}