package com.dude.dms.ui.views;

import com.dude.dms.backend.data.LogEntry;
import com.dude.dms.backend.service.LogEntryService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.converters.LocalDateTimeConverter;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = Const.PAGE_LOG, layout = MainView.class)
@RouteAlias(value = Const.PAGE_LOG, layout = MainView.class)
@PageTitle("Log")
public class LogView extends VerticalLayout {

    @Autowired
    public LogView(LogEntryService logEntryService) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        Grid<LogEntry> grid = new Grid<>();
        grid.addColumn(log -> LocalDateTimeConverter.convert(log.getTimestamp())).setHeader("Timestamp").setAutoWidth(true).setResizable(true);
        grid.addColumn(LogEntry::getLevel).setHeader("Level").setAutoWidth(true).setResizable(true);
        grid.addColumn(LogEntry::getClassName).setHeader("Class").setAutoWidth(true).setResizable(true);
        grid.addColumn(LogEntry::getMessage).setHeader("Message").setAutoWidth(true).setResizable(true);
        grid.setItems(logEntryService.findByOrderByTimestampDesc());
        grid.setSizeFull();
        add(grid);
    }
}