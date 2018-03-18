package com.pronvis.revolut.test.model

import slick.dbio.Effect.{Read, Write}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class AccountsModel(protected val driver: JdbcProfile)(implicit ec: ExecutionContext) {

  import driver.api._

  class AccountsTable(tag: Tag) extends Table[Account](tag, "accounts") {
    def id       = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name     = column[String]("name")
    def balance  = column[BigDecimal]("balance")

    def * = (id, name, balance) <> ((Account.apply _).tupled, Account.unapply)
  }

  lazy val accounts = TableQuery[AccountsTable]
  lazy val insertQuery = accounts returning accounts.map(_.id)

  def all: DBIOAction[Seq[Account], NoStream, Read] = {
    accounts.result
  }

  def addAccount(name: String, balance: BigDecimal): DBIOAction[Long, NoStream, Write] = {
    insertQuery += Account(0, name, balance)
  }

  def update(acc: Account): DBIOAction[Int, NoStream, Write] = {
    accounts.insertOrUpdate(acc)
  }

  def find(name: String): DBIOAction[Option[Account], NoStream, Read] = {
    accounts.filter(_.name === name).result.headOption
  }

  def find(ids: Set[Long]): DBIOAction[Seq[Account], NoStream, Read] = {
    accounts.filter(_.id inSet ids).result
  }

  def transferTransactionally(from: Account, toAccount: Account, amount: BigDecimal):
  DBIOAction[Unit, NoStream, Read with Write with Write with Effect.Transactional] = {
    val action = for {
      _ <- accounts.filter(acc => acc.id === from.id || acc.id === toAccount.id).forUpdate.result
      _ <- update(from.copy(balance = from.balance - amount))
      _ <- update(toAccount.copy(balance = toAccount.balance + amount))
    } yield ()

    action.transactionally
  }

}