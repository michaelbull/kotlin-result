package com.github.michaelbull.result.example.service

import com.github.michaelbull.result.*
import com.github.michaelbull.result.example.model.domain.Customer
import com.github.michaelbull.result.example.model.domain.CustomerCreated
import com.github.michaelbull.result.example.model.domain.CustomerId
import com.github.michaelbull.result.example.model.domain.CustomerNotFound
import com.github.michaelbull.result.example.model.domain.DatabaseError
import com.github.michaelbull.result.example.model.domain.DatabaseTimeout
import com.github.michaelbull.result.example.model.domain.DomainMessage
import com.github.michaelbull.result.example.model.domain.EmailAddressChanged
import com.github.michaelbull.result.example.model.entity.CustomerEntity
import java.sql.SQLTimeoutException

object CustomerService {
    private val repository = InMemoryCustomerRepository()

    fun getAll(): Result<Collection<Customer>, DomainMessage> {
        return runCatching(repository::findAll)
            .mapError(::exceptionToDomainMessage)
            .mapAll(Customer.Companion::from)
    }

    fun getById(id: CustomerId): Result<Customer, DomainMessage> {
        return getAll().andThenRun{ findCustomer(id) }
    }

    fun upsert(customer: Customer): Result<DomainMessage?, DomainMessage> {
        val entity = CustomerEntity.from(customer)
        return getById(customer.id).mapBoth(
            success = { existing -> updateCustomer(entity, existing, customer) },
            failure = { createCustomer(entity) }
        )
    }

    private fun updateCustomer(entity: CustomerEntity, old: Customer, new: Customer) =
        runCatching { repository.update(entity) }
            .map { differenceBetween(old, new) }
            .mapError(::exceptionToDomainMessage)

    private fun createCustomer(entity: CustomerEntity) =
        runCatching { repository.insert(entity) }
            .map { CustomerCreated }
            .mapError(::exceptionToDomainMessage)

    private fun Collection<Customer>.findCustomer(id: CustomerId): Result<Customer, CustomerNotFound> {
        return find { it.id == id }.toResultOr { CustomerNotFound }
    }

    private fun differenceBetween(old: Customer, new: Customer): EmailAddressChanged? {
        return if (new.email != old.email) {
            EmailAddressChanged(old.email.address, new.email.address)
        } else {
            null
        }
    }

    private fun exceptionToDomainMessage(t: Throwable) = when (t) {
        is SQLTimeoutException -> DatabaseTimeout
        else -> DatabaseError(t.message)
    }
}
