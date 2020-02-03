package com.dude.dms.ui.dataproviders

import com.dude.dms.backend.data.LogEntry
import com.dude.dms.backend.service.LogEntryService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.ui.dataproviders.LogDataProvider.Filter
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.spring.annotation.SpringComponent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.io.Serializable
import java.time.LocalDate

@SpringComponent
class LogDataProvider(private val logEntryService: LogEntryService) : GridViewDataProvider<LogEntry, Filter>() {

    data class Filter(
            var date: LocalDate? = null,
            var className: String? = null,
            var level: DmsLogger.Level? = null,
            var isUI: Boolean = false
    ) : Serializable

    init {
        setSortOrders(Sort.Direction.DESC, "timestamp")
    }

    override fun fetchFromBackEnd(query: Query<LogEntry, Filter>, pageable: Pageable) = when {
        query.filter.isPresent -> {
            val page = logEntryService.findByFilter(query.filter.get(), pageable)
            pageObserver?.accept(page)
            page
        }
        else -> Page.empty()
    }

    override fun sizeInBackEnd(query: Query<LogEntry, Filter>) = when {
        query.filter.isPresent -> logEntryService.countByFilter(query.filter.get())
        else -> 0
    }.toInt()
}