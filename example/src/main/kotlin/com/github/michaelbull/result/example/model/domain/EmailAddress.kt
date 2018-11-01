package com.github.michaelbull.result.example.model.domain

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

data class EmailAddress(
    val address: String
) {
    companion object {
        private const val MAX_LENGTH = 20
        private val PATTERN = ".+@.+\\..+".toRegex() // crude validation

        fun create(address: String?): Result<EmailAddress, DomainMessage> {
            return when {
                address.isNullOrBlank() -> Err(EmailRequired)
                address.length > MAX_LENGTH -> Err(EmailTooLong)
                !address.matches(PATTERN) -> Err(EmailInvalid)
                else -> Ok(EmailAddress(address))
            }
        }
    }
}

