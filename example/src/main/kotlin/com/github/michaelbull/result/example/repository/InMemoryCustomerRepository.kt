package com.github.michaelbull.result.example.repository

import com.github.michaelbull.result.example.model.entity.CustomerEntity
import com.github.michaelbull.result.example.model.entity.CustomerId
import java.sql.SQLTimeoutException

class InMemoryCustomerRepository(
    private val customers: MutableMap<CustomerId, CustomerEntity>,
) : CustomerRepository {

    override fun findById(id: CustomerId): CustomerEntity? {
        return customers.entries.find { (key) -> key == id }?.value
    }

    override fun save(entity: CustomerEntity) {
        val id = entity.id

        if (id == TIMEOUT_CUSTOMER_ID) {
            throw SQLTimeoutException()
        } else {
            customers[id] = entity
        }
    }

    private companion object {
        private val TIMEOUT_CUSTOMER_ID = CustomerId(42L)
    }
}
