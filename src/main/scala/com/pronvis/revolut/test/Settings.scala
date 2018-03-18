package com.pronvis.revolut.test

import com.typesafe.config.{Config, ConfigValue}
import slick.jdbc.JdbcProfile

import scala.collection.JavaConverters._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

class Settings(config: Config) {

  final val REVOLUT = "revolut"

  val httpInterface: String         = config.getString(s"$REVOLUT.http.interface")
  val httpPort: Int                 = config.getInt(s"$REVOLUT.http.port")
  val queryTimeout: FiniteDuration  = config.getInt(s"$REVOLUT.query.timeoutSec").seconds
  val storageSchema: String         = config.getString(s"$REVOLUT.storage.schema")
  val migrationsDirs: Seq[String]   = config.getString(s"$REVOLUT.storage.migrations.dir").split(",")

  private val profileClass = Class.forName(config.getString("revolut.storage.profile"))
  val jdbcProfile: JdbcProfile = profileClass.getField("MODULE$").get(profileClass).asInstanceOf[slick.jdbc.JdbcProfile]

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
