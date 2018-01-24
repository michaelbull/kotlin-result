package com.github.michaelbull.result.example.model.domain

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok

data class PersonalName(
    val first: String,
    val last: String
) {
    companion object {
        fun create(first: String?, last: String?) = when {
            first == null || first.isBlank() -> Err(FirstNameRequired)
            last == null || last.isBlank() -> Err(LastNameRequired)
            first.length > 10 -> Err(FirstNameTooLong)
            last.length > 10 -> Err(LastNameTooLong)
            else -> Ok(PersonalName(first, last))
        }
    }
}
