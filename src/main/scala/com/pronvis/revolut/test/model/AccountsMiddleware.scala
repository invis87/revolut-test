package com.pronvis.revolut.test.model

import com.pronvis.revolut.test.controllers.requests.CreateAccountRequest
import com.pronvis.revolut.test.exceptions.BusinessException
import com.typesafe.scalalogging.LazyLogging
import slick.dbio.{DBIOAction, Effect, NoStream}

import scala.concurrent.{ExecutionContext, Future}

class AccountsMiddleware(
  database: slick.jdbc.JdbcBackend#Database,
  accountsModel: AccountsModel)
  (implicit executionContext: ExecutionContext) extends LazyLogging {

  def getAll: Future[Seq[Account]] = {
    database.run(accountsModel.all)
  }

  def addAccount(newAccount: CreateAccountRequest): Future[Long] = {
    val dbAction: DBIOAction[Long, NoStream, Effect.Write with Effect.Read ] = accountsModel.find(newAccount.name).flatMap {
      case None    => accountsModel.addAccount(newAccount.name, newAccount.balance)
      case Some(_) => DBIOAction.failed(new BusinessException(s"Account with name '${newAccount.name}' already exists"))
    }

    database.run(dbAction)
  }

}
