package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.data.entity.DocHistory;
import com.dude.dms.backend.repositories.DocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class DocService extends HistoricalCrudService<Doc, DocHistory> {

    private final DocRepository docRepository;

    @Autowired
    public DocService(DocRepository docRepository) {
        this.docRepository = docRepository;
    }

    @Override
    public JpaRepository<Doc, Long> getRepository() {
        return docRepository;
    }

    @Override
    public DocHistory createHistory(Doc entity, String text, boolean created, boolean edited, boolean deleted) {
        return new DocHistory(entity, text, created, edited, deleted);
    }

}
