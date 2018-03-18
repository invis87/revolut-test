package com.pronvis.revolut.test.model

import com.pronvis.revolut.test.exceptions.BusinessException
import slick.dbio.Effect.Write
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class AccountsModel(
  protected val db: slick.jdbc.JdbcBackend#Database,
  protected val driver: JdbcProfile)
  (implicit ec: ExecutionContext) {

  import driver.api._

  class AccountsTable(tag: Tag) extends Table[Account](tag, "accounts") {
    def id       = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name     = column[String]("name")
    def balance  = column[BigDecimal]("balance")

    def * = (id, name, balance) <> ((Account.apply _).tupled, Account.unapply)
  }

  lazy val accounts = TableQuery[AccountsTable]
  lazy val insertQuery = accounts returning accounts.map(_.id)

  def all: Future[Seq[Account]] = db.run {
    accounts.result
  }

  def addAccount(name: String, balance: BigDecimal): Future[Long] = db.run {
    insertQuery += Account(0, name, balance)
  }

  private def updateAction(acc: Account): DBIOAction[Int, NoStream, Write] = {
    accounts.insertOrUpdate(acc)
  }

  def update(acc: Account): Future[Int] = db.run  {
    updateAction(acc)
  }

  def find(name: String): Future[Option[Account]] = db.run {
    accounts.filter(_.name === name).result.headOption
  }

  private def findQuery(ids: Set[Long]) = {
    accounts.filter(_.id inSet ids)
  }

  def find(ids: Set[Long]): Future[Seq[Account]] = db.run {
    findQuery(ids).result
  }

  def transferTransactionally(from: Long, to: Long, amount: BigDecimal): Future[Unit] = db.run {
    findQuery(Set(from, to)).forUpdate.result.flatMap { accs =>
      val fromAcc = accs.find(_.id == from).getOrElse(throw new BusinessException(s"Account '$from' doesn't exists!"))
      val toAcc = accs.find(_.id == to).getOrElse(throw new BusinessException(s"Account '$to' doesn't exists!"))
      if(fromAcc.balance < amount) throw new BusinessException(s"'${fromAcc.name}' don't have enough money($amount) to send.")

      for {
        _ <- updateAction(fromAcc.copy(balance = fromAcc.balance - amount))
        _ <- updateAction(toAcc.copy(balance = toAcc.balance + amount))
      } yield ()
    }.transactionally
  }

}