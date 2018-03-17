package com.pronvis.revolut.test.controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives.{parameter, _}
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future, TimeoutException}

class AccountsController()(
  implicit system: ActorSystem,
  mat: Materializer,
  ec: ExecutionContext) extends Controller with FailFastCirceSupport with LazyLogging {

  def route: Route = {
    path("accounts") {
      get {
        val allAccs = Seq(1, 2, 3, 4)
        complete(allAccs)
      }
    } ~ path("timeout") { get { throw new TimeoutException("hello from timeout") } }
  }


}