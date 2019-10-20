package com.dude.dms.backend.service;

import com.dude.dms.backend.data.history.TagHistory;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class TagService extends HistoricalCrudService<Tag, TagHistory> {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag create(Tag entity) {
        Optional<Tag> tag = tagRepository.findByName(entity.getName());
        return tag.orElseGet(() -> super.create(entity));
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

    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    public Set<Tag> findByPlainTextRule(PlainTextRule rule) {
        return tagRepository.findByPlainTextRules(rule);
    }

    public Set<Tag> findByRegexRule(RegexRule rule) {
        return tagRepository.findByRegexRules(rule);
    }
}