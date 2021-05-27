package com.dude.dms.backend.data.filter

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.LogsEvents
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class Filter : DataEntity(), LogsEvents
