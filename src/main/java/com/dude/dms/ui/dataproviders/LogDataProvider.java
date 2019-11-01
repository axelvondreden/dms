package com.dude.dms.ui.dataproviders;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.LogEntry;
import com.dude.dms.backend.service.LogEntryService;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.QuerySortOrderBuilder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

@SpringComponent
//@UIScope
public class LogDataProvider extends FilterablePageableDataProvider<LogEntry, LogDataProvider.LogFilter> {

    public static class LogFilter implements Serializable {

        private LocalDate date;
        private String className;
        private DmsLogger.Level level;
        private boolean ui;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public DmsLogger.Level getLevel() {
            return level;
        }

        public void setLevel(DmsLogger.Level level) {
            this.level = level;
        }

        public boolean isUI() {
            return ui;
        }

        public void setUi(boolean ui) {
            this.ui = ui;
        }
    }

    private final LogEntryService logEntryService;
    private List<QuerySortOrder> defaultSortOrders;
    private Consumer<? super Page<LogEntry>> pageObserver;

    @Autowired
    public LogDataProvider(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
        setSortOrders(Sort.Direction.DESC, "timestamp");
    }

    private void setSortOrders(Sort.Direction direction, String... properties) {
        QuerySortOrderBuilder builder = new QuerySortOrderBuilder();
        for (String property : properties) {
            if (direction.isAscending()) {
                builder.thenAsc(property);
            } else {
                builder.thenDesc(property);
            }
        }
        defaultSortOrders = builder.build();
    }

    @Override
    protected Page<LogEntry> fetchFromBackEnd(Query<LogEntry, LogFilter> query, Pageable pageable) {
        if (query.getFilter().isPresent()) {
            Page<LogEntry> page = logEntryService.findByLogFilter(query.getFilter().get(), pageable);
            if (pageObserver != null) {
                pageObserver.accept(page);
            }
            return page;
        }
        return null;
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return defaultSortOrders;
    }

    @Override
    protected int sizeInBackEnd(Query<LogEntry, LogFilter> query) {
        return (int) (query.getFilter().isPresent() ? logEntryService.countByLogFilter(query.getFilter().get()) : 0);
    }

    public void setPageObserver(Consumer<? super Page<LogEntry>> pageObserver) {
        this.pageObserver = pageObserver;
    }

    @Override
    public Object getId(LogEntry item) {
        return item.hashCode();
    }
}
