package com.dude.dms.backend.repositories

import com.dude.dms.brain.DmsLogger
import com.dude.dms.backend.data.LogEntry
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface LogEntryRepository : JpaRepository<LogEntry, Long> {

    @Query("SELECT log FROM LogEntry log " +
            "WHERE (:className is null or log.className = :className) " +
            "AND (:date is null or CAST(log.timestamp AS date) = CAST(:date AS date))" +
            "AND (:level is null or log.level = :level)" +
            "AND (:message is null or LOWER(log.message) LIKE %:message%)" +
            "AND (:ui = false or log.isUi = :ui)")
    fun findByFilter(@Param("date") date: LocalDate?, @Param("className") className: String?, @Param("level") level: DmsLogger.Level?,
                     @Param("message") message: String?, @Param("ui") ui: Boolean?, pageable: Pageable): Page<LogEntry>

    @Query("SELECT COUNT(*) FROM LogEntry log " +
            "WHERE (:className is null or log.className = :className) " +
            "AND (:date is null or CAST(log.timestamp AS date) = CAST(:date AS date))" +
            "AND (:level is null or log.level = :level)" +
            "AND (:message is null or LOWER(log.message) LIKE %:message%)" +
            "AND (:ui = false or log.isUi = :ui)")
    fun countByFilter(@Param("date") date: LocalDate?, @Param("className") className: String?, @Param("level") level: DmsLogger.Level?,
                      @Param("message") message: String?, @Param("ui") ui: Boolean?): Long

    @Query("SELECT DISTINCT log.className FROM LogEntry log")
    fun findDistinctClassNames(): Set<String>

    fun findTopByOrderByTimestampAsc(): LogEntry

    fun findTopByOrderByTimestampDesc(): LogEntry
}
