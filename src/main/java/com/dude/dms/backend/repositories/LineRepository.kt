package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import org.springframework.data.jpa.repository.JpaRepository

interface LineRepository : JpaRepository<Line, Long> {

    fun findByDoc(doc: Doc): Set<Line>

    fun countByDoc(doc: Doc): Long
}