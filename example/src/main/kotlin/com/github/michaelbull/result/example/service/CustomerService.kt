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
        return getAll().andThen { it.findCustomer(id) }
    }

    fun upsert(customer: Customer): Result<DomainMessage?, DomainMessage> {
        val entity = CustomerEntity.from(customer)

        return getById(customer.id).mapBoth(
            success = { existing ->
                Result.of { repository.update(entity) }
                    .mapError(this::exceptionToDomainMessage)
                    .map {
                        if (customer.email != existing.email) {
                            DomainMessage.EmailAddressChanged(existing.email.address, customer.email.address)
                        } else {
                            null
                        }
                    }
            },
            failure = {
                Result.of { repository.insert(entity) }
                    .mapError(this::exceptionToDomainMessage)
                    .map { DomainMessage.CustomerCreated }
            }
        )
    }

    private fun Collection<Customer>.findCustomer(id: CustomerId): Result<Customer, DomainMessage.CustomerNotFound> {
        val customer = find { it.id == id }
        return if (customer != null) Ok(customer) else Err(DomainMessage.CustomerNotFound)
    }

    private fun exceptionToDomainMessage(it: Throwable): DomainMessage {
        return when (it) {
            is SQLTimeoutException -> DomainMessage.DatabaseTimeout
            else -> DomainMessage.DatabaseError(it.message)
        }
    }
}
