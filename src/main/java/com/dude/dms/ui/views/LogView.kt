package com.dude.dms.ui.views

import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.OptionKey
import com.dude.dms.backend.data.LogEntry
import com.dude.dms.backend.service.LogEntryService
import com.dude.dms.ui.Const
import com.dude.dms.ui.MainView
import com.dude.dms.ui.components.standard.DmsDatePicker
import com.dude.dms.ui.extensions.convert
import com.dude.dms.ui.dataproviders.LogDataProvider
import com.dude.dms.ui.dataproviders.LogDataProvider.LogFilter
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias
import java.time.LocalDate
import java.util.*

@Route(value = Const.PAGE_LOG, layout = MainView::class)
@RouteAlias(value = Const.PAGE_LOG, layout = MainView::class)
@PageTitle("Log")
class LogView(private val logDataProvider: LogDataProvider, logEntryService: LogEntryService) : VerticalLayout() {

    private val dateFilter = DmsDatePicker().apply {
        placeholder = "Date"
        value = LocalDate.now()
        locale = Locale.forLanguageTag(OptionKey.LOCALE.string)
        addValueChangeListener { refreshFilter() }
    }

    private val classNameFilter = ComboBox<String>().apply {
        placeholder = "All Classes"
        isPreventInvalidInput = true
        isClearButtonVisible = true
        isAllowCustomValue = false
        setItems(logEntryService.findDistinctClassNames())
        addValueChangeListener { refreshFilter() }
    }

    private val levelFilter = ComboBox<DmsLogger.Level>().apply {
        placeholder = "All Levels"
        isPreventInvalidInput = true
        isClearButtonVisible = true
        isAllowCustomValue = false
        setItems(*DmsLogger.Level.values())
        addValueChangeListener { refreshFilter() }
    }

    private val uiFilter = Checkbox().apply { addValueChangeListener { refreshFilter() } }

    init {
        setSizeFull()
        isPadding = false
        isSpacing = false

        val grid = Grid<LogEntry>().apply {
            pageSize = 200
            dataProvider = logDataProvider
            addThemeVariants(GridVariant.LUMO_COMPACT)
            addColumn { it.timestamp.convert() }.setHeader("Timestamp").setAutoWidth(true).setResizable(true).key = "timestamp"
            addColumn { it.level }.setHeader("Level").setAutoWidth(true).setResizable(true).key = "level"
            addColumn { it.className }.setHeader("Class").setAutoWidth(true).setResizable(true).key = "class"
            addColumn { it.message }.setHeader("Message").setAutoWidth(true).setResizable(true).key = "message"
            addComponentColumn { Checkbox(it.isUi).apply { isReadOnly = true } }.setHeader("UI").setAutoWidth(true).setResizable(true).key = "ui"
            setSizeFull()
        }

        grid.appendHeaderRow().apply {
            getCell(grid.getColumnByKey("timestamp")).setComponent(dateFilter)
            getCell(grid.getColumnByKey("class")).setComponent(classNameFilter)
            getCell(grid.getColumnByKey("level")).setComponent(levelFilter)
            getCell(grid.getColumnByKey("ui")).setComponent(uiFilter)
        }
        add(grid)
        refreshFilter()
    }

    private fun refreshFilter() {
        val filter = LogFilter(dateFilter.optionalValue.orElse(null), classNameFilter.optionalValue.orElse(null), levelFilter.optionalValue.orElse(null), uiFilter.value)
        logDataProvider.setFilter(filter)
        logDataProvider.refreshAll()
    }
}