package com.dude.dms.backend.service

import com.dude.dms.backend.data.DataEntity
import org.springframework.data.jpa.repository.JpaRepository

abstract class CrudService<T : DataEntity>(protected val repository: JpaRepository<T, Long>) {

    open fun create(entity: T) = repository.saveAndFlush(entity)

    open fun save(entity: T) = repository.saveAndFlush(entity)

    open fun delete(entity: T) = repository.delete(entity)

    fun count() = repository.count()

    fun load(id: Long): T? = repository.findById(id).orElse(null)

    fun findAll() = repository.findAll()
}