package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Page

interface PageRepository : RestoreRepository<Page> {
    fun findByDocAndDeletedFalse(doc: Doc): Set<Page>
}