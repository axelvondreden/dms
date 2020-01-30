package com.dude.dms.backend.data.history

import com.dude.dms.backend.data.docs.Doc
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class DocHistory(
        @ManyToOne var doc: Doc,
        text: String?,
        created: Boolean,
        edited: Boolean
) : History(created, edited, text)