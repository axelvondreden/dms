package com.dude.dms.backend.service

import com.dude.dms.backend.data.DataEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

abstract class CrudService<T : DataEntity>(private val repository: JpaRepository<T, Long>) {

    open fun create(entity: T): T = repository.saveAndFlush(entity)

    open fun save(entity: T): T = repository.saveAndFlush(entity)

    open fun delete(entity: T) = repository.delete(entity)

    open fun count() = repository.count()

    fun load(id: Long): T? = repository.findById(id).orElse(null)

    open fun findAll(): List<T> = repository.findAll()

    open fun findAll(pageable: Pageable): Page<T> = repository.findAll(pageable)
}
