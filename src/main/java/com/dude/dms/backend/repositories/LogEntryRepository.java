package com.dude.dms.backend.repositories;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.LogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    List<LogEntry> findByClassName(String className);

    List<LogEntry> findByLevel(DmsLogger.Level level);

    List<LogEntry> findByOrderByTimestampDesc();

    @Query("SELECT log FROM LogEntry log " +
            "WHERE (:className is null or log.className = :className) " +
            "AND (:date is null or CAST(log.timestamp AS date) = :date)" +
            "AND (:level is null or log.level = :level)")
    Page<LogEntry> findByLogFilter(@Param("date") LocalDate date, @Param("className") String className, @Param("level") DmsLogger.Level level, Pageable pageable);

    @Query("SELECT COUNT(*) FROM LogEntry log " +
            "WHERE (:className is null or log.className = :className) " +
            "AND (:date is null or CAST(log.timestamp AS date) = :date)" +
            "AND (:level is null or log.level = :level)")
    long countByLogFilter(@Param("date") LocalDate date, @Param("className") String className, @Param("level") DmsLogger.Level level);

    @Query("SELECT DISTINCT log.className FROM LogEntry log")
    List<String> findDistinctClassNames();
}