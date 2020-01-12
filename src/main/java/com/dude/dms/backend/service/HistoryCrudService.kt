package com.dude.dms.backend.service

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.Historical
import com.dude.dms.backend.data.history.History
import org.springframework.data.jpa.repository.JpaRepository

abstract class HistoryCrudService<T, U : History>(jpaRepository: JpaRepository<U, Long>) : CrudService<U>(jpaRepository) where T : DataEntity, T : Historical<U> {
    abstract fun getHistory(entity: T): List<U>
}