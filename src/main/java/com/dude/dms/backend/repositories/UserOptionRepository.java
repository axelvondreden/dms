package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.data.entity.UserOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserOptionRepository extends JpaRepository<UserOption, Long> {

    Optional<UserOption> findByUserAndKey(User user, String key);

    List<UserOption> findByUser(User user);

}
