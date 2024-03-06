package com.github.michaelbull.result.example.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.example.model.domain.Created
import com.github.michaelbull.result.example.model.domain.Customer
import com.github.michaelbull.result.example.model.domain.CustomerIdMustBePositive
import com.github.michaelbull.result.example.model.domain.CustomerNotFound
import com.github.michaelbull.result.example.model.domain.CustomerRequired
import com.github.michaelbull.result.example.model.domain.DatabaseError
import com.github.michaelbull.result.example.model.domain.DatabaseTimeout
import com.github.michaelbull.result.example.model.domain.DomainMessage
import com.github.michaelbull.result.example.model.domain.EmailAddressChanged
import com.github.michaelbull.result.example.model.domain.Event
import com.github.michaelbull.result.example.model.domain.FirstNameChanged
import com.github.michaelbull.result.example.model.domain.LastNameChanged
import com.github.michaelbull.result.example.model.dto.CustomerDto
import com.github.michaelbull.result.example.model.entity.CustomerEntity
import com.github.michaelbull.result.example.model.entity.CustomerId
import com.github.michaelbull.result.example.repository.CustomerRepository
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import com.github.michaelbull.result.toResultOr
import com.github.michaelbull.result.zip
import java.sql.SQLTimeoutException

class CustomerService(
    private val repository: CustomerRepository,
) {

    fun getById(id: Long): Result<CustomerDto, DomainMessage> {
        return parseCustomerId(id)
            .andThen(::findById)
            .map(::entityToDto)
    }

    fun save(id: Long, dto: CustomerDto): Result<Event?, DomainMessage> {
        return parseCustomerId(id)
            .andThen { upsert(it, dto) }
    }

    private fun parseCustomerId(id: Long?) = when {
        id == null -> Err(CustomerRequired)
        id < 1 -> Err(CustomerIdMustBePositive)
        else -> Ok(CustomerId(id))
    }

    private fun entityToDto(entity: CustomerEntity): CustomerDto {
        return CustomerDto(
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email
        )
    }

    private fun findById(id: CustomerId): Result<CustomerEntity, CustomerNotFound> {
        return repository.findById(id)
            .toResultOr { CustomerNotFound }
    }

    private fun upsert(id: CustomerId, dto: CustomerDto): Result<Event?, DomainMessage> {
        val existingCustomer = repository.findById(id)

        return if (existingCustomer != null) {
            update(existingCustomer, dto)
        } else {
            insert(id, dto)
        }
    }

    private fun update(entity: CustomerEntity, dto: CustomerDto): Result<Event?, DomainMessage> {
        val validated = validate(dto).getOrElse { return Err(it) }

        val updated = entity.copy(
            firstName = validated.name.first,
            lastName = validated.name.last,
            email = validated.email.address
        )

        return runCatching { repository.save(updated) }
            .map { compare(entity, updated) }
            .mapError(::exceptionToDomainMessage)
    }

    private fun insert(id: CustomerId, dto: CustomerDto): Result<Created, DomainMessage> {
        val entity = createEntity(id, dto).getOrElse { return Err(it) }

        return runCatching { repository.save(entity) }
            .map { Created }
            .mapError(::exceptionToDomainMessage)
    }

    private fun validate(dto: CustomerDto): Result<Customer, DomainMessage> {
        return zip(
            { PersonalNameParser.parse(dto.firstName, dto.lastName) },
            { EmailAddressParser.parse(dto.email) },
            ::Customer
        )
    }

    private fun createEntity(id: CustomerId, dto: CustomerDto): Result<CustomerEntity, DomainMessage> {
        return zip(
            { PersonalNameParser.parse(dto.firstName, dto.lastName) },
            { EmailAddressParser.parse(dto.email) },
            { (first, last), (address) -> CustomerEntity(id, first, last, address) }
        )
    }

    private fun exceptionToDomainMessage(t: Throwable) = when (t) {
        is SQLTimeoutException -> DatabaseTimeout
        else -> DatabaseError(t.message)
    }

    private fun compare(old: CustomerEntity, new: CustomerEntity): Event? {
        return when {
            new.firstName != old.firstName -> FirstNameChanged(old.firstName, new.firstName)
            new.lastName != old.lastName -> LastNameChanged(old.lastName, new.lastName)
            new.email != old.email -> EmailAddressChanged(old.email, new.email)
            else -> null
        }
    }
}
