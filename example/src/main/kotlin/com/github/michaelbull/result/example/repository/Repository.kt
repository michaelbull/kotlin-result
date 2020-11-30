package com.github.michaelbull.result.example.repository

/**
 * A class that encapsulates storage and retrieval of domain objects of type [T], identified by a key of type [ID].
 */
interface Repository<T, ID> {
    fun findById(id: ID): T?
    fun save(entity: T)
}
