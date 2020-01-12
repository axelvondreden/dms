package com.dude.dms.ui.dataproviders

import com.dude.dms.backend.brain.DmsLogger
import com.dude.dms.backend.data.LogEntry
import com.dude.dms.backend.service.LogEntryService
import com.dude.dms.ui.dataproviders.LogDataProvider.LogFilter
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.provider.QuerySortOrder
import com.vaadin.flow.data.provider.QuerySortOrderBuilder
import com.vaadin.flow.spring.annotation.SpringComponent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider
import java.io.Serializable
import java.time.LocalDate
import java.util.function.Consumer

@SpringComponent
class LogDataProvider(private val logEntryService: LogEntryService) : FilterablePageableDataProvider<LogEntry, LogFilter>() {

    data class LogFilter(var date: LocalDate? = null, var className: String? = null, var level: DmsLogger.Level? = null, var isUI: Boolean = false) : Serializable

    private var defaultSortOrders: List<QuerySortOrder>? = null
    private var pageObserver: Consumer<in Page<LogEntry>>? = null

    init {
        setSortOrders(Sort.Direction.DESC, "timestamp")
    }

    private fun setSortOrders(direction: Sort.Direction, vararg properties: String) {
        val builder = QuerySortOrderBuilder()
        for (property in properties) {
            if (direction.isAscending) {
                builder.thenAsc(property)
            } else {
                builder.thenDesc(property)
            }
        }
        defaultSortOrders = builder.build()
    }

    override fun fetchFromBackEnd(query: Query<LogEntry, LogFilter>, pageable: Pageable) = when {
        query.filter.isPresent -> {
            val page = logEntryService.findByLogFilter(query.filter.get(), pageable)
            pageObserver?.accept(page)
            page
        }
        else -> Page.empty()
    }

    override fun getDefaultSortOrders() = defaultSortOrders!!

    override fun sizeInBackEnd(query: Query<LogEntry, LogFilter>) = when {
        query.filter.isPresent -> logEntryService.countByLogFilter(query.filter.get())
        else -> 0
    }.toInt()

    override fun getId(item: LogEntry) = item.hashCode()
}