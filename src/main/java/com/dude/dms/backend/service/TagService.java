package com.dude.dms.backend.service;

import com.dude.dms.backend.data.history.TagHistory;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<Tag> findTop10ByNameContaining(String name) {
        return tagRepository.findTop10ByNameContaining(name);
    }

    public long countByNameContaining(String name) {
        return tagRepository.countByNameContaining(name);
    }

    public List<Tag> findTop10ByNameContainingIgnoreCase(String name) {
        return tagRepository.findTop10ByNameContainingIgnoreCase(name);
    }

    public long countByNameContainingIgnoreCase(String name) {
        return tagRepository.countByNameContainingIgnoreCase(name);
    }
}