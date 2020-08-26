package com.github.michaelbull.result.example.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.example.model.domain.DomainMessage
import com.github.michaelbull.result.example.model.domain.EmailAddress
import com.github.michaelbull.result.example.model.domain.EmailInvalid
import com.github.michaelbull.result.example.model.domain.EmailRequired
import com.github.michaelbull.result.example.model.domain.EmailTooLong

object EmailAddressParser {

    private const val MAX_LENGTH = 20
    private val PATTERN = ".+@.+\\..+".toRegex() // crude validation

    fun parse(address: String?): Result<EmailAddress, DomainMessage> {
        return when {
            address.isNullOrBlank() -> Err(EmailRequired)
            address.length > MAX_LENGTH -> Err(EmailTooLong)
            !address.matches(this.PATTERN) -> Err(EmailInvalid)
            else -> Ok(EmailAddress(address))
        }
    }
}
