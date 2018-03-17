package com.pronvis.revolut.test.controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.pronvis.revolut.test.Settings
import com.pronvis.revolut.test.model.AccountsMiddleware
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future, TimeoutException}

class AccountsController(accountsMiddleware: AccountsMiddleware)(
  implicit system: ActorSystem,
  mat: Materializer,
  ec: ExecutionContext,
  settings: Settings) extends Controller with FailFastCirceSupport with LazyLogging {

  def route: Route = {
    path("accounts") {
      get {
        val allAccs = accountsMiddleware.getAll
        complete(allAccs)
      }
    }
  }

  //-------------------------------------------------------------------------
  def delayed(queryName: String) =
    akka.pattern.after(settings.queryTimeout, using = system.scheduler)(Future.failed(
      new TimeoutException(s"[$queryName] query did not complete within ${ settings.queryTimeout }")))
}