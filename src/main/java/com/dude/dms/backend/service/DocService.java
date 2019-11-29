package com.dude.dms.backend.service;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.data.docs.AttributeValue;
import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.history.DocHistory;
import com.dude.dms.backend.repositories.DocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocService extends HistoricalCrudService<Doc, DocHistory> {

    private final DocRepository docRepository;

    private final AttributeValueService attributeValueService;

    @Autowired
    public DocService(DocRepository docRepository, AttributeValueService attributeValueService) {
        this.docRepository = docRepository;
        this.attributeValueService = attributeValueService;
    }

    @Override
    public JpaRepository<Doc, Long> getRepository() {
        return docRepository;
    }

    @Override
    public Doc create(Doc entity) {
        createAttributeValues(entity);
        return super.create(entity);
    }

    @Override
    public Doc save(Doc entity) {
        createAttributeValues(entity);
        super.save(entity);
        deleteAttributeValues(entity);
        return entity;
    }

    private void createAttributeValues(Doc doc) {
        doc.getTags().stream()
                .flatMap(tag -> tag.getAttributes().stream())
                .filter(attribute -> !attributeValueService.findByDocAndAttribute(doc, attribute).isPresent())
                .map(attribute -> new AttributeValue(doc, attribute))
                .distinct()
                .forEach(attributeValueService::create);
    }

    private void deleteAttributeValues(Doc doc) {
        List<Attribute> attributes = doc.getTags().stream().flatMap(tag -> tag.getAttributes().stream()).collect(Collectors.toList());
        doc.getAttributeValues().stream()
                .filter(attributeValue -> !attributes.contains(attributeValue.getAttribute()))
                .distinct()
                .forEach(attributeValueService::delete);
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

    public long countByAttribute(Attribute attribute) {
        return docRepository.countByAttributeValues_AttributeEquals(attribute);
    }
}