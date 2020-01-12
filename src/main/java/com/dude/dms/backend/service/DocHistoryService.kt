package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.history.DocHistory
import com.dude.dms.backend.repositories.DocHistoryRepository
import org.springframework.stereotype.Service

@Service
class DocHistoryService(private val docHistoryRepository: DocHistoryRepository) : HistoryCrudService<Doc, DocHistory>(docHistoryRepository) {

    override fun getHistory(entity: Doc) = docHistoryRepository.findByDoc(entity)
}