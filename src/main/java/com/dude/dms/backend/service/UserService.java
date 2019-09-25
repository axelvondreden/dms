package com.dude.dms.backend.service;

import com.dude.dms.app.security.SecurityUtils;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements CrudService<User> {

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

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(User entity) {
        throwIfDeletingSelf(entity);
        CrudService.super.delete(entity);
    }

    private static void throwIfDeletingSelf(User user) {
        if (SecurityUtils.getUsername() == null || SecurityUtils.getUsername().equalsIgnoreCase(user.getLogin())) {
            throw new RuntimeException(DELETING_SELF_NOT_PERMITTED);
        }
    }

}
