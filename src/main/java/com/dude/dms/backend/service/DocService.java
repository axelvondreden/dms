package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.repositories.DocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocService implements CrudService<Doc> {

    private final DocRepository docRepository;

    @Autowired
    public DocService(DocRepository docRepository) {
        this.docRepository = docRepository;
    }

    public Page<Doc> findByTitleLikeIgnoreCase(Optional<String> title, Pageable pageable) {
        return title.isPresent() ? docRepository.findByTitleLikeIgnoreCase(title.get(), pageable) : docRepository.findAll(pageable);
    }

    public long countByTitleLikeIgnoreCase(Optional<String> title) {
        return title.map(docRepository::countByTitleLikeIgnoreCase).orElseGet(docRepository::count);
    }

    @Override
    public JpaRepository<Doc, Long> getRepository() {
        return docRepository;
    }

    public List<Doc> findAll() {
        return docRepository.findAll();
    }
}
