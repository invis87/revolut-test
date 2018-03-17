package com.pronvis.revolut.test.model

import slick.dbio.Effect.Read
import slick.jdbc.JdbcProfile

class AccountsModel(protected val driver: JdbcProfile)
{

  import driver.api._

  class AccountsTable(tag: Tag) extends Table[AccountInternal](tag, "accounts") {
    def id       = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name     = column[String]("name")
    def amount   = column[BigDecimal]("amount")

    def * = (id, name, amount) <> ((AccountInternal.customApply _).tupled, AccountInternal.customUnapply)
  }

  lazy val accounts = TableQuery[AccountsTable]
  lazy val insertQuery = accounts returning accounts.map(_.id)

  def all(): DBIOAction[Seq[AccountInternal], NoStream, Read] = {
    accounts.result
  }

}

object AccountInternal {
  def customApply(id: Long, name: String, amount: BigDecimal): AccountInternal =
    new AccountInternal(id, Account(name, amount))

  def customUnapply(arg: AccountInternal): Option[(Long, String, BigDecimal)] =
    Some((arg.id, arg.account.name, arg.account.amount))
}

case class AccountInternal(id: Long, account: Account)

