package com.github.michaelbull.result.example.model.domain

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok

data class CustomerId(val id: Long) {
    companion object {
        fun create(id: Long?) = when {
            id == null -> Err(CustomerRequired)
            id < 1 -> Err(CustomerIdMustBePositive)
            else -> Ok(CustomerId(id))
        }
    }
}
