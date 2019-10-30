package com.dude.dms.backend.repositories;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    List<LogEntry> findByClassName(String className);

    List<LogEntry> findByLevel(DmsLogger.Level level);
}