package com.pronvis.revolut.test

import javax.sql.DataSource
import org.flywaydb.core.Flyway
import slick.jdbc.DataSourceJdbcDataSource
import slick.jdbc.hikaricp.HikariCPJdbcDataSource

object SchemaMigration {

  def migrate(
    slickDatasource: slick.jdbc.JdbcDataSource,
    dir: Seq[String],
    schema: String,
    baseline: Boolean = false,
    shutdown: => Unit = {}
    ): Unit = {

    val flyway = new Flyway()
    val ds: DataSource = slickDatasource match {
      case d: DataSourceJdbcDataSource =>
        d.ds
      case d: HikariCPJdbcDataSource =>
        d.ds
      case other =>
        throw new IllegalStateException("Unknown DataSource type: " + other)
    }

    flyway.setDataSource(ds)
    flyway.setLocations(dir: _*)
    flyway.setSchemas(schema)

    try {
      flyway.migrate()
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        shutdown
    }
  }
}