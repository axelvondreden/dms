package com.dude.dms.backend.service;

import com.dude.dms.app.security.SecurityUtils;
import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.data.entity.DocHistory;
import com.dude.dms.backend.data.entity.User;
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

    private final DocHistoryService docHistoryService;

    private final UserService userService;

    @Autowired
    public DocService(DocRepository docRepository, DocHistoryService docHistoryService, UserService userService) {
        this.docRepository = docRepository;
        this.docHistoryService = docHistoryService;
        this.userService = userService;
    }

    @Override
    public JpaRepository<Doc, Long> getRepository() {
        return docRepository;
    }

    @Override
    public Doc create(Doc entity) {
        User currentUser = userService.findByLogin(SecurityUtils.getUsername()).orElseThrow(() -> new RuntimeException("No User!"));
        docHistoryService.create(new DocHistory(entity, currentUser, "Created", true, false, false));
        return CrudService.super.create(entity);
    }

    public Page<Doc> findByTitleLikeIgnoreCase(Optional<String> title, Pageable pageable) {
        return title.isPresent() ? docRepository.findByTitleLikeIgnoreCase(title.get(), pageable) : docRepository.findAll(pageable);
    }

    public long countByTitleLikeIgnoreCase(Optional<String> title) {
        return title.map(docRepository::countByTitleLikeIgnoreCase).orElseGet(docRepository::count);
    }

    public List<Doc> findAll() {
        return docRepository.findAll();
    }
}
