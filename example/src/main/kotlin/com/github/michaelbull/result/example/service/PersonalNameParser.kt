package com.github.michaelbull.result.example.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.example.model.domain.DomainMessage
import com.github.michaelbull.result.example.model.domain.FirstNameRequired
import com.github.michaelbull.result.example.model.domain.FirstNameTooLong
import com.github.michaelbull.result.example.model.domain.LastNameRequired
import com.github.michaelbull.result.example.model.domain.LastNameTooLong
import com.github.michaelbull.result.example.model.domain.PersonalName

object PersonalNameParser {

    private const val MAX_LENGTH = 10

    fun parse(first: String?, last: String?): Result<PersonalName, DomainMessage> {
        return when {
            first.isNullOrBlank() -> Err(FirstNameRequired)
            last.isNullOrBlank() -> Err(LastNameRequired)
            first.length > MAX_LENGTH -> Err(FirstNameTooLong)
            last.length > MAX_LENGTH -> Err(LastNameTooLong)
            else -> Ok(PersonalName(first, last))
        }
    }
}
