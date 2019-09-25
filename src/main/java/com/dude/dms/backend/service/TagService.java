package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Tag;
import com.dude.dms.backend.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
    public JpaRepository<Tag, Long> getRepository() {
        return tagRepository;
    }

    public Page<Tag> find(Pageable pageable) {
        return tagRepository.findBy(pageable);
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }
}
