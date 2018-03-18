package com.pronvis.revolut.test

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.pronvis.revolut.test.controllers.{AccountsController, Controller}
import com.pronvis.revolut.test.model.{AccountsMiddleware, AccountsModel}
import com.pronvis.revolut.test.utils.ErrorHelper
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

object Application extends App with LazyLogging with ErrorHelper {

  // ============ CONFIGURATION ============

  private final val SERVICE_NAME = "revolut-test"
  private val config = ConfigFactory.load()

  implicit lazy val system: ActorSystem = ActorSystem(SERVICE_NAME, config)
  implicit val settings: Settings = new Settings(config)
  implicit lazy val ec: ExecutionContext = system.dispatcher
  implicit lazy val mat: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(system))

  // ============ DATABASE ============

  implicit val db: JdbcBackend.Database = Database.forConfig(path = "revolut.storage", config = config)
  SchemaMigration.migrate(
    slickDatasource = db.source,
    dir = settings.migrationsDirs,
    schema = settings.storageSchema,
    baseline = true,
    shutdown = { shutdownHook; sys.exit(1) })

  // ============ CONTROLLERS ============

  val accountsModel = new AccountsModel(settings.jdbcProfile)
  val accountsMiddleware = new AccountsMiddleware(db, accountsModel)

  val controllers = Seq(
    new AccountsController(accountsMiddleware, settings.queryTimeout)
  )

  // ============ START SERVER ============

  logger.info(s"Starting '$SERVICE_NAME' http server with settings:\n$settings")
  val binding = Http().bindAndHandle(
    routeHandlers,
    settings.httpInterface,
    settings.httpPort)

  binding.onComplete {
    case Success(b) ⇒
      val bindAddress = b.localAddress
      logger.info(s"Listening on ${ bindAddress.getHostName }:${ bindAddress.getPort }")
    case Failure(e) ⇒
      logger.error(s"Binding failed with ${ e.getMessage }")
      system.terminate()
  }

  sys addShutdownHook shutdownHook

  lazy val shutdownHook: Unit = {
    logger.info("Shutting down application...")
    val whenTerminated = system.terminate()
    Await.result(whenTerminated, 5 seconds)
    logger.info("Application shut down.")
  }

  // ============ HELPER ============

  def routeHandlers: Route = controllersToRoute(controllers)

  private def controllersToRoute(controllers: Iterable[Controller]): Route = controllers.map(_.route).reduce(_ ~ _ )
}
