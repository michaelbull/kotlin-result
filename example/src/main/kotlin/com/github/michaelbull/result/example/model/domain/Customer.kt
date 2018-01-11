package com.github.michaelbull.result.example.model.domain

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.example.model.dto.CustomerDto
import com.github.michaelbull.result.example.model.entity.CustomerEntity
import com.github.michaelbull.result.zip

data class Customer(
    val id: CustomerId,
    val name: PersonalName,
    val email: EmailAddress
) {
    companion object {
        fun from(entity: CustomerEntity): Result<Customer, DomainMessage> {
            val createId = { CustomerId.create(entity.id) }
            val createName = { PersonalName.create(entity.firstName, entity.lastName) }
            val createEmail = { EmailAddress.create(entity.email) }
            return zip(createId, createName, createEmail, ::Customer)
        }

        fun from(dto: CustomerDto): Result<Customer, DomainMessage> {
            val createId = { CustomerId.create(dto.id) }
            val createName = { PersonalName.create(dto.firstName, dto.lastName) }
            val createEmail = { EmailAddress.create(dto.email) }
            return zip(createId, createName, createEmail, ::Customer)
        }
    }
}
