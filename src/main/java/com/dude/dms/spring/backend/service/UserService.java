package com.dude.dms.spring.backend.service;

import com.dude.dms.spring.backend.data.entity.User;
import com.dude.dms.spring.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements CrudService<User> {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserRepository getRepository() {
    return userRepository;
  }

  public User save(User currentUser, User entity) {
    return userRepository.saveAndFlush(entity);
  }
}
