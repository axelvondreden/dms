package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.TagHistory;
import com.dude.dms.backend.repositories.TagHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagHistoryService implements CrudService<TagHistory> {

    private final TagHistoryRepository tagHistoryRepository;

    @Autowired
    public TagHistoryService(TagHistoryRepository tagHistoryRepository) {
        this.tagHistoryRepository = tagHistoryRepository;
    }

    @Override
    public TagHistoryRepository getRepository() {
        return tagHistoryRepository;
    }
}
