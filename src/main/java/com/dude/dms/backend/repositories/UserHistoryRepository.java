package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.data.entity.UserHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

    Page<UserHistory> findBy(Pageable pageable);

    Page<UserHistory> findByTextLikeIgnoreCase(String textLike, Pageable pageable);

    long countByTextLikeIgnoreCase(String textLike);
}
