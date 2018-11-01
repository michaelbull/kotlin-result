package com.github.michaelbull.result.example.model.domain

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

data class PersonalName(
    val first: String,
    val last: String
) {
    companion object {
        private const val MAX_LENGTH = 10

        fun create(first: String?, last: String?): Result<PersonalName, DomainMessage> {
            return when {
                first.isNullOrBlank() -> Err(FirstNameRequired)
                last.isNullOrBlank() -> Err(LastNameRequired)
                first.length > MAX_LENGTH -> Err(FirstNameTooLong)
                last.length > MAX_LENGTH -> Err(LastNameTooLong)
                else -> Ok(PersonalName(first, last))
            }
        }
    }
}
