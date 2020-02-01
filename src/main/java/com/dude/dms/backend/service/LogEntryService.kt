package com.dude.dms.backend.service

import com.dude.dms.backend.data.LogEntry
import com.dude.dms.backend.repositories.LogEntryRepository
import com.dude.dms.ui.dataproviders.LogDataProvider.LogFilter
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class LogEntryService(private val logEntryRepository: LogEntryRepository) : CrudService<LogEntry>(logEntryRepository) {

    fun findByLogFilter(filter: LogFilter, pageable: Pageable) = logEntryRepository.findByLogFilter(filter.date, filter.className, filter.level, filter.isUI, pageable)

    fun countByLogFilter(filter: LogFilter) = logEntryRepository.countByLogFilter(filter.date, filter.className, filter.level, filter.isUI)

    fun findDistinctClassNames() = logEntryRepository.findDistinctClassNames()

    fun findTopOrderByIdAsc() = logEntryRepository.findTopOrderByIdAsc()

    fun findTopOrderByIdDesc() = logEntryRepository.findTopOrderByIdDesc()
}