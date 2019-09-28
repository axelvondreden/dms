package com.dude.dms.backend.service;

import com.dude.dms.app.security.SecurityUtils;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.data.entity.UserHistory;
import com.dude.dms.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService extends HistoricalCrudService<User, UserHistory> {

    private static final String DELETING_SELF_NOT_PERMITTED = "You cannot delete your own account";
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserRepository getRepository() {
        return userRepository;
    }

    @Override
    public void delete(User entity) {
        throwIfDeletingSelf(entity);
        super.delete(entity);
    }

    @Override
    public UserHistory createHistory(User entity, User currentUser, String text, boolean created, boolean edited, boolean deleted) {
        return new UserHistory(entity, currentUser, text, created, edited, deleted);
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    private static void throwIfDeletingSelf(User user) {
        if (SecurityUtils.getUsername() == null || SecurityUtils.getUsername().equalsIgnoreCase(user.getLogin())) {
            throw new RuntimeException(DELETING_SELF_NOT_PERMITTED);
        }
    }

    @Override
    protected User getCurrentUser(User entity) {
        return findByLogin(SecurityUtils.getUsername()).orElse(entity);
    }
}
