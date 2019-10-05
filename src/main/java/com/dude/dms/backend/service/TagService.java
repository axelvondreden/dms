package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Tag;
import com.dude.dms.backend.data.entity.TagHistory;
import com.dude.dms.backend.repositories.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagService extends HistoricalCrudService<Tag, TagHistory> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag create(Tag entity) {
        Optional<Tag> tag = tagRepository.findByName(entity.getName());
        if (tag.isPresent()) {
            LOGGER.warn("Tag with name '{}' already exists", entity.getName());
            return tag.get();
        }
        return super.create(entity);
    }

    @Override
    public TagHistory createHistory(Tag entity, String text, boolean created, boolean edited, boolean deleted) {
        return new TagHistory(entity, text, created, edited, deleted);
    }

    @Override
    public TagRepository getRepository() {
        return tagRepository;
    }

    public Optional<Tag> findById(long id) {
        return tagRepository.findById(id);
    }
}
