package com.pronvis.revolut.test

import com.pronvis.revolut.test.controllers.TransactionLogic
import com.pronvis.revolut.test.exceptions.{AccountDoesntExistsException, InsufficientFundException, SameAccountTransferException}
import com.pronvis.revolut.test.model.Account
import org.scalatest.EitherValues._
import org.scalatest.{FunSuite, MustMatchers}

class TransactionLogicTest extends FunSuite with MustMatchers {

  val petia = Account(2, "Petia", 456)
  val masha = Account(3, "Masha", 789)
  val acccounts = Seq(
    Account(1, "Vasia", 123),
    petia,
    masha,
    Account(4, "Praskovia", 555),
    Account(5, "Marfa", 666)
  )

  // ================================================================

  test("success transfer") {
    TransactionLogic.validateTransaction(2, 3, acccounts, 22).right.value mustBe (petia, masha)
  }

  test("transfer to same account not allowed") {
    TransactionLogic.validateTransaction(2, 2, acccounts, 22).left.value mustBe an[SameAccountTransferException]
  }

  test("transfer to nonexistent account") {
    TransactionLogic.validateTransaction(2, 20, acccounts, 22).left.value mustBe an[AccountDoesntExistsException]
  }

  test("transfer from nonexistent account") {
    TransactionLogic.validateTransaction(20, 2, acccounts, 22).left.value mustBe an[AccountDoesntExistsException]
  }

  test("not enough funds to transfer") {
    TransactionLogic.validateTransaction(2, 3, acccounts, 22000).left.value mustBe an[InsufficientFundException]
  }
}
