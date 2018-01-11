package com.github.michaelbull.result.example.model.entity

import com.github.michaelbull.result.example.model.domain.Customer

/**
 * Represents an [Entity](https://docs.oracle.com/cd/E17277_02/html/collections/tutorial/Entity.html)
 * mapped to a table in a database.
 */
data class CustomerEntity(
    var id: Long = 0L,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null
) {
    companion object {
        fun from(customer: Customer) = CustomerEntity(
            id = customer.id.id,
            firstName = customer.name.first,
            lastName = customer.name.last,
            email = customer.email.address
        )
    }
}
