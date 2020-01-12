package com.dude.dms.backend.service

import com.dude.dms.backend.data.DataEntity
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.EntityNotFoundException

abstract class CrudService<T : DataEntity>(protected val repository: JpaRepository<T, Long>) {

    open fun save(entity: T) = repository.saveAndFlush(entity)

    open fun delete(entity: T) = repository.delete(entity)

    fun count() = repository.count()

    fun load(id: Long) = repository.findById(id).orElse(null) ?: throw EntityNotFoundException("entity was null")

    open fun create(entity: T) = repository.saveAndFlush(entity)

    fun findAll() = repository.findAll()
}