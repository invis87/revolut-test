package com.pronvis.revolut.test

import com.typesafe.config.{Config, ConfigValue}

import scala.collection.JavaConverters._

class Settings(config: Config) {

  final val REVOLUT = "revolut"

  val httpInterface: String   = config.getString(s"$REVOLUT.http.interface")
  val httpPort: Int           = config.getInt(s"$REVOLUT.http.port")

  //helper methods

  implicit def ordering: Ordering[java.util.Map.Entry[String, ConfigValue]] =
    Ordering.fromLessThan(_.getKey < _.getKey)

  override def toString: String =
    getAllProps.filter(_.getKey.contains(REVOLUT)).mkString("\n")

  def toStringAll: String =
    getAllProps.mkString("\n")

  private def getAllProps =
    config.entrySet().asScala.toList.sorted
}
