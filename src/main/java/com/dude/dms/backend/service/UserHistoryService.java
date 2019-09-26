package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.UserHistory;
import com.dude.dms.backend.repositories.UserHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserHistoryService implements CrudService<UserHistory> {

    private final UserHistoryRepository userHistoryRepository;

    @Autowired
    public UserHistoryService(UserHistoryRepository userHistoryRepository) {
        this.userHistoryRepository = userHistoryRepository;
    }

    @Override
    public UserHistoryRepository getRepository() {
        return userHistoryRepository;
    }
}
