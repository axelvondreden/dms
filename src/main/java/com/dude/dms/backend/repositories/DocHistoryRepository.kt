package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.history.DocHistory
import org.springframework.data.jpa.repository.JpaRepository

interface DocHistoryRepository : JpaRepository<DocHistory, Long> {
    fun findByDoc(doc: Doc): List<DocHistory>
}