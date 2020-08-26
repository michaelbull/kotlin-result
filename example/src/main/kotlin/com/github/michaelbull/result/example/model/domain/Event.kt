package com.github.michaelbull.result.example.model.domain

sealed class Event

object Created : Event()
class FirstNameChanged(val old: String, val new: String) : Event()
class LastNameChanged(val old: String, val new: String) : Event()
class EmailAddressChanged(val old: String, val new: String) : Event()
