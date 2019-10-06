package com.dude.dms.backend.service;

import com.dude.dms.backend.data.base.Doc;
import com.dude.dms.backend.data.history.DocHistory;
import com.dude.dms.backend.repositories.DocHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocHistoryService extends HistoryCrudService<Doc, DocHistory> {

    private final DocHistoryRepository docHistoryRepository;

    @Autowired
    public DocHistoryService(DocHistoryRepository docHistoryRepository) {
        this.docHistoryRepository = docHistoryRepository;
    }

    @Override
    public DocHistoryRepository getRepository() {
        return docHistoryRepository;
    }

    @Override
    public List<DocHistory> getHistory(Doc entity) {
        return docHistoryRepository.findByDoc(entity);
    }
}
