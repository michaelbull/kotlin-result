package com.github.michaelbull.result.example.model.dto

import com.github.michaelbull.result.example.model.domain.Customer

/**
 * A [DTO](https://en.wikipedia.org/wiki/Data_transfer_object) sent over the network
 * that represents a [Customer].
 */
data class CustomerDto(
    var id: Long = 0L,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null
) {
    companion object {
        fun from(customer: Customer) = CustomerDto(
            id = customer.id.id,
            firstName = customer.name.first,
            lastName = customer.name.last,
            email = customer.email.address
        )
    }
}
