package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.UserOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOptionRepository extends JpaRepository<UserOption, Long> {

    Optional<UserOption> findByKey(String key);

}
