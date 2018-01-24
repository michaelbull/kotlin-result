package com.github.michaelbull.result.example.model.domain

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok

data class EmailAddress(
    val address: String
) {
    companion object {
        private val pattern = ".+@.+\\..+".toRegex() // crude validation

        fun create(address: String?) = when {
            address == null || address.isBlank() -> Err(EmailRequired)
            address.length > 20 -> Err(EmailTooLong)
            !address.matches(pattern) -> Err(EmailInvalid)
            else -> Ok(EmailAddress(address))
        }
    }
}

