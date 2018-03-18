package com.pronvis.revolut.test.model

import com.pronvis.revolut.test.controllers.requests.CreateAccountRequest
import com.pronvis.revolut.test.exceptions.BusinessException
import com.typesafe.scalalogging.LazyLogging
import slick.dbio.DBIOAction

import scala.concurrent.{ExecutionContext, Future}

class AccountsMiddleware(
  database: slick.jdbc.JdbcBackend#Database,
  accountsModel: AccountsModel,
  transactionsModel: TransactionsModel)
  (implicit executionContext: ExecutionContext) extends LazyLogging {

  def getAll: Future[Seq[Account]] = {
    database.run(accountsModel.all)
  }

  def addAccount(newAccount: CreateAccountRequest): Future[Long] = {
    val dbAction = accountsModel.find(newAccount.name).flatMap {
      case None    => accountsModel.addAccount(newAccount.name, newAccount.balance)
      case Some(_) => DBIOAction.failed(new BusinessException(s"Account with name '${newAccount.name}' already exists."))
    }

    database.run(dbAction)
  }

  def transferFunds(from: Long, to: Long, amount: BigDecimal): Future[Long] = {
    val dbAction = accountsModel.find(Set(from, to)).flatMap { accs =>
      if(accs.length != 2 ) DBIOAction.failed(new BusinessException("Not enought accounts to transfer money."))
      val fromAcc = accs.find(_.id == from).get
      val toAcc = accs.find(_.id == to).get
      if(fromAcc.balance < amount) {
        DBIOAction.failed(new BusinessException(s"'${fromAcc.name}' don't have enough money($amount) to send."))
      }
        accountsModel.transferTransactionally(fromAcc, toAcc, amount)
          .flatMap(_ => transactionsModel.addTransaction(from, to, amount))
    }

    database.run(dbAction)
  }

  def getAllTransactions: Future[Seq[Transaction]] = {
    database.run(transactionsModel.all)
  }

}
