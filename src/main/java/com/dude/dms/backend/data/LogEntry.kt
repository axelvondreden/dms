package com.dude.dms.backend.data

import com.dude.dms.brain.DmsLogger
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.time.LocalDateTime
import javax.persistence.Entity

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class LogEntry(
        var timestamp: LocalDateTime,
        var className: String,
        var path: String,
        var message: String,
        var level: DmsLogger.Level,
        var stacktrace: String? = null,
        var isUi: Boolean = false
) : DataEntity() {
    override fun toString(): String = "LogEntry($timestamp, $message, $level)"
}