package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Tag;
import com.dude.dms.backend.data.entity.TagHistory;
import com.dude.dms.backend.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService extends HistoricalCrudService<Tag, TagHistory> {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public TagHistory createHistory(Tag entity, String text, boolean created, boolean edited, boolean deleted) {
        return new TagHistory(entity, text, created, edited, deleted);
    }

    @Override
    public TagRepository getRepository() {
        return tagRepository;
    }
}
