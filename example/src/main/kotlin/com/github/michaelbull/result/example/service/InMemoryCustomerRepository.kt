package com.github.michaelbull.result.example.service

import com.github.michaelbull.result.example.model.domain.repository.CustomerRepository
import com.github.michaelbull.result.example.model.entity.CustomerEntity
import java.sql.SQLException
import java.sql.SQLTimeoutException

class InMemoryCustomerRepository : CustomerRepository {
    private val table = mutableMapOf(
        5L to CustomerEntity(5L, "Michael", "Bull", "example@email.com")
    )

    override fun findAll(): Collection<CustomerEntity> {
        return table.values
    }

    override fun update(entity: CustomerEntity) {
        val id = entity.id

        if (id !in table) {
            throw SQLException("No customer found for id $id")
        } else {
            setOrTimeout(id, entity)
        }
    }

    override fun insert(entity: CustomerEntity) {
        val id = entity.id

        if (id in table) {
            throw SQLException("Customer already exists with id $id")
        } else {
            setOrTimeout(id, entity)
        }
    }

    private fun setOrTimeout(id: Long, entity: CustomerEntity) {
        if (id == 42L) {
            throw SQLTimeoutException()
        } else {
            table[id] = entity
        }
    }
}
