package com.dude.dms.ui.views

import com.dude.dms.backend.data.LogEntry
import com.dude.dms.backend.service.LogEntryService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.dude.dms.ui.components.standard.DmsDatePicker
import com.dude.dms.ui.dataproviders.LogDataProvider
import com.dude.dms.ui.dataproviders.LogDataProvider.Filter
import com.dude.dms.brain.extensions.convert
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias
import org.vaadin.olli.FileDownloadWrapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Route(value = Const.PAGE_LOG, layout = MainView::class)
@RouteAlias(value = Const.PAGE_LOG, layout = MainView::class)
@PageTitle("Log")
class LogView(private val logDataProvider: LogDataProvider, logEntryService: LogEntryService) : VerticalLayout() {

    private val dateFilter = DmsDatePicker().apply {
        placeholder = t("date")
        value = LocalDate.now()
        locale = Locale.forLanguageTag(Options.get().view.locale)
        addValueChangeListener { refreshFilter() }
        min = logEntryService.findFirst().timestamp.toLocalDate()
        max = logEntryService.findLast().timestamp.toLocalDate()
        setWidthFull()
    }

    private val classNameFilter = ComboBox<String>().apply {
        placeholder = "Classes"
        isPreventInvalidInput = true
        isClearButtonVisible = true
        isAllowCustomValue = false
        setItems(logEntryService.findDistinctClassNames())
        addValueChangeListener { refreshFilter() }
        setWidthFull()
    }

    private val levelFilter = ComboBox<DmsLogger.Level>().apply {
        placeholder = "Levels"
        isPreventInvalidInput = true
        isClearButtonVisible = true
        isAllowCustomValue = false
        setItems(*DmsLogger.Level.values())
        setItems(*DmsLogger.Level.values())
        addValueChangeListener { refreshFilter() }
        setWidthFull()
    }

    private val messageFilter = TextField { refreshFilter() }.apply {
        placeholder = "Messages"
        setWidthFull()
        valueChangeMode = ValueChangeMode.LAZY
        valueChangeTimeout = 500
    }

    private val uiFilter = Checkbox().apply { addValueChangeListener { refreshFilter() } }

    init {
        setSizeFull()
        isPadding = false
        isSpacing = false

        val grid = Grid<LogEntry>()
        val export = FileDownloadWrapper("dms.log") { exportLog(grid.selectedItems) }
        val exportButton = Button("Export", VaadinIcon.FILE_ZIP.create()).apply {
            isEnabled = false
        }
        export.wrapComponent(exportButton)

        grid.apply {
            pageSize = 200
            dataProvider = logDataProvider
            addThemeVariants(GridVariant.LUMO_COMPACT)
            addColumn { it.timestamp.convert() }.setHeader("Timestamp").setAutoWidth(true).setResizable(true).key = "timestamp"
            addColumn { it.level }.setHeader("Level").setAutoWidth(true).setResizable(true).key = "level"
            addColumn { it.className }.setHeader("Class").setAutoWidth(true).setResizable(true).key = "class"
            addColumn { it.message }.setHeader("Message").setAutoWidth(true).setResizable(true).key = "message"
            addComponentColumn { Checkbox(it.isUi).apply { isReadOnly = true } }.setHeader("UI").setAutoWidth(true).setResizable(true).key = "ui"
            addComponentColumn { entry ->
                Button(VaadinIcon.FILE_TEXT.create()) { showStackTrace(entry) }.apply { isEnabled = entry.stacktrace != null }
            }.setHeader("Stack").setAutoWidth(true).setResizable(true).key = "stacktrace"
            setSizeFull()
            setSelectionMode(Grid.SelectionMode.MULTI)
            asMultiSelect().addSelectionListener { exportButton.isEnabled = it.allSelectedItems.isNotEmpty() }
        }

        grid.appendHeaderRow().apply {
            getCell(grid.getColumnByKey("timestamp")).setComponent(dateFilter)
            getCell(grid.getColumnByKey("class")).setComponent(classNameFilter)
            getCell(grid.getColumnByKey("level")).setComponent(levelFilter)
            getCell(grid.getColumnByKey("message")).setComponent(messageFilter)
            getCell(grid.getColumnByKey("ui")).setComponent(uiFilter)
        }

        add(export, grid)
        refreshFilter()
    }

    private fun exportLog(entries: Set<LogEntry>): ByteArray {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
        return entries.sortedBy { it.timestamp }.joinToString("\n") {
            "${it.timestamp.format(formatter)} ${it.level} ${it.className} ${it.message}${if (it.stacktrace != null) "\n${it.stacktrace}" else ""}"
        }.toByteArray()
    }

    private fun showStackTrace(entry: LogEntry) {
        Dialog(TextArea("", entry.stacktrace, "").apply { isReadOnly = true; setWidthFull() }).apply {
            width = "80vw"
        }.open()
    }

    private fun refreshFilter() {
        val filter = Filter(dateFilter.optionalValue.orElse(null), classNameFilter.optionalValue.orElse(null), levelFilter.optionalValue.orElse(null), messageFilter.optionalValue.orElse(null), uiFilter.value)
        logDataProvider.setFilter(filter)
        logDataProvider.refreshAll()
    }
}