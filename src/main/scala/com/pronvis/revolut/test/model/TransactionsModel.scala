package com.pronvis.revolut.test.model

import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class TransactionsModel(
  protected val db: slick.jdbc.JdbcBackend#Database,
  protected val driver: JdbcProfile) {

  import driver.api._

  class TransactionsTable(tag: Tag) extends Table[Transaction](tag, "transactions") {
    def id       = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def from     = column[Long]("from")
    def to       = column[Long]("to")
    def amount   = column[BigDecimal]("amount")

    def * = (id, from, to, amount) <> ((Transaction.apply _).tupled, Transaction.unapply)
  }

  lazy val transactions = TableQuery[TransactionsTable]
  lazy val insertQuery = transactions returning transactions.map(_.id)

  def addTransaction(from: Long, to: Long, amount: BigDecimal): Future[Long] = db.run {
    insertQuery += Transaction(0, from, to, amount)
  }

  def all: Future[Seq[Transaction]] = db.run {
    transactions.result
  }

  //todo: findById, findByParticipant, etc.

}

case class Transaction(id: Long, from: Long, to: Long, amount: BigDecimal)