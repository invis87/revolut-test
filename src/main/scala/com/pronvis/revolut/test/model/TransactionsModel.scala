package com.pronvis.revolut.test.model

import slick.dbio.Effect.{Read, Write}
import slick.jdbc.JdbcProfile

class TransactionsModel(protected val driver: JdbcProfile) {

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

  def addTransaction(from: Long, to: Long, amount: BigDecimal): DBIOAction[Long, NoStream, Write] = {
    insertQuery += Transaction(0, from, to, amount)
  }

  def all: DBIOAction[Seq[Transaction], NoStream, Read] = {
    transactions.result
  }

  //todo: findById, findByParticipant, etc.

}

case class Transaction(id: Long, from: Long, to: Long, amount: BigDecimal)