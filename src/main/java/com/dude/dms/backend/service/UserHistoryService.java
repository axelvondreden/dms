package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.UserHistory;
import com.dude.dms.backend.repositories.UserHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserHistoryService implements FilterableCrudService<UserHistory> {

    private final UserHistoryRepository userHistoryRepository;

    @Autowired
    public UserHistoryService(UserHistoryRepository userHistoryRepository) {
        this.userHistoryRepository = userHistoryRepository;
    }

    public Page<UserHistory> findAnyMatching(Optional<String> filter, Pageable pageable) {
        if (filter.isPresent()) {
            String repositoryFilter = '%' + filter.get() + '%';
            return userHistoryRepository.findByTextLikeIgnoreCase(repositoryFilter, pageable);
        } else {
            return find(pageable);
        }
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        if (filter.isPresent()) {
            String repositoryFilter = '%' + filter.get() + '%';
            return userHistoryRepository.countByTextLikeIgnoreCase(repositoryFilter);
        } else {
            return count();
        }
    }

    @Override
    public UserHistoryRepository getRepository() {
        return userHistoryRepository;
    }

    public Page<UserHistory> find(Pageable pageable) {
        return userHistoryRepository.findBy(pageable);
    }

    @Override
    public UserHistory save(UserHistory entity) {
        return userHistoryRepository.saveAndFlush(entity);
    }
}
