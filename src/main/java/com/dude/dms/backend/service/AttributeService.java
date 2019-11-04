package com.dude.dms.backend.service;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.repositories.AttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public Attribute create(Attribute entity) {
        Optional<Attribute> current = findByName(entity.getName());
        if (current.isPresent()) {
            LOGGER.showError("Attribute '" + entity.getName() + "' already exists!");
            return current.get();
        }
        return super.create(entity);
    }
}