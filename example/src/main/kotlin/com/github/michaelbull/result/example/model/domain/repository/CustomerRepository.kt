package com.github.michaelbull.result.example.model.domain.repository

import com.github.michaelbull.result.example.model.domain.Customer
import com.github.michaelbull.result.example.model.entity.CustomerEntity

/**
 * A repository that stores [Customers][Customer] with a [Long] ID.
 */
interface CustomerRepository : Repository<CustomerEntity, Long>
