package com.dude.dms.backend.service;

import com.dude.dms.app.security.SecurityUtils;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.data.entity.UserHistory;
import com.dude.dms.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements CrudService<User> {

    private static final String DELETING_SELF_NOT_PERMITTED = "You cannot delete your own account";
    private final UserRepository userRepository;

    private final UserHistoryService userHistoryService;

    @Autowired
    public UserService(UserRepository userRepository, UserHistoryService userHistoryService) {
        this.userRepository = userRepository;
        this.userHistoryService = userHistoryService;
    }

    @Override
    public UserRepository getRepository() {
        return userRepository;
    }

    @Override
    public void delete(User entity) {
        throwIfDeletingSelf(entity);
        CrudService.super.delete(entity);
    }

    @Override
    public User create(User entity) {
        User currentUser = findByLogin(SecurityUtils.getUsername()).orElseThrow(() -> new RuntimeException("No User!"));
        userHistoryService.create(new UserHistory(entity, currentUser, "Created", true, false, false));
        return CrudService.super.create(entity);
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    private static void throwIfDeletingSelf(User user) {
        if (SecurityUtils.getUsername() == null || SecurityUtils.getUsername().equalsIgnoreCase(user.getLogin())) {
            throw new RuntimeException(DELETING_SELF_NOT_PERMITTED);
        }
    }

}
