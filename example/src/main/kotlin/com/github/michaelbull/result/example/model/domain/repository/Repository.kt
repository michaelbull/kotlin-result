package com.github.michaelbull.result.example.model.domain.repository

/**
 * A class that encapsulates storage and retrieval of domain objects of type [T], identified by a key of type [ID].
 */
interface Repository<T, ID> {
    fun findAll(): Collection<T>
    fun update(entity: T)
    fun insert(entity: T)
}
