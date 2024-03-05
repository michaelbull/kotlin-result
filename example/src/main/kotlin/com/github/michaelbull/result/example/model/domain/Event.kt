package com.github.michaelbull.result.example.model.domain

sealed interface Event

data object Created : Event
data class FirstNameChanged(val old: String, val new: String) : Event
data class LastNameChanged(val old: String, val new: String) : Event
data class EmailAddressChanged(val old: String, val new: String) : Event
