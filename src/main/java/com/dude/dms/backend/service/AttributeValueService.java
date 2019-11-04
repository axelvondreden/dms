package com.dude.dms.backend.service;

import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.data.docs.AttributeValue;
import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.repositories.AttributeValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AttributeValueService extends CrudService<AttributeValue> {

    private final AttributeValueRepository attributeValueRepository;

    @Autowired
    public AttributeValueService(AttributeValueRepository attributeValueRepository) {
        this.attributeValueRepository = attributeValueRepository;
    }

    @Override
    public AttributeValueRepository getRepository() {
        return attributeValueRepository;
    }

    public Optional<AttributeValue> findByDocAndAttribute(Doc doc, Attribute attribute) {
        return attributeValueRepository.findByDocAndAttribute(doc, attribute);
    }
}