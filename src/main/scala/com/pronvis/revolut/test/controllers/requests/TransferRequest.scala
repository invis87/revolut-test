package com.pronvis.revolut.test.controllers.requests

case class TransferRequest(from: Long, to: Long, amount: BigDecimal)
