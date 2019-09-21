package com.dude.dms.spring.backend.repositories;

import com.dude.dms.spring.backend.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByName(String name);
}
