package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService implements FilterableCrudService<User> {

    private static final String DELETING_SELF_NOT_PERMITTED = "You cannot delete your own account";
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public Page<User> findAnyMatching(Optional<String> filter, Pageable pageable) {
        if (filter.isPresent()) {
            String repositoryFilter = '%' + filter.get() + '%';
            return userRepository.findByLoginLikeIgnoreCaseOrRoleLikeIgnoreCase(repositoryFilter, repositoryFilter, pageable);
        } else {
            return find(pageable);
        }
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        if (filter.isPresent()) {
            String repositoryFilter = '%' + filter.get() + '%';
            return userRepository.countByLoginLikeIgnoreCaseOrRoleLikeIgnoreCase(repositoryFilter, repositoryFilter);
        } else {
            return count();
        }
    }

    @Override
    public UserRepository getRepository() {
        return userRepository;
    }

    public Page<User> find(Pageable pageable) {
        return userRepository.findBy(pageable);
    }

    @Override
    public User save(User currentUser, User entity) {
        return userRepository.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public void delete(User currentUser, User entity) {
        throwIfDeletingSelf(currentUser, entity);
        FilterableCrudService.super.delete(currentUser, entity);
    }

    private static void throwIfDeletingSelf(User currentUser, User user) {
        if (currentUser.equals(user)) {
            throw new RuntimeException(DELETING_SELF_NOT_PERMITTED);
        }
    }

}
