package com.dude.dms.backend.service;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.repositories.AttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AttributeService extends CrudService<Attribute> {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(AttributeService.class);

    private final AttributeRepository attributeRepository;

    @Autowired
    public AttributeService(AttributeRepository attributeRepository) {
        this.attributeRepository = attributeRepository;
    }

    @Override
    public AttributeRepository getRepository() {
        return attributeRepository;
    }

    public Optional<Attribute> findByName(String name) {
        return attributeRepository.findByName(name);
    }

    public Set<Attribute> findByTag(Tag tag) {
        return attributeRepository.findByTag(tag);
    }

    public Set<Attribute> findByTagNot(Tag tag) {
        return attributeRepository.findByTagNot(tag);
    }

    @Override
    public Attribute create(Attribute entity) {
        if (findByName(entity.getName()).isPresent()) {
            LOGGER.showError("Attribute '" + entity.getName() + "' already exists!");
            return null;
        }
        return super.create(entity);
    }
}