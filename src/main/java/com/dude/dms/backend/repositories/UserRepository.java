package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    Page<User> findBy(Pageable pageable);

    Page<User> findByLoginLikeIgnoreCaseOrRoleLikeIgnoreCase(String loginLike, String roleLike, Pageable pageable);

    long countByLoginLikeIgnoreCaseOrRoleLikeIgnoreCase(String loginLike, String roleLike);
}
