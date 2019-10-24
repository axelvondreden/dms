package com.dude.dms.backend.service;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.data.history.DocHistory;
import com.dude.dms.backend.repositories.DocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Doc> findByGuid(String guid) {
        return docRepository.findByGuid(guid);
    }

    public List<Doc> findByTag(Tag tag) {
        return docRepository.findByTags(tag);
    }

    public long countByTag(Tag tag) {
        return docRepository.countByTags(tag);
    }

    public List<Doc> findTop10ByRawTextContaining(String rawText) {
        return docRepository.findTop10ByRawTextContaining(rawText);
    }

    public long countByRawTextContaining(String rawText) {
        return docRepository.countByRawTextContaining(rawText);
    }

    public List<Doc> findTop10ByRawTextContainingIgnoreCase(String rawText) {
        return docRepository.findTop10ByRawTextContainingIgnoreCase(rawText);
    }

    public long countByRawTextContainingIgnoreCase(String rawText) {
        return docRepository.countByRawTextContainingIgnoreCase(rawText);
    }
}