import sbt.Keys._
import sbt._
import sbt.plugins.{IvyPlugin, JvmPlugin}
import sbtassembly.AssemblyKeys.{assemblyJarName, assembly}

object CommonModule extends AutoPlugin {

  val javaVersion = "1.8"

  override def requires: Plugins = JvmPlugin && IvyPlugin && Dependencies

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Setting[_]] = Seq(
    organization := "com.pronvis",
    name := name.value,
    scalaVersion := "2.12.4",
    javacOptions ++= Seq("-source", javaVersion, "-target", javaVersion),
    scalacOptions += s"-target:jvm-$javaVersion",
    scalacOptions += "-Xfatal-warnings",

    logLevel in update := Level.Warn,

    cancelable in Global := true,
    parallelExecution in Test := true,
    fork in Test := true,
    fork in Compile := true,
    javaOptions += "-Djava.net.preferIPv4Stack=true",
    scalacOptions ++= Seq("-deprecation", "-feature", "-language:postfixOps"),

    assemblyJarName in assembly := name.value + "-" + version.value + "-jar-with-dependencies.jar"
  )
}