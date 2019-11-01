package com.dude.dms.ui.views;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.LogEntry;
import com.dude.dms.backend.service.LogEntryService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.components.standard.DmsDatePicker;
import com.dude.dms.ui.converters.LocalDateTimeConverter;
import com.dude.dms.ui.dataproviders.LogDataProvider;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Route(value = Const.PAGE_LOG, layout = MainView.class)
@RouteAlias(value = Const.PAGE_LOG, layout = MainView.class)
@PageTitle("Log")
public class LogView extends VerticalLayout {

    private final DmsDatePicker dateFilter;

    private final ComboBox<String> classNameFilter;

    private final ComboBox<DmsLogger.Level> levelFilter;

    private final LogDataProvider logDataProvider;

    @Autowired
    public LogView(LogDataProvider logDataProvider, LogEntryService logEntryService) {
        this.logDataProvider = logDataProvider;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        Grid<LogEntry> grid = new Grid<>();
        grid.setPageSize(200);
        grid.setDataProvider(logDataProvider);
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        grid.addColumn(log -> LocalDateTimeConverter.convert(log.getTimestamp())).setHeader("Timestamp").setAutoWidth(true).setResizable(true).setKey("timestamp");
        grid.addColumn(LogEntry::getLevel).setHeader("Level").setAutoWidth(true).setResizable(true).setKey("level");
        grid.addColumn(LogEntry::getClassName).setHeader("Class").setAutoWidth(true).setResizable(true).setKey("class");
        grid.addColumn(LogEntry::getMessage).setHeader("Message").setAutoWidth(true).setResizable(true).setKey("message");
        grid.setItems(logEntryService.findByOrderByTimestampDesc());
        grid.setSizeFull();

        dateFilter = new DmsDatePicker();
        dateFilter.setPlaceholder("Date");
        dateFilter.setValue(LocalDate.now());
        dateFilter.addValueChangeListener(e -> refreshFilter());

        classNameFilter = new ComboBox<>();
        classNameFilter.setPlaceholder("All Classes");
        classNameFilter.setPreventInvalidInput(true);
        classNameFilter.setAllowCustomValue(false);
        classNameFilter.setItems(logEntryService.findDistinctClassNames());
        classNameFilter.addValueChangeListener(e -> refreshFilter());

        levelFilter = new ComboBox<>();
        levelFilter.setPlaceholder("All Levels");
        levelFilter.setPreventInvalidInput(true);
        levelFilter.setAllowCustomValue(false);
        levelFilter.setItems(DmsLogger.Level.values());
        levelFilter.addValueChangeListener(e -> refreshFilter());

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(grid.getColumnByKey("timestamp")).setComponent(dateFilter);
        headerRow.getCell(grid.getColumnByKey("class")).setComponent(classNameFilter);
        headerRow.getCell(grid.getColumnByKey("level")).setComponent(levelFilter);

        add(grid);
    }

    private void refreshFilter() {
        LogDataProvider.LogFilter filter = new LogDataProvider.LogFilter();
        filter.setClassName(classNameFilter.getOptionalValue().orElse(null));
        filter.setDate(dateFilter.getOptionalValue().orElse(null));
        filter.setLevel(levelFilter.getOptionalValue().orElse(null));
        logDataProvider.setFilter(filter);
    }
}