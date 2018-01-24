package com.github.michaelbull.result.example.model.domain

/**
 * All possible things that can happen in the use-cases
 */
sealed class DomainMessage

/* validation errors */

object CustomerRequired : DomainMessage()
object CustomerIdMustBePositive : DomainMessage()

object FirstNameRequired : DomainMessage()
object FirstNameTooLong : DomainMessage()

object LastNameRequired : DomainMessage()
object LastNameTooLong : DomainMessage()

object EmailRequired : DomainMessage()
object EmailTooLong : DomainMessage()
object EmailInvalid : DomainMessage()

/* events */

object CustomerCreated : DomainMessage()
class EmailAddressChanged(val old: String, val new: String) : DomainMessage()

/* exposed errors */

object CustomerNotFound : DomainMessage()

/* internal errors */

object SqlCustomerInvalid : DomainMessage()
object DatabaseTimeout : DomainMessage()
class DatabaseError(val reason: String?) : DomainMessage()
