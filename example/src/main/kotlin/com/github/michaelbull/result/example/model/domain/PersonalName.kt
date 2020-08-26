package com.github.michaelbull.result.example.model.domain

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

data class PersonalName(
    val first: String,
    val last: String
)

private const val MAX_LENGTH = 10

fun Pair<String?, String?>.toPersonalName(): Result<PersonalName, DomainMessage> {
    val (first, last) = this

    return when {
        first.isNullOrBlank() -> Err(FirstNameRequired)
        last.isNullOrBlank() -> Err(LastNameRequired)
        first.length > MAX_LENGTH -> Err(FirstNameTooLong)
        last.length > MAX_LENGTH -> Err(LastNameTooLong)
        else -> Ok(PersonalName(first, last))
    }
}
