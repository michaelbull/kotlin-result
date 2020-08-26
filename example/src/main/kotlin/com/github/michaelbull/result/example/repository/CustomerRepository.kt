package com.github.michaelbull.result.example.repository

import com.github.michaelbull.result.example.model.entity.CustomerEntity
import com.github.michaelbull.result.example.model.entity.CustomerId

/**
 * A repository that stores a [CustomerEntity] identified by a [CustomerId].
 */
interface CustomerRepository : Repository<CustomerEntity, CustomerId>
