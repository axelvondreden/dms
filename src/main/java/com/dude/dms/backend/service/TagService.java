package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Tag;
import com.dude.dms.backend.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService implements CrudService<Tag> {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public TagRepository getRepository() {
        return tagRepository;
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

}
