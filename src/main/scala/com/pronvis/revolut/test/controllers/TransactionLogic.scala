package com.pronvis.revolut.test.controllers

import com.pronvis.revolut.test.exceptions.{AccountDoesntExistsException, InsufficientFundException, SameAccountTransferException}
import com.pronvis.revolut.test.model.Account

import scala.util.Try

object TransactionLogic {

  def validateTransaction(from: Long, to: Long, accounts: Seq[Account], amount: BigDecimal): Either[Throwable, (Account, Account)] = {
    Try {
      if(from == to) throw new SameAccountTransferException(s"Transfer funds to yourself($from) is not allowed!")
      val fromAcc = accounts.find(_.id == from).getOrElse(throw new AccountDoesntExistsException(s"Account '$from' doesn't exists!"))
      val toAcc = accounts.find(_.id == to).getOrElse(throw new AccountDoesntExistsException(s"Account '$to' doesn't exists!"))

      if (fromAcc.balance < amount) throw new InsufficientFundException(s"'${ fromAcc.name }' don't have enough money($amount) to send.")

      (fromAcc, toAcc)
    }.toEither
  }
}
