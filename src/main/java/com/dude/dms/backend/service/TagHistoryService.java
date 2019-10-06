package com.dude.dms.backend.service;

import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.data.history.TagHistory;
import com.dude.dms.backend.repositories.TagHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagHistoryService extends HistoryCrudService<Tag, TagHistory> {

    private final TagHistoryRepository tagHistoryRepository;

    @Autowired
    public TagHistoryService(TagHistoryRepository tagHistoryRepository) {
        this.tagHistoryRepository = tagHistoryRepository;
    }

    @Override
    public TagHistoryRepository getRepository() {
        return tagHistoryRepository;
    }

    @Override
    public List<TagHistory> getHistory(Tag entity) {
        return tagHistoryRepository.findByTag(entity);
    }
}
