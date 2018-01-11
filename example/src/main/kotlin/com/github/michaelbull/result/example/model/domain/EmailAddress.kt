package com.github.michaelbull.result.example.model.domain

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok

data class EmailAddress(
    val address: String
) {
    companion object {
        private val pattern = ".+@.+\\..+".toRegex() // crude validation

        fun create(address: String?) = when {
            address == null || address.isBlank() -> Err(DomainMessage.EmailRequired)
            address.length > 20 -> Err(DomainMessage.EmailTooLong)
            !address.matches(pattern) -> Err(DomainMessage.EmailInvalid)
            else -> Ok(EmailAddress(address))
        }
    }
}

