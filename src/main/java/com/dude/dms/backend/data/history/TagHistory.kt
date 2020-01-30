package com.dude.dms.backend.data.history

import com.dude.dms.backend.data.Tag
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class TagHistory(
        @ManyToOne var tag: Tag,
        text: String?,
        created: Boolean,
        edited: Boolean
) : History(created, edited, text)