package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.RestorableEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface RestoreRepository<T : RestorableEntity> : JpaRepository<T, Long> {

    fun findByDeletedFalse(): List<T>

    fun findByDeletedFalse(pageable: Pageable): Page<T>

    fun countByDeletedFalse(): Long

    fun findByDeletedTrue(): List<T>

    fun countByDeletedTrue(): Long
}
