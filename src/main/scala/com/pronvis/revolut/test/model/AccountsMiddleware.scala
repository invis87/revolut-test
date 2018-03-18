package com.pronvis.revolut.test.model

import com.pronvis.revolut.test.controllers.requests.CreateAccountRequest
import com.pronvis.revolut.test.exceptions.BusinessException
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class AccountsMiddleware(
  accountsModel: AccountsModel,
  transactionsModel: TransactionsModel)
  (implicit executionContext: ExecutionContext) extends LazyLogging {

  def getAll: Future[Seq[Account]] = {
    accountsModel.all
  }

  def get(from: Long, to: Long): Future[Seq[Account]] = {
    accountsModel.find(Set(from, to))
  }

  def addAccount(newAccount: CreateAccountRequest): Future[Long] = {
    accountsModel.find(newAccount.name).flatMap {
      case None    => accountsModel.addAccount(newAccount.name, newAccount.balance)
      case Some(_) => Future.failed(new BusinessException(s"Account with name '${newAccount.name}' already exists."))
    }
  }

  def transferFunds(from: Long, to: Long, amount: BigDecimal): Future[Long] = {
    accountsModel.transferTransactionally(from, to, amount)
      .flatMap(_ => transactionsModel.addTransaction(from, to, amount))
  }

  def getAllTransactions: Future[Seq[Transaction]] = {
    transactionsModel.all
  }

}
