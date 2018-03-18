package com.pronvis.revolut.test.controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.pronvis.revolut.test.controllers.requests.CreateAccountRequest
import com.pronvis.revolut.test.model.AccountsMiddleware
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class AccountsController(accountsMiddleware: AccountsMiddleware, queryTimeout: FiniteDuration)(
  implicit system: ActorSystem,
  ec: ExecutionContext) extends TimeoutController(queryTimeout) with FailFastCirceSupport with LazyLogging {

  def timeoutedRoute: Route = {
    path("accounts") {
      get {
        complete(accountsMiddleware.getAll)
      } ~
        post {
          entity(as[CreateAccountRequest]) { newAccount =>
            complete(accountsMiddleware.addAccount(newAccount))
          }
        }

    }

  }


}