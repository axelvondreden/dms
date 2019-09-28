package com.dude.dms.backend.service;

import com.dude.dms.app.security.SecurityUtils;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.data.entity.UserHistory;
import com.dude.dms.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        User user = CrudService.super.create(entity);
        User currentUser = findByLogin(SecurityUtils.getUsername()).orElse(user);
        userHistoryService.create(new UserHistory(user, currentUser, "Created", true, false, false));
        return user;
    }

    @Override
    public User save(User entity) {
        User before = load(entity.getId());
        User after = CrudService.super.save(entity);
        User currentUser = findByLogin(SecurityUtils.getUsername()).orElse(after);
        userHistoryService.create(new UserHistory(after, currentUser, before.diff(after), true, false, false));
        return after;
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    private static void throwIfDeletingSelf(User user) {
        if (SecurityUtils.getUsername() == null || SecurityUtils.getUsername().equalsIgnoreCase(user.getLogin())) {
            throw new RuntimeException(DELETING_SELF_NOT_PERMITTED);
        }
    }
}
