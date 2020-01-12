package com.dude.dms.backend.data

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.validation.constraints.Size

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Changelog(
        var published: LocalDateTime,
        var body: @Size(max = 99999999) String,
        var version: String
) : DataEntity()