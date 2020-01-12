package com.dude.dms.backend.data.history

import com.dude.dms.backend.data.DataEntity
import java.time.LocalDateTime
import javax.persistence.MappedSuperclass
import javax.validation.constraints.Size

@MappedSuperclass
abstract class History(
        var created: Boolean,
        var edited: Boolean,
        var deleted: Boolean,
        var text: @Size(max = 255) String? = null,
        var timestamp: LocalDateTime = LocalDateTime.now()
) : DataEntity()