package com.pronvis.revolut.test.exceptions

abstract class RevolutTestException(message: String) extends Throwable(message)
class BusinessException(message: String) extends RevolutTestException(message)
class SameAccountTransferException(message: String) extends RevolutTestException(message)
class AccountDoesntExistsException(message: String) extends RevolutTestException(message)
class InsufficientFundException(message: String) extends RevolutTestException(message)