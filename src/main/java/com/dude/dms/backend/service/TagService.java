package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Tag;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagService implements FilterableCrudService<Tag> {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Page<Tag> findAnyMatching(Optional<String> filter, Pageable pageable) {
        if (filter.isPresent()) {
            String repositoryFilter = '%' + filter.get() + '%';
            return tagRepository.findByNameLikeIgnoreCase(repositoryFilter, pageable);
        } else {
            return find(pageable);
        }
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        if (filter.isPresent()) {
            String repositoryFilter = '%' + filter.get() + '%';
            return tagRepository.countByNameLikeIgnoreCase(repositoryFilter);
        } else {
            return count();
        }
    }

    public Page<Tag> find(Pageable pageable) {
        return tagRepository.findBy(pageable);
    }

    @Override
    public JpaRepository<Tag, Long> getRepository() {
        return tagRepository;
    }

    @Override
    public Tag save(Tag entity) {
        try {
            return FilterableCrudService.super.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("There is already a tag with that name.");
        }
    }
}
