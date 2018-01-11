package com.github.michaelbull.result.example.service

import com.github.michaelbull.result.*
import com.github.michaelbull.result.example.model.domain.Customer
import com.github.michaelbull.result.example.model.domain.CustomerId
import com.github.michaelbull.result.example.model.domain.DomainMessage
import com.github.michaelbull.result.example.model.entity.CustomerEntity
import java.sql.SQLTimeoutException

object CustomerService {
    private val repository = InMemoryCustomerRepository()

    fun getAll(): Result<Collection<Customer>, DomainMessage> {
        return Result.of(repository::findAll)
            .mapError(this::exceptionToDomainMessage)
            .andThen { result: Collection<CustomerEntity> ->
                Ok(result.map {
                    val customer = Customer.from(it)
                    when (customer) {
                        is Ok -> customer.value
                        is Err -> return customer
                    }
                })
            }
    }

    fun getById(id: CustomerId): Result<Customer, DomainMessage> {
        return getAll().andThen { findCustomer(it, id) }
    }

    fun upsert(customer: Customer): Result<DomainMessage?, DomainMessage> {
        val entity = CustomerEntity.from(customer)
        return getById(customer.id).mapBoth(
            { existing -> updateCustomer(entity, existing, customer) },
            { createCustomer(entity) }
        )
    }

    private fun updateCustomer(entity: CustomerEntity, old: Customer, new: Customer) =
        Result.of { repository.update(entity) }
            .map { differenceBetween(old, new) }
            .mapError(this::exceptionToDomainMessage)

    private fun createCustomer(entity: CustomerEntity) =
        Result.of { repository.insert(entity) }
            .mapError(this::exceptionToDomainMessage)
            .map { DomainMessage.CustomerCreated }

    private fun findCustomer(customers: Collection<Customer>, id: CustomerId): Result<Customer, DomainMessage.CustomerNotFound> {
        val customer = customers.find { it.id == id }
        return if (customer != null) Ok(customer) else Err(DomainMessage.CustomerNotFound)
    }

    private fun differenceBetween(old: Customer, new: Customer) = when {
        new.email != old.email -> DomainMessage.EmailAddressChanged(old.email.address, new.email.address)
        else -> null
    }

    private fun exceptionToDomainMessage(t: Throwable) = when (t) {
        is SQLTimeoutException -> DomainMessage.DatabaseTimeout
        else -> DomainMessage.DatabaseError(t.message)
    }
}
