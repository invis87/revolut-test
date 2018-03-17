package com.pronvis.revolut.test.model

import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.JdbcBackend

import scala.concurrent.Future

class AccountsMiddleware(
  database: slick.jdbc.JdbcBackend#Database,
  accountsModel: AccountsModel) extends LazyLogging {

  def getAll: Future[Seq[AccountInternal]] = {
    database.run(accountsModel.all())

  }


}
