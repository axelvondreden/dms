package com.dude.dms.backend.data

import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class RestorableEntity : DataEntity() {
    var deleted: Boolean? = false
}
