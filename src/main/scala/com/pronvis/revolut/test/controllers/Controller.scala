package com.pronvis.revolut.test.controllers

import akka.http.scaladsl.server.Route

trait Controller {
  def route: Route
}