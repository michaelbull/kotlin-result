package com.github.michaelbull.result.example.model.domain

/**
 * All possible things that can happen in the use-cases
 */
sealed interface DomainMessage

/* validation errors */

data object CustomerRequired : DomainMessage
data object CustomerIdMustBePositive : DomainMessage

data object FirstNameRequired : DomainMessage
data object FirstNameTooLong : DomainMessage

data object LastNameRequired : DomainMessage
data object LastNameTooLong : DomainMessage

data object EmailRequired : DomainMessage
data object EmailTooLong : DomainMessage
data object EmailInvalid : DomainMessage

/* exposed errors */

data object CustomerNotFound : DomainMessage

/* internal errors */

data object SqlCustomerInvalid : DomainMessage
data object DatabaseTimeout : DomainMessage
data class DatabaseError(val reason: String?) : DomainMessage
