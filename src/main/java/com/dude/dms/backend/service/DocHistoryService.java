package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.DocHistory;
import com.dude.dms.backend.repositories.DocHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocHistoryService implements CrudService<DocHistory> {

    private final DocHistoryRepository docHistoryRepository;

    @Autowired
    public DocHistoryService(DocHistoryRepository docHistoryRepository) {
        this.docHistoryRepository = docHistoryRepository;
    }

    @Override
    public DocHistoryRepository getRepository() {
        return docHistoryRepository;
    }
}
