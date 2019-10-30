package com.dude.dms.backend.data;

import com.dude.dms.backend.brain.DmsLogger;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class LogEntry extends DataEntity {

    @NotNull
    protected LocalDateTime timestamp;

    @NotBlank
    protected String className;

    @NotBlank
    protected String path;

    protected String message;

    protected String stacktrace;

    @NotNull
    protected DmsLogger.Level level;

    public LogEntry() {

    }

    public LogEntry(@NotNull LocalDateTime timestamp, @NotBlank String className, @NotBlank String path, @NotNull DmsLogger.Level level) {
        this.timestamp = timestamp;
        this.className = className;
        this.path = path;
        this.level = level;
    }

    public LogEntry(@NotNull LocalDateTime timestamp, @NotBlank String className, @NotBlank String path, String message, @NotNull DmsLogger.Level level) {
        this.timestamp = timestamp;
        this.className = className;
        this.path = path;
        this.message = message;
        this.level = level;
    }

    public LogEntry(@NotNull LocalDateTime timestamp, @NotBlank String className, @NotBlank String path, String message, String stacktrace, @NotNull DmsLogger.Level level) {
        this.timestamp = timestamp;
        this.className = className;
        this.path = path;
        this.message = message;
        this.stacktrace = stacktrace;
        this.level = level;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public DmsLogger.Level getLevel() {
        return level;
    }

    public void setLevel(DmsLogger.Level level) {
        this.level = level;
    }
}