package com.pronvis.revolut.test.controllers

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.pronvis.revolut.test.utils.ErrorMessage

import scala.concurrent.duration.FiniteDuration
import io.circe.generic.auto._
import io.circe.syntax._

trait Controller {
  def route: Route
}

abstract class TimeoutController(queryTimeout: FiniteDuration) extends Controller {
  override def route: Route = {
    withRequestTimeout(queryTimeout) {
      withRequestTimeoutResponse(_ => HttpResponse(
        status = StatusCodes.InternalServerError,
        entity = HttpEntity(ContentTypes.`application/json`,
          ErrorMessage("Timeout").asJson.noSpaces)))
      {
        timeoutedRoute
      }
    }
  }

  def timeoutedRoute: Route
}