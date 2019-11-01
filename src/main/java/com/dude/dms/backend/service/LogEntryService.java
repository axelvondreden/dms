package com.dude.dms.backend.service;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.LogEntry;
import com.dude.dms.backend.repositories.LogEntryRepository;
import com.dude.dms.ui.dataproviders.LogDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogEntryService extends CrudService<LogEntry> {

    private final LogEntryRepository logEntryRepository;

    @Autowired
    public LogEntryService(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    @Override
    public JpaRepository<LogEntry, Long> getRepository() {
        return logEntryRepository;
    }

    public List<LogEntry> findByClassName(String className) {
        return logEntryRepository.findByClassName(className);
    }

    public List<LogEntry> findByLevel(DmsLogger.Level level) {
        return logEntryRepository.findByLevel(level);
    }

    public List<LogEntry> findByOrderByTimestampDesc() {
        return logEntryRepository.findByOrderByTimestampDesc();
    }

    public Page<LogEntry> findByLogFilter(LogDataProvider.LogFilter logFilter, Pageable pageable) {
        return logEntryRepository.findByLogFilter(logFilter.getDate(), logFilter.getClassName(), logFilter.getLevel(), pageable);
    }

    public long countByLogFilter(LogDataProvider.LogFilter logFilter) {
        return logEntryRepository.countByLogFilter(logFilter.getDate(), logFilter.getClassName(), logFilter.getLevel());
    }

    public List<String> findDistinctClassNames() {
        return logEntryRepository.findDistinctClassNames();
    }
}