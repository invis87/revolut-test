package com.pronvis.revolut.test.model

import slick.dbio.Effect.{Read, Write}
import slick.jdbc.JdbcProfile

class AccountsModel(protected val driver: JdbcProfile) {

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

  def find(name: String): DBIOAction[Option[Account], NoStream, Read] = {
    accounts.filter(_.name === name).result.headOption
  }

}