package com.pronvis.revolut.test.utils

import java.util.concurrent.TimeoutException

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{complete, extractRequest, extractRequestContext, extractUri}
import akka.http.scaladsl.server.{ExceptionHandler, MalformedRequestContentRejection, RejectionHandler}
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._
import io.circe.syntax._

trait ErrorHelper extends LazyLogging {

  implicit val globalRejectionHandler: RejectionHandler = RejectionHandler
    .newBuilder()
    .handle {
      case _@MalformedRequestContentRejection(msg, cause) =>
        extractRequestContext { ctx =>
          extractRequest { req =>
            logger.warn(s"Reject request $req, msg: $msg, cause: $cause")
            complete(ErrorHelper.errorResponse(StatusCodes.BadRequest, msg))
          }
        }
    }
    .result()
    .withFallback(RejectionHandler.default.mapRejectionResponse {
      case res @ HttpResponse(_, _, ent: HttpEntity.Strict, _) =>
        res.copy(entity = HttpEntity(
          ContentTypes.`application/json`,
          ErrorMessage(message = ent.data.utf8String).asJson.noSpaces)
        )
      case x => x // pass through all other types of responses
    })

  implicit val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case ex: Throwable =>
      extractUri { uri =>
        extractRequest { request =>
          logger.error(s"Request to $uri could not be handled normally, request: $request", ex)
          complete(ErrorHelper.errorResponse(StatusCodes.InternalServerError, ex))
        }
      }
  }
}

object ErrorHelper {
  def errorResponse(statusCode: StatusCode, message: String): HttpResponse = {
    HttpResponse(status = statusCode,
      entity = HttpEntity(ContentTypes.`application/json`,
        ErrorMessage(message).asJson.noSpaces))
  }

  def errorResponse(statusCode: StatusCode, throwable: Throwable): HttpResponse = {
    HttpResponse(
      status = statusCode,
      entity = HttpEntity(ContentTypes.`application/json`,
        ErrorMessage(throwable).asJson.noSpaces))
  }
}

case class ErrorMessage(message: String)
object ErrorMessage {

  def apply(ex: Throwable): ErrorMessage = {
    val message = ex match {
      case _: TimeoutException => "Timeout"
      case _ => "Internal error"
    }
    ErrorMessage(message)
  }
}