package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.data.entity.UserHistory;
import com.dude.dms.backend.repositories.UserHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserHistoryService implements HistoricalCrudService<User, UserHistory> {

    private final UserHistoryRepository userHistoryRepository;

    @Autowired
    public UserHistoryService(UserHistoryRepository userHistoryRepository) {
        this.userHistoryRepository = userHistoryRepository;
    }

    @Override
    public UserHistoryRepository getRepository() {
        return userHistoryRepository;
    }

    @Override
    public List<UserHistory> getHistory(User entity) {
        return userHistoryRepository.findByUser(entity);
    }
}
