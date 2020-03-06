package com.dude.dms.backend.data

import com.dude.dms.brain.DmsLogger
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.validation.constraints.Size

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class LogEntry(
        var timestamp: LocalDateTime,
        var className: String,
        var path: String,
        var message: String,
        var level: DmsLogger.Level,
        @Column(columnDefinition = "LONGVARCHAR")
        @Size(max = 10000)
        var stacktrace: String? = null,
        var isUi: Boolean = false
) : DataEntity() {
    override fun toString(): String = "LogEntry($timestamp, $message, $level)"
}