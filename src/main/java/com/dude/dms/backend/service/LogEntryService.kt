package com.dude.dms.backend.service

import com.dude.dms.backend.data.LogEntry
import com.dude.dms.backend.repositories.LogEntryRepository
import com.dude.dms.ui.dataproviders.LogDataProvider.Filter
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class LogEntryService(private val logEntryRepository: LogEntryRepository) : CrudService<LogEntry>(logEntryRepository) {

    fun findByFilter(filter: Filter, pageable: Pageable) = logEntryRepository.findByFilter(filter.date, filter.className, filter.level, filter.message, filter.isUI, pageable)

    fun countByFilter(filter: Filter) = logEntryRepository.countByFilter(filter.date, filter.className, filter.level, filter.message, filter.isUI)

    fun findDistinctClassNames() = logEntryRepository.findDistinctClassNames()

    fun findFirst() = logEntryRepository.findTopByOrderByTimestampAsc()

    fun findLast() = logEntryRepository.findTopByOrderByTimestampDesc()
}