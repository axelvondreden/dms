package com.dude.dms.backend.service;

import com.dude.dms.app.security.SecurityUtils;
import com.dude.dms.backend.data.entity.Tag;
import com.dude.dms.backend.data.entity.TagHistory;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService implements CrudService<Tag> {

    private final TagRepository tagRepository;

    private final TagHistoryService tagHistoryService;

    private final UserService userService;

    @Autowired
    public TagService(TagRepository tagRepository, TagHistoryService tagHistoryService, UserService userService) {
        this.tagRepository = tagRepository;
        this.tagHistoryService = tagHistoryService;
        this.userService = userService;
    }

    @Override
    public Tag create(Tag entity) {
        User currentUser = userService.findByLogin(SecurityUtils.getUsername()).orElseThrow(() -> new RuntimeException("No User!"));
        tagHistoryService.create(new TagHistory(entity, currentUser, "Created", true, false, false));
        return CrudService.super.create(entity);
    }

    @Override
    public TagRepository getRepository() {
        return tagRepository;
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

}
