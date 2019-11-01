package com.dude.dms.backend.service;

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

    public Page<LogEntry> findByLogFilter(LogDataProvider.LogFilter filter, Pageable pageable) {
        return logEntryRepository.findByLogFilter(filter.getDate(), filter.getClassName(), filter.getLevel(), filter.isUI(), pageable);
    }

    public long countByLogFilter(LogDataProvider.LogFilter filter) {
        return logEntryRepository.countByLogFilter(filter.getDate(), filter.getClassName(), filter.getLevel(), filter.isUI());
    }

    public List<String> findDistinctClassNames() {
        return logEntryRepository.findDistinctClassNames();
    }
}